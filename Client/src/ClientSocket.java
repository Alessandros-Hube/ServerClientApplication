import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Observable;

public class ClientSocket extends Observable implements Runnable{

	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private String buffer;

	private State clientState;

	/**
	 * return the local host address (IPv4 address), if not could be resolved into an address return 0.0.0.0 
     */
	public static String getHostAddress() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return "0.0.0.0";
		}
	}

	/**
	 * This method connect the client socket with the server.
	 * She initialized the socket with the host address and the port number, 
	 * the writer with the OutputStream from socket and the reader with the InputStream from socket.
	 * Is the socket connected call notify with ClientConnection State.
	 * 
	 * @param host is the host address from the server
	 * @param port is the port number from the server
	 */
	public void connect(String host, int port) {
		try {
			socket = new Socket(InetAddress.getByName(host), port);
			writer = new PrintWriter(socket.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			if (socket.isConnected()) {
				notify(State.ClientConnection);
			}
		} catch (UnknownHostException e) {
			System.err.println("IP-address(IPv4!!!) not found");
		} catch (IOException e) {
			System.err.println("I/O error by try the connection");	
			disconnect();
		}
	}

	/**
	 * This method disconnect the socket from server.
	 * He closes the writer, reader and the socket.
	 * Finally, call to notify with ClientDisconnection State.
	 */
	public void disconnect() {
		if(writer != null) {
			writer.close();
		}
		try {
			if(reader != null) {
				reader.close();
			}
		} catch(IOException e) {
			System.err.println("The buffered reader is closed");
		} finally {
			try {
				if(reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				System.err.println("Method disconnect finally-Block: The buffered reader is closed");
			}
		}

		try {
			if(socket != null) {
				socket.close();
				notify(State.ClientDisconnection);
			}
		} catch (IOException e) {
			System.err.println("The socket is closed");
		}
	}
	
	/**
	 * This method set the new clientState value and notify the observers above the change
	 * 
	 * @param state is the new clientState value
	 */
	private void notify(State state) {
		clientState = state;
		setChanged();
		notifyObservers();
	}

	/**
	 * This method writes the message to the server.
	 * 
	 * @param message is the message that is written 
	 */
	public void write(String message) {
		writer.println(message);
	}

	/**
	 * return the current clientState
     */
	public State getClientState() {
		return clientState;
	}

	/**
	 * return the buffer
     */
	public String getBuffer() {
		return buffer;
	}
	
	@Override
    public void run() {
		try {
			String m;
			while ((m = reader.readLine()) != null) {
				buffer = m;
				notify(State.NewMessage);
			}	
		} catch (IOException e) {
			System.err.println("Socket is close");
		} finally {
			writer.close();
			try {
				if(reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				System.err.println("Method run finally-Block: The buffered reader is closed");
			}
		}
	}
}
