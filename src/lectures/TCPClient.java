package lectures;
import java.io.*;
import java.net.*;

public class TCPClient {

	public static void main(String argv[]) throws Exception {
		String sentence;
		String modifiedSentence;
		while(true) {
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			sentence = inFromUser.readLine();
			if(sentence.equalsIgnoreCase("bye") || sentence.equalsIgnoreCase("quit"))
				break;
			Socket clientSocket = new Socket("localhost", 6000);
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			outToServer.writeBytes(sentence + '\n');
			modifiedSentence = inFromServer.readLine();
			System.out.println("FROM SERVER: " + modifiedSentence);
			clientSocket.close();
		}
	}
}
