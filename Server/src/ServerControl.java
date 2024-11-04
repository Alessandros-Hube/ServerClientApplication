import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerControl extends Observable implements Observer{

	private final ServerSock server = new ServerSock();

	private ExecutorService listenThread = Executors.newFixedThreadPool(1);

	private State controlState;

	private String buffer;

	private int clientID;

	/**
	 * registers itself as an observer at the server
	 */
	public ServerControl() {
		server.addObserver(this);
	}

	/**
	 * This method call the bonding method from server with the port number
	 * 
	 * @param port is the bonding port number
	 */
	public void startServer(int port) {
		server.bonding(port);
	}

	/**
	 * This method called the write method form server with the message: "Server is Offline" and the prefix 0x03.
	 * Finally called the closeServerSock method from server
	 */
	public void stopServer() {
		server.write(0x03+";"+"Server is offline");
		server.closeServerSock();
	}

	/**
	 * This method handles the prefixes in the reception message.
	 */
	public void controller() {
		String tempBuffer = server.getClients(clientID).getBuffer();
		String[] bufferSplit = tempBuffer.split(";");
		int command = -1;
		
		if (bufferSplit.length >= 2) {
			command = Integer.decode(bufferSplit[0]);
		}
		switch (command) {
		case 0x01:
			server.getClients(clientID).setName(bufferSplit[1]);//set the receiving name in a client
			server.getClients(clientID).write(0x02+";"+"Server writes: Welcome");//write the client a welcome message
			//notify the Observer about the new client his name and IP address
			controlState = State.NewClient;
			setChanged();
			notifyObservers(bufferSplit[1]+";"+server.getClients(clientID).getIP());
			buffer = server.getClients(clientID).getName()+" joined the server.";
			server.writeWithOutOf(0x02+";"+buffer, clientID);//send all clients the name of the new client
			notify(State.NewMessage);
			break;
		case 0x02://put message in the buffer, send all other clients the buffer and called notify with NewMessage
			buffer = bufferSplit[1];
			server.writeWithOutOf(0x02+";"+buffer, clientID);
			notify(State.NewMessage);
			break;
		case 0x03://put message in the buffer, remove the client from server, send all clients the buffer and call notify with NewMessage
			buffer = bufferSplit[1];
			server.removeClient(clientID);
			server.write(buffer);
			notify(State.NewMessage);
			//notify the Observer which client is offline
			controlState = State.ClientOffline;
			setChanged();
			notifyObservers(clientID);
			break;
		default://send error message to the client and remove this client
			buffer="Connection error";
			server.getClients(clientID).write(buffer);
			server.removeClient(clientID);
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
	 * This method modify the message with the prefix 0x02.
	 * Finally called the write method from socket with the message 
	 * 
	 * @param message is the message that is written 
	 */
	public void write(String message) {
		server.write(0x02+";"+"Server writes: "+message);
	}

	public void removeClient(int id) {
		server.getClients(id).write(0x03+";"+"Server kicked you out");
		server.writeWithOutOf(0x02+";"+"Server threw out "+server.getClients(id).getName()+".",id);
		server.removeClient(id);
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
		State state;
		if (arg == null) {
			state = server.getSockState();
		}else {
			state = server.getClients(Integer.parseInt(arg.toString())).getHandlerState();
		}

		switch(state) {
		case ServerBound://initialize the listenThread and called execute with server and call notify with ServerBound
			listenThread = Executors.newFixedThreadPool(1);
			listenThread.execute(server);
			notify(state);
			break;
		case NewClient://registers itself as an observer at the last client from the client list
			server.getClients(server.getClientListSize()-1).addObserver(this);
			break;
		case NewMessage://set the clientID and call controller
            assert arg != null;
            clientID = Integer.parseInt(arg.toString());
			controller();
			break;
		case ServerClose://shutdown the listenThread and call notify with ServerClose.
			if(listenThread != null) {
				listenThread.shutdownNow();
			}
			listenThread = null;
			notify(state);
			break;
		default://print a error message
			System.err.println(state+" is a unknown sockState/handlerState state");
			break;
		}
	}

}
