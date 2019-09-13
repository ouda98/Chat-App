package GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import network.Client;

import javax.swing.JButton;
import javax.swing.JComboBox;

import java.awt.Color;

import javax.swing.JTextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CHAT {

	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CHAT window = new CHAT();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public CHAT() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frame = new JFrame();
		frame.setBounds(100, 100, 500, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblAboamo = new JLabel("ABO-3amo");
		lblAboamo.setBackground(Color.YELLOW);
		lblAboamo.setFont(new Font("Snap ITC", Font.BOLD, 32));
		lblAboamo.setBounds(131, 33, 203, 50);
		frame.getContentPane().add(lblAboamo);

		JLabel lblUserName = new JLabel("User Name");
		lblUserName.setFont(new Font("Tahoma", Font.PLAIN, 23));
		lblUserName.setBounds(33, 150, 131, 29);
		frame.getContentPane().add(lblUserName);

		textField = new JTextField();
		textField.setBounds(174, 150, 177, 29);
		frame.getContentPane().add(textField);
		textField.setColumns(10);

		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(174, 226, 177, 29);
		frame.getContentPane().add(textField_1);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 23));
		lblPassword.setBounds(33, 226, 131, 29);
		frame.getContentPane().add(lblPassword);
		String[] servers = { "Server 1", "Server 2" };
		JComboBox<String> comboBox = new JComboBox<>(servers);
		comboBox.setBounds(174, 267, 108, 20);
		frame.getContentPane().add(comboBox);

		JLabel lblChooseServer = new JLabel("Choose Server");
		lblChooseServer.setBounds(43, 266, 95, 22);
		frame.getContentPane().add(lblChooseServer);

		JButton btnChat = new JButton("Chat");
		btnChat.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				String userName = textField.getText().toString();
				String password = textField_1.getText().toString();
				String value = comboBox.getSelectedItem().toString();
				if (!userName.equals("") && !password.equals("")) {

					int x = 6001;
					if (value.contentEquals("Server 2"))
						x = 6002;
					System.out.println(userName + "   " + password + "   " + x);
					try {

						Client c = new Client(userName, password, x);
						// Page user = new Page(userName,password);
						Page user = new Page(c);
						user.frame1.setVisible(true);
						frame.setVisible(false);

					} catch (Exception m) {
						JOptionPane.showMessageDialog(null, "RUN SERVERS");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Either USERNAME OR PASSWORD IS EMPTY");
					frame.setVisible(false);
					frame.dispose();
					System.exit(0);

				}

			}
		});
		btnChat.setFont(new Font("Tempus Sans ITC", Font.BOLD | Font.ITALIC, 24));
		btnChat.setBounds(371, 311, 103, 39);
		frame.getContentPane().add(btnChat);

	}

}
