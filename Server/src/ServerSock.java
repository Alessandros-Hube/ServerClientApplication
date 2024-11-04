import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerSock extends Observable implements Runnable{

	private ServerSocket server;
	private final ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();

	private ExecutorService threadPool;

	private State sockState;

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
	 * This Method bound the server to the port number and call notify with ServerBound
	 * @param port is the port number he will bound the server
	 */
	public void bonding(int port) {
		try {
			server = new ServerSocket(port);	
			if(server.isBound()) {
				threadPool = Executors.newCachedThreadPool();
				notify(State.ServerBound);
			}
		} catch (IOException e) {
			System.err.println("Connecting error change Port");
		}
	}

	/**
	 * This method disconnect all clients from the server, shutdown the threadPool and call notify with ServerClose 
	 */
	public void closeServerSock() {
		try {

			if(server != null) {
				for(int i = 0; i < clients.size(); i++) {
					clients.get(i).close();
					clients.remove(i);
				}
				clients.clear();
				server.close();
				if(threadPool != null) {
					threadPool.shutdownNow();
				}
				threadPool = null;
				server = null;
				notify(State.ServerClose);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method set the new sockState value and notify the observers above the change
	 * 
	 * @param state is the new sockState value
	 */
	private void notify(State state) {
		sockState = state;
		setChanged();
		notifyObservers();
	}

	/**
	 * This method writes the message to all clients.
	 * 
	 * @param message is the message that is written 
	 */
	public void write(String message) {
        for (ClientHandler client : clients) {
            client.write(message);
        }
	}

	/**
	 * This method writes the message to all clients without the client with the id outOf.
	 * 
	 * @param message is the message that is written 
	 * @param outOf is the id he not receives the message
	 */
	public void writeWithOutOf(String message, int outOf) {
		for (int i = 0; i < clients.size(); i++) {
			if(i != outOf) {
				clients.get(i).write(message);
			}
		}
	}

	/**
	 * remove the client with the id from the client list
	 * @param id is the id from the remove client
	 */
	public void removeClient(int id) {
		clients.get(id).close();
		clients.remove(id);
	}

	/**
	 * return the current sockState
     */
	public State getSockState() {
		return sockState;
	}

	/**
	 * get the ClientHandler from the index
	 * @param index is the index from the ClientHandler he will return 
	 * @return is an Object for the type ClientHandler
	 */
	public ClientHandler getClients(int index) {
		return clients.get(index);
	}

	/**
	 * return the size from clients
     */
	public int getClientListSize() {
		return clients.size();
	}

	@Override
    public void run() {
		try {
			Socket s;
			while(((s = server.accept()) != null)) {
				clients.add(new ClientHandler(s, clients.size()));
				threadPool.execute(clients.get(clients.size()-1));
				notify(State.NewClient);
			}
		} catch (IOException e) {
			System.err.println("Server is close!");
		} 
	}
}
