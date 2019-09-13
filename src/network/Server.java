package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import data.Message;

public class Server {

	private int portNumber;
	private HashMap<String, String> connectedClients;
	private int connectedServerPort;
	private HashMap<String, Queue<Message>> unreadMessages;

	public Server(int portNumber, int connectedServerPort) {
		this.portNumber = portNumber;
		this.connectedClients = new HashMap<String, String>();
		this.connectedServerPort = connectedServerPort;
		this.unreadMessages = new HashMap<String, Queue<Message>>();
		init();
	}
	
	public Server(int portNumber, HashMap<String, String> connectedClients, int connectedServerPort,
			HashMap<String, Queue<Message>> unreadMessages) {
		this.portNumber = portNumber;
		this.connectedClients = connectedClients;
		this.connectedServerPort = connectedServerPort;
		this.unreadMessages = unreadMessages;
		init();
	}
	
	public void init() {
		ExecutorService pool = Executors.newFixedThreadPool(20);
		int clientNumber = 0;
		try (ServerSocket listener = new ServerSocket(portNumber)) {
			while (true) {
				pool.execute(new Responser(this, listener.accept(), clientNumber++));
			}
		} catch(Exception e) {
			System.out.println("Error in Server");
		}
	}
	
	private static class Responser implements Runnable {
		private Server host;
		private Socket socket;
		private int clientNumber;
		
		public Responser(Server host, Socket socket, int clientNumber) {
			this.host = host;
			this.socket = socket;
			this.clientNumber = clientNumber;
			System.out.println("New client #" + clientNumber + "connected at " + socket);
		}

		public void run() {
			Message clientMessage;
			Message serverMessage = null;
			try {
				ObjectOutputStream outToClient = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inFromClient = new ObjectInputStream(socket.getInputStream());
				
				clientMessage = (Message)inFromClient.readObject();
				int type = clientMessage.getType();
				
				if(type == 0) { //fetch
					if(!host.unreadMessages.get(clientMessage.getFrom()).isEmpty())
						serverMessage = host.unreadMessages.get(clientMessage.getFrom()).poll();
					else
						serverMessage = new Message(4);
				} else if(type == 1) { // send
					if(host.unreadMessages.containsKey(clientMessage.getTo())) {
						host.unreadMessages.get(clientMessage.getTo()).add(clientMessage);
					} else {
						if(clientMessage.getTtl() > 0) {
							host.send(clientMessage.getFrom(), clientMessage.getTo(), 
									clientMessage.getTtl() - 1, clientMessage.getContent(), host.connectedServerPort); 
						}
					}
				} else if(type == 2) { // getMemberList
					String members = host.getMemberList();
					serverMessage = new Message(members, 0);
				} else if(type == 3) { // Join
					StringTokenizer st = new StringTokenizer(clientMessage.getContent());
					int clientId = host.joinRespose(st.nextToken(), st.nextToken()) ? 1 : -1;
					String toBeSent = clientId + "";
					serverMessage = new Message(toBeSent, 0);
				} else if(type == 5) {
					String members = host.getMemberListLast();
					serverMessage = new Message(members, 0);
				}
				outToClient.writeObject(serverMessage);
				
			} catch (Exception e) {
				System.out.println("Error handling client #" + clientNumber);
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
				}
				System.out.println("Connection with client # " + clientNumber + " closed");
			}
		}
	}
	
	public boolean joinRespose(String userName, String password) throws ClassNotFoundException, IOException {
		if(connectedClients.containsKey(userName)) {
			if(connectedClients.get(userName).equals(password)) {
				System.out.println(userName+ " has logged in");
				return true;
			} else {
				System.out.println("Wrong password");
				return false;
			}
		}
		//Add predicate response from the other server to check if username exists
		String other = getMemberListFromServer();
		StringTokenizer st = new StringTokenizer(other, "\n");
		while(st.hasMoreTokens()) {
			if(userName.equals(st.nextToken()))
				return false;
		}
		addClient(userName, password);
		System.out.println(userName + " just signed up!");
		return true;
	}
	
	
	public void addClient(String userName, String password) {
		connectedClients.put(userName, password);
		unreadMessages.put(userName, new LinkedList<>());
	}
	
	public String getMemberList() throws ClassNotFoundException, IOException {
		String members = "";
		for(String str : connectedClients.keySet())
			members += str + "\n";
		members += getMemberListFromServer() + "\n";
		return members;
	}
	
	public String getMemberListLast() throws ClassNotFoundException, IOException {
		String members = "";
		for(String str : connectedClients.keySet())
			members += str + "\n";
		return members;
	}
	
	public String getMemberListFromServer() throws IOException, ClassNotFoundException {
		Message receivedMessage;
		Socket askSocket = new Socket("localhost", connectedServerPort);
		
		ObjectOutputStream outToServer = new ObjectOutputStream(askSocket.getOutputStream());
		ObjectInputStream inFromServer = new ObjectInputStream(askSocket.getInputStream());
		
		outToServer.writeObject(new Message(5));
		
		receivedMessage = (Message) inFromServer.readObject();
		
		askSocket.close();
		
		return receivedMessage.getContent();
	}
	
	public void send(String from, String to, int ttl, String content, int port) throws IOException, ClassNotFoundException {
		Message msgToBeSent = new Message(from, to, content, ttl, 1);
		
		Socket clientSocket = new Socket("localhost", port);
		ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
		
		outToServer.writeObject(msgToBeSent);
		
		clientSocket.close();
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public HashMap<String, String> getConnectedClients() {
		return connectedClients;
	}

	public void setConnectedClients(HashMap<String, String> connectedClients) {
		this.connectedClients = connectedClients;
	}

	public int getConnectedServerPort() {
		return connectedServerPort;
	}

	public void setConnectedServers(int connectedServerPort) {
		this.connectedServerPort = connectedServerPort;
	}

	public HashMap<String, Queue<Message>> getUnreadMessages() {
		return unreadMessages;
	}

	public void setUnreadMessages(HashMap<String, Queue<Message>> unreadMessages) {
		this.unreadMessages = unreadMessages;
	}

	
	public static void main(String[] args) throws Exception {
		new Server(6001, 6002);
//		new Server(6002, 6001);
		
	}
}


