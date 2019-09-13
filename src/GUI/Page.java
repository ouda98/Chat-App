package GUI;

import java.awt.EventQueue;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JTextField;

import data.Message;
import network.Client;

import javax.swing.JComboBox;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JTextArea;

public class Page {

	JFrame frame1;
	JComboBox<String> comboBox;
	
	String userName;
	String password;
	private JTextField textField;
	Client c;
	private JTextArea textArea;

	public Page(Client c) {
		this.c = c;
		initialize();
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
 		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Page window = new Page();
					window.frame1.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Page() {
		initialize();
	}
	
	public void initRefresher() {
		ExecutorService pool = Executors.newFixedThreadPool(1);
		try {
			pool.execute(new Refresher(c, textArea, this));
		} catch(Exception e) {
			System.out.println("Error in Client @" + userName);
		}
	}
	
	private static class Refresher implements Runnable {
		private Client client;
		private JTextArea textArea;
		private Page page;
		
		public Refresher(Client client, JTextArea textArea, Page page) {
			this.client = client;
			this.textArea = textArea;
			this.page = page;
		}

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(1000);
					if(client.getRecieved().size() > 0) {
						Message msg = client.getRecieved().poll();
						textArea.setText(textArea.getText().toString() + (String) "\nFrom <" + msg.getFrom()+"> : "+ msg.getContent());
					}
					//Update members
					String a = "";
					try {
						a = client.getMemberList();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Why?");
					}
					String[] users = a.split("\n");
					if(users.length > page.comboBox.getItemCount()) {
						page.comboBox.removeAllItems();
						for(String s : users)
							page.comboBox.addItem(s);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Error in Client @" + client.getUserName());
				}
			}
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame1 = new JFrame(c.getUserName());
		frame1.setBounds(100, 100, 500, 600);
		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame1.getContentPane().setLayout(null);

		textField = new JTextField();
		textField.setBounds(111, 516, 249, 22);
		frame1.getContentPane().add(textField);
		textField.setColumns(10);
		
		String a = "";
		try {
			a = c.getMemberList();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Why?");
		}
		String[] users = a.split("\n");
		System.out.println();
		System.out.println(Arrays.toString(users));
		System.out.println(a);
		comboBox = new JComboBox<>(users); // threads
		comboBox.setBounds(10, 517, 91, 20);
		frame1.getContentPane().add(comboBox);

		textArea = new JTextArea();
		textArea.setBounds(10, 11, 449, 471);
		frame1.getContentPane().add(textArea);
		JButton btnSend = new JButton("SEND");
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String a=textField.getText().toString();
				String value = comboBox.getSelectedItem().toString();
				textArea.setText(textArea.getText().toString()+"\nTo <"+value+"> : "+a);
				try {
					c.send(c.getUserName(), value, 2, a);
				} catch (ClassNotFoundException | IOException e1) {
					JOptionPane.showMessageDialog(null, "Why?");
					e1.printStackTrace();
				}
				textField.setText("");
			
			}
		});
		
		btnSend.setBounds(370, 516, 89, 23);
		frame1.getContentPane().add(btnSend);
		initRefresher();
	}
}
