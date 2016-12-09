import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;



public class Client_new {

	private static BufferedReader br = null;
	private static PrintWriter out = null;
	
	private JFrame frame  = new JFrame("Client");
	private JTextField Ennter_text = new JTextField(50);
	private JTextArea messageArea = new JTextArea("Welcome here..\n",14,50);
	//private JButton login, logout, whoIsIn, send;
	
	//Constructor
	public Client_new(){
		
		//GUI Layout
		Ennter_text.setEditable(true);
		messageArea.setEditable(false);
		frame.getContentPane().add(Ennter_text, "North");
		frame.getContentPane().add(new JScrollPane(messageArea), "Center");
		
		/*
		login = new JButton("Login");
		login.addActionListener(this);
		
		JPanel centerPanel = new JPanel(new GridLayout(1, 1));
       // centerPanel.add(new JScrollPane(ta));
        //ta.setEditable(false);
        add(centerPanel, BorderLayout.CENTER);
		 JPanel southPanel = new JPanel();
	     southPanel.add(login);
	     add(southPanel, BorderLayout.SOUTH);
	     */
		frame.pack();
		
		//Add listener to the Ennter_text
		Ennter_text.addActionListener(new ActionListener() {
			/*
			 * Here after pressing enter we send the contents of textField to the 
			 * server and display the response from the server in the TextArea
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Witing on to server..");
				//Here we write on to the server
				
				out.println(Ennter_text.getText());	
				Ennter_text.setText("");
				String response_server;
				try {
					response_server = br.readLine();
					messageArea.append(response_server+" \n");
					Ennter_text.selectAll();
				} catch (IOException e2) {

					e2.printStackTrace();
				};
			
				
			}
		});
		
	}//End Constructor
	
	//Function returning ClientName
		private String getClientName(){
			System.out.println("Dialog Box popup");
			/*return JOptionPane.showInputDialog(frame,
					"Choose a client name",
					"Entered Client Name",
						JOptionPane.PLAIN_MESSAGE);*/
			String client_name = JOptionPane.showInputDialog(frame,
					"Choose a client name",
					"Entered Client Name",
						JOptionPane.QUESTION_MESSAGE);

			return client_name;
		}
		
		private String getServerAddress(){
			
			String serverAdr = JOptionPane.showInputDialog(frame,
					"Enter IP adress of server",
					"Welcome to captilization program",
						JOptionPane.QUESTION_MESSAGE);

			return serverAdr;
		}
		/*
		private int getExitClient_Confirmation(){
		int dialogButton = JOptionPane.YES_OPTION;
		int dialogResult = JOptionPane.showConfirmDialog(null, "Press Okay to exit","warning",dialogButton);
		if(dialogResult == JOptionPane.YES_OPTION){
			  \\ Saving code here
			}
		}
		*/
	public void startConnection(){
	
		int portNumber = 5992;
		Socket socket;
		
		//Make connection and Initialize input/output streams 
		try {
			//String client_name_froom_user = getClientName();
			String server_address = getServerAddress();
			socket = new Socket(server_address, portNumber);
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			//String input = br.readLine();
			//System.out.println("Hello we recived From server:"+input);	//We read from server socket
			out = new PrintWriter(socket.getOutputStream(),true);
			//out.println("Hello from client: "+);//We write on server socket
			
			for (int i = 1; i < 4; i++) {
				
				//Append string 
				messageArea.append("\n"+br.readLine());
				}
		
			while (true) {
				String read_client_Line = br.readLine().toString();
				if (read_client_Line.startsWith("Welcome to chat")) 
				{
					out.println(getClientName());
				}
				else if(read_client_Line.startsWith("[**Clinet"))
				{
					messageArea.append("\n"+read_client_Line.toString());
				}
				else if (read_client_Line.startsWith("exit"))
				{
					System.out.println("Set Jframe Invisible");
					frame.setVisible(false);
				}
				else if (read_client_Line.startsWith("*****New"))
				{
					messageArea.append("\n"+read_client_Line.toString());
				}
				//Accepting message  
				else if (read_client_Line.startsWith("Client"))
				{
					messageArea.append("\n"+read_client_Line.toString());
				}
				else if (read_client_Line.startsWith("Personal"))
				{
					messageArea.append("\n"+read_client_Line.toString());
				}
			}//End While loop
		
		} catch (IOException e) {
			System.out.println("Socket connection errlocalhostor");
			e.printStackTrace();
		}
		
	}//End function
	
	
	public static void main(String[] args) {
		
	
		Client_new obj = new Client_new();
		obj.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		obj.frame.pack();//Causes this Window to be sized to fit the preferred size and layouts of its subcomponents
		obj.frame.setVisible(true);//Visibility of the Window is turned on
		obj.startConnection();
	
	}//End main

}//End class
