package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import data.Message;

public class Client {
	
	public static int clientCount = 1;
	private boolean offline;
	private String userName;
	private String password;
	private int connectedServerPort;
	private HashMap<String, ArrayList<Message>> history;
	private Queue<Message> recieved;
	private ExecutorService pool;

	public Client(String userName, String password, int connectedServerPort) throws ClassNotFoundException, IOException {
		this.userName = userName;
		this.password = password;
		this.connectedServerPort = connectedServerPort;
		this.history = new HashMap<String, ArrayList<Message>>();
		recieved = new LinkedList<>();
		if(!join(userName, password)) {
			System.out.println("Invalid username or password");
		} else {
			offline = false;
			init();
			clientCount++;
		}
	}
	
	public void init() {
		pool = Executors.newFixedThreadPool(1);
		try {
			pool.execute(new Fetcher(this));
		} catch(Exception e) {
			System.out.println("Error in Client @" + userName);
		}
	}
	
	private static class Fetcher implements Runnable {
		private Client client;
		
		public Fetcher(Client client) {
			this.client = client;
		}

		@Override
		public void run() {
			while (true) {
				if(client.isOffline()) {
					client.pool.shutdown();
					return;
				}
				try {
					Thread.sleep(1000);
					client.fetch();
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Error in Client @" + client.getUserName());
				}
			}
		}
	}
	
	public boolean join(String username, String password) throws IOException, ClassNotFoundException {
		int clientId = -1;
		Socket clientSocket = new Socket("localhost", connectedServerPort);
		
		ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
		ObjectInputStream inFromServer = new ObjectInputStream(clientSocket.getInputStream());	
		
		outToServer.writeObject(new Message(username + " " + password, 3));
		
		Message recieved = (Message) inFromServer.readObject();;
		
		clientId = Integer.parseInt(recieved.getContent());
		
		clientSocket.close();
		
		return clientId > -1;
	}
	
	public void fetch() throws IOException, ClassNotFoundException {
		Message receivedMessage;
		Socket clientSocket = new Socket("localhost", connectedServerPort);
		
		ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
		ObjectInputStream inFromServer = new ObjectInputStream(clientSocket.getInputStream());
		
		outToServer.writeObject(new Message(userName, "", 0));

		receivedMessage = (Message) inFromServer.readObject();
		if(receivedMessage.getType() != 4) {
			if(!history.containsKey(receivedMessage.getTo()))
				history.put(receivedMessage.getTo(), new ArrayList<>());
			history.get(receivedMessage.getTo()).add(receivedMessage);
			System.out.println(receivedMessage);
			System.out.println("Adding Message");
			recieved.add(receivedMessage);
		}
		clientSocket.close();
	}

	public void send(String from, String to, int ttl, String content) throws IOException, ClassNotFoundException {
		Message msgToBeSent = new Message(from, to, content, ttl, 1);
		
		Socket clientSocket = new Socket("localhost", connectedServerPort);
		ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
		
		outToServer.writeObject(msgToBeSent);
		
		if(!history.containsKey(msgToBeSent.getFrom()))
			history.put(msgToBeSent.getFrom(), new ArrayList<>());
		history.get(msgToBeSent.getFrom()).add(msgToBeSent);
		clientSocket.close();
	}
	
	public String getMemberList() throws IOException, ClassNotFoundException {
		Message receivedMessage;
		Socket clientSocket = new Socket("localhost", connectedServerPort);
		
		ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
		ObjectInputStream inFromServer = new ObjectInputStream(clientSocket.getInputStream());
		
		outToServer.writeObject(new Message(2));
		
		receivedMessage = (Message) inFromServer.readObject();
		
		clientSocket.close();
		
		return receivedMessage.getContent();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getConnectedServerPort() {
		return connectedServerPort;
	}

	public void setConnectedServerPort(int connectedServerPort) {
		this.connectedServerPort = connectedServerPort;
	}

	public HashMap<String, ArrayList<Message>> getHistory() {
		return history;
	}

	public void setHistory(HashMap<String, ArrayList<Message>> history) {
		this.history = history;
	}
	
	public boolean isOffline() {
		return offline;
	}

	public void setOffline(boolean offline) {
		this.offline = offline;
	}

	public Queue<Message> getRecieved() {
		return recieved;
	}

	public void setRecieved(Queue<Message> recieved) {
		this.recieved = recieved;
	}

	public static void main(String[] args) throws Exception {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		String username = bf.readLine();
		String password = bf.readLine();
		int serverPort = Integer.parseInt(bf.readLine());
		Client c = new Client(username, password, serverPort);
		System.out.println(username);
		System.out.println("-------------");
		while(true) {
			System.out.println("Wating to send...");
			String to = bf.readLine();
			if(to.equalsIgnoreCase("quit") || to.equalsIgnoreCase("bye")) {
				c.setOffline(true);
			}
			if(c.isOffline()) return;
			String content = bf.readLine();
			c.send(c.getUserName(), to, 2, content);
		}
	}
	
}
