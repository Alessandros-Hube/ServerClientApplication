import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observable;

public class ClientHandler extends Observable implements Runnable{

	private final Socket client;
	private final PrintWriter writer;
	private final BufferedReader reader;
	private boolean isSocketClose;
	private String buffer;

	private State handlerState;

	private final int id;
	private String name;
	private final String ip;

	/**
	 * Initializes the attributes of this class
	 * 
	 * @param clientSocket is the socket of the client
	 * @param _id is the id from the position in the client list from server
	 * @throws IOException if an I/O error occurs when creating the output/input stream or if the socket is not connected.
	 */
	public ClientHandler(Socket clientSocket, int _id) throws IOException {
		client = clientSocket;
		id = _id;
		ip = clientSocket.getInetAddress().getHostAddress();
		writer = new PrintWriter(client.getOutputStream(), true);
		reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
		isSocketClose = false;
	}

	@Override
    public void run() {
		try {
			String m;
			while ((!isSocketClose)&&((m = reader.readLine()) != null)) {
				buffer = m;
				handlerState = State.NewMessage;
				setChanged();
				notifyObservers(id);
			}
		} catch (IOException e) {
			System.err.println("Socket is close");
			close();
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

	/**
	 * This method close the socket.
	 * He closes the writer, reader and the socket.
	 */
	public void close() {
		isSocketClose = true;
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
				System.err.println("Method close finally-Block: The buffered reader is closed");
			}
		}

		try {
			if(client != null) {
				client.close();
			}
		} catch (IOException e) {
			System.err.println("The socket is closed");
		}
	}

	/**
	 * This method writes the message to the client.
	 * 
	 * @param message is the message that is written 
	 */
	public void write(String message) {
		writer.println(message);
	}

	/**
	 * return the name
     */
	public String getName() {
		return name;
	}

	/**
	 * set the name
     */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * return the buffer
     */
	public String getBuffer() {
		return buffer;
	}

	/**
	 * return the ip
     */
	public String getIP() {
		return ip;
	}

	/**
	 * return the current handlerState
     */
	public State getHandlerState() {
		return handlerState;
	}

}
