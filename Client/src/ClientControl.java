import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientControl extends Observable implements Observer{

	private final ClientSocket socket = new ClientSocket();

	private ExecutorService readThread;

	private State controlState;

	private String buffer;

	private String userName;

	/**
	 * registers itself as an observer at the socket
	 */
	public ClientControl() {
		socket.addObserver(this);
	}

	/**
	 * This method set the value of the userName and 
	 * call the connect method from socket with the host address and port number.
	 * 
	 * @param host is the host address from the server
	 * @param port is the port number from the server
	 * @param name is the value for the userName
	 */
	public void startSocket(String host,  int port, String name) {
		userName = name;
		socket.connect(host, port);
	}

	/**
	 * This method called the write method form socket with the message: "he(userName) is Offline" and the prefix 0x03.
	 * Finally called the disconnect method from socket
	 */
	public void stopSocket() {
		socket.write(0x03+";"+userName+" is offline");
		socket.disconnect();
	}

	/**
	 * This method handles the prefixes in the reception message.
	 */
	private void controller() {
		String buffer = socket.getBuffer();

		String[] bufferSplit = buffer.split(";");
		int command = -1;
		
		if (bufferSplit.length >= 2) {
			command = Integer.decode(bufferSplit[0]);
		}
		
		switch (command) {
		case 0x02://put message in the buffer and called notify with NewMessage
			this.buffer = bufferSplit[1];
			notify(State.NewMessage);
			break;
		case 0x03://put message in the buffer, disconnect the socket and called notify with NewMessage
			this.buffer = bufferSplit[1];
			socket.disconnect();
			notify( State.ServerOffline);
			break;
		default://send error message to the server, called notify with NewMessage and disconnect the socket
			this.buffer = "Connection error";
			write(this.buffer);
			notify(State.NewMessage);
			socket.disconnect();
			break;
		}

	}

	/**
	 * This method set the new controlState value and notify the observers above the change
	 * 
	 * @param state is the new controlState value
	 */
	private void notify(State state) {
		controlState = state;
		setChanged();
		notifyObservers();
	}

	/**
	 * This method modify the message with the prefix 0x02 and the userName.
	 * Finally called the write method from socket with the message 
	 * 
	 * @param message is the message that is written 
	 */
	public void write(String message) {
		socket.write(0x02+";"+userName+" writes: "+message);
	}

	/**
	 * return the buffer
     */
	public String getBuffer() {
		return buffer;
	}

	/**
	 * return the current controlState
     */
	public State getControlState() {
		return controlState;
	}

	@Override
    public void update(Observable o, Object arg) {
		State state = socket.getClientState();

		switch(state) {
		case ClientConnection://write the userName with prefix 0x01, initialize the readThread and called execute with socket and call notify with ClientConnection 
			socket.write("0x01;"+userName);
			readThread = Executors.newFixedThreadPool(1);
			readThread.execute(socket);
			notify(State.ClientConnection);
			break;
		case ClientDisconnection://shutdown the readThread and call notify with ClientDisconnection.
			if(readThread != null) {
				readThread.shutdownNow();
			}
			readThread = null;
			notify(State.ClientDisconnection);
			break;
		case NewMessage://call controller
			controller();
			break;
		default://print a error message
			System.err.println(state+" is a unknown clientSocket state");
			break;
		}

	}

}
