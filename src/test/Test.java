package test;


import network.Client;

public class Test {
	
	public static void main(String[] args) throws Exception {
		String username1 = "abc";
		String password1 = "123";
		Client c1 = new Client(username1, password1, 6001);
		
		String username2 = "abcd";
		String password2 = "1234";
		Client c2 = new Client(username2, password2, 6002);
		
		String username3 = "abcde";
		String password3 = "12345";
		Client c3 = new Client(username3, password3, 6002);
		
		String username4 = "abcde";
		String password4 = "123456";
		new Client(username4, password4, 6001);
		
		System.out.println("---------------");
		System.out.println(c3.getMemberList());
		System.out.println("---------------");
		
		c1.send(c1.getUserName(), c3.getUserName(), 2, "Wa7shni ya 3");
		c1.send(c1.getUserName(), c2.getUserName(), 2, "Wa7shni ya 2");
		c1.send(c1.getUserName(), c2.getUserName(), 2, "Wa7shni ya 2");
		c2.send(c2.getUserName(), c1.getUserName(), 0, "Wa7shni ya Abu 3amooo");
		
//		c3.fetch();
//		c2.fetch();
//		c2.fetch();
//		c1.fetch();
		
	}
}
