package test;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import data.Message;

public class ThreadTest {
	public static void main(String[] args) throws Exception {
		System.out.println("The server is Online");
		ExecutorService pool = Executors.newFixedThreadPool(20);
		int clientNumber = 0;
		try (ServerSocket listener = new ServerSocket(6000)) {
			while (true) {
				pool.execute(new Capitalizer(listener.accept(), clientNumber++));
			}
		} catch(Exception e) {
			System.out.println("Error in Server");
		}
	}

	private static class Capitalizer implements Runnable {
		private Socket socket;
		private int clientNumber;

		public Capitalizer(Socket socket, int clientNumber) {
			this.socket = socket;
			this.clientNumber = clientNumber;
			System.out.println("New client #" + clientNumber + "connected at " + socket);
		}

		public void run() {
			Message clientMessage;
			Message capitalizedMessage;
			try {
				ObjectOutputStream outToClient = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inFromClient = new ObjectInputStream(socket.getInputStream());
				clientMessage = (Message)inFromClient.readObject();
				capitalizedMessage = new Message(clientMessage.getFrom(), clientMessage.getTo(),
						clientMessage.getContent().toUpperCase(), clientMessage.getTtl(), 1);
				outToClient.writeObject(capitalizedMessage);
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
}

