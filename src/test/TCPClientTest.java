package test;
import java.io.*;
import java.net.*;

import data.Message;

public class TCPClientTest {

	public static void main(String argv[]) throws Exception {
		String sentence;
		Message modifiedMessage;
		while(true) {
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			sentence = inFromUser.readLine();
			if(sentence.equalsIgnoreCase("bye") || sentence.equalsIgnoreCase("quit"))
				break;
			Socket clientSocket = new Socket("localhost", 6000);
			ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
			ObjectInputStream inFromServer = new ObjectInputStream(clientSocket.getInputStream());
			Message msg = new Message("", "", sentence, 5, 1);
			outToServer.writeObject(msg);
			modifiedMessage = (Message)inFromServer.readObject();
			System.out.println("FROM SERVER: ");
			System.out.println(modifiedMessage);
			clientSocket.close();
		}
	}
}
