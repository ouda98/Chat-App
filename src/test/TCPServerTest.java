package test;
import java.io.*;
import java.net.*;

import data.Message;

public class TCPServerTest {
	public static void main(String argv[]) throws Exception {
		Message clientMessage;
		Message capitalizedMessage;
		@SuppressWarnings("resource")
		ServerSocket welcomeSocket = new ServerSocket(6000);
		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			ObjectOutputStream outToClient = new ObjectOutputStream(connectionSocket.getOutputStream());
			ObjectInputStream inFromClient = new ObjectInputStream(connectionSocket.getInputStream());
			clientMessage = (Message)inFromClient.readObject();
			capitalizedMessage = new Message(clientMessage.getFrom(), clientMessage.getTo(),
					clientMessage.getContent().toUpperCase(), clientMessage.getTtl(), 1);
			outToClient.writeObject(capitalizedMessage);
		}
	}
}