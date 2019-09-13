package lectures;
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CapitalizeServer {
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
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				out.println(in.readLine().toUpperCase());
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
