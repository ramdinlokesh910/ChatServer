import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Server_new {


	//ServerSocket object
	private static ServerSocket listner;
	//private static Integer client_Id_num = 0;
	//Socket object
	private static Socket socket;
	//private static DataInputStream input_stream = null;
	private static final int PORT = 5992;
	
	//Server_Ip address
	public static InetAddress server_IP;
	
	//Array List stores all name of CLient
	private static ArrayList<String> namesArray = new ArrayList<String>();
	/*
	 * storing printWriters for respective clients in list. 
	 * Makes Easy to broadcast message to all clients connected to the server		
	 */
	private static ArrayList<PrintWriter> writersArray = new ArrayList<PrintWriter>(); 
	private static HashMap<String, PrintWriter> map = new HashMap<String, PrintWriter>();
	
	public static void main(String[] args) throws Exception
	{	//Class Object created
		Server_new Class_Obj = new Server_new();	
		//listner = new ServerSocket(PORT);
		try {
            while (true) 
            {	
            	System.out.println("Server Running Port: " + PORT);
            	Class_Obj.startConnection();

            }
        } finally {
        	System.out.println("finally....");
        	listner.close();
        }
	}//End main Function
	
	//Function to create/handle new client request 
			public void startConnection() throws Exception
			{
				listner = new ServerSocket(PORT);
				server_IP = listner.getInetAddress().getLocalHost();
				
				System.out.println("server waiting for client on port:"+PORT+" Server Ip"+server_IP);
				 //socket = listner.accept();
				
				try {
					System.out.println("Runing Client Thread");
					socket = listner.accept();
					//get the Ip address of the client Connected
					InetAddress clientIp = socket.getInetAddress();
					System.out.println("Connected to :"+clientIp.toString());

					//Passing socket and server_IP: to THread
					 ClientThread ct = new ClientThread(socket,server_IP);
					 ct.start();
				}finally {
		        	listner.close();
		        }
				 
			//	InetAddress clientIp = socket.getInetAddress();
			//	System.out.println("Connected to :"+clientIp.toString());
			//	client_Id_num++;
				//Start a new client thread
			//	ClientThread ct = new ClientThread(socket,client_Id_num);
				//new ClientThread(socket,client_Id_num).start();
		//		tempList.add(ct);
		//		ct.start();
				
				
			}
			
	/*
	 * A private thread to handle requests on a particular
     * socket.  The client terminates the dialogue by sending a single line
     * containing only a period.
	 */
	public static class ClientThread extends Thread{
		private Socket socket_client;
		private Integer client_Id;
		private String client_name;
		PrintWriter out;
		BufferedReader input;
		InetAddress server_IP_add;
		
		public ClientThread(Socket socket,InetAddress server_IP) {
			this.socket_client = socket;		//Assign socket to current thread
			client_Id = 1;
			this.server_IP_add = server_IP;
			/*JFrame frame  = new JFrame("Client");
			JOptionPane.showInputDialog(frame,
					"Choose a client name",
					"Entered Client Name",
						JOptionPane.QUESTION_MESSAGE);*/
			System.out.println("New Connection with client Id:"+client_Id+"at socket "+socket_client);
		}
		/**
         * This thread  first sends the
         * client a welcome message then repeatedly reading strings
         * and sending back the string received to all the other clients.
         */
		@Override
		public void run() {
			
			try {
				//Reading Initialize
				input = new BufferedReader(new InputStreamReader(socket_client.getInputStream()));
				//DataInputStream input = new DataInputStream(socket_client.getInputStream());
				
				//Writing characters and flush after sending
				 out= new PrintWriter(socket_client.getOutputStream(),true);
				
				 //Request the client name
			while (true)
				{
					out.println("Welcome to chat server...You are connected to Server_IP :"+server_IP_add+""+
							"\nEnter your name in textField above");
					client_name = input.readLine().toString();
					
					if (client_name == null || client_name == "") {
						return;
					}
					synchronized (namesArray) {
						if (!namesArray.contains(client_name)) {
							namesArray.add(client_name);
							break;
						}
					}
				}//End while loop

				//Add sockets print writer to array list so that client can recive the broadcast message
				out.println("****Hello "+client_name+" to chat room****");
				
				//Client Entered Information to other clients in the network
				for (PrintWriter write_NewEntry : writersArray) 
				{	System.out.println("new client entry");
				write_NewEntry.println("*****New Client Entry :"+client_name+"*******");
					//write.flush();
				}
				//Add the new client's writer object to array
				writersArray.add(out);
				
				map.put(client_name, out);//Here we map the clientName to its:- PrintWriter object
				
				//Here we read the input from client line by line and return the string to all clients in network 
				while (true) 
				{	out.println("\nwritten to client.. Now read :");
					String str = input.readLine().toString();
					if(str == null)
					{
						return;
					}
					System.out.println("After read: "+str.toString());
					if(str.startsWith("exit"))
					{	System.out.println("Client exit");
						namesArray.remove(client_name);
				
						//This is returning String= "exit" to the client on which the 
						//Jframe of particular client is made invisible				
						out.println(str);	
						
						//Loop informing other clients about leaving Chat room
						for (PrintWriter write_LeaveMsg : writersArray) {
							System.out.println("Before write in client loop:"+str+"|| Size:"+ writersArray.size()+"|| Array Data:"+writersArray);
							write_LeaveMsg.println("Client "+client_name+" Left the Chat room");
							//write.flush();
						}
						//Removes the client printWriter object from the array
						//writersArray.remove(out);
						socket_client.close();
						continue;
					}
					//Return the client names present in the Chat room/Connected to server 
					if(str.startsWith("who is in"))
					{
						System.out.println("Printing Clients Name present in the Chat room ");
						for (String write_Names : namesArray) 
						{	System.out.println("Before write in client loop:"+str+"|| Size:"+ writersArray.size()+"|| Array Data:"+writersArray);
							out.println("Client "+write_Names+" is Present***\n");
						//	write_Names.println("Client "+client_name+" Left the Chat room");
							//write.flush();
						}
						continue;
					}
					//Initiating personal chat--Client1	
					if(str.startsWith("initiate personal chat"))
					{//Print to all the clietnts the about personal chat
						
						//namesArray.get(str);
						String s_otherClient = str.substring(23);
						String s1_thisClient = client_name;
						System.out.println("This Client:"+s1_thisClient+"\nOther client:"+s_otherClient);
						StartPersonalChat( input,s_otherClient, s1_thisClient);
						continue;
					}//End Outer If loop
					
				/*	//Initiating personal chat-Client2			
					if(str.startsWith("pqr"))
					{//Print to all the clietnts the about personal chat
						for (PrintWriter Personal_Chat : writersArray) 
						{	System.out.println("Initiate personal chat");
							
							String other_Client ="xyz";
							if (map.containsKey(other_Client)) 
							{
									map.get("pqr").println("Personal Chat pqr xyz:"+str);
									map.get("xyz").println("Personal Chat pqr xyz:"+str);
							}
						}	
						continue;
					}//End Outer If loop
					*/
					//Writing to all the clients
					for (PrintWriter write : writersArray) {
						System.out.println("Before write in client loop:"+str+"|| Size:"+ writersArray.size()+"|| Array Data:"+writersArray);
						write.println("[**Clinet "+client_name+"] "+str);
						//write.flush();
					}
					
//					String ret = str.toUpperCase();
//					out.println("String Return in uppercase: "+ret);
				}//ENd 2nd while loop

			} catch (IOException e) {
				System.out.println("error running thread  with Client# "+client_Id);
			}
			finally {
				
				if (client_name != null) {
				namesArray.remove(client_name);
				}
				if (out != null) {
					writersArray.remove(out);
				}	
				
				try {
					socket_client.close();
				} catch (IOException e) {
					System.out.println("error closing client thread socket");
					e.printStackTrace();
				}
			}//End finally
			System.out.println("Closed client# " + client_name);
			
		}
		//Function Personal Chat
		void StartPersonalChat(BufferedReader input, String s, String s1) throws IOException
		{
			//String other_Client = "pqr";
			String other_Client = s;
			String this_client = s1;
			System.out.println("In function || ClientNames:"+this_client+"||"+other_Client);
			BufferedReader input_obj =input;
			//Checks if other client name present as key-: returns true if present
			if (map.containsKey(other_Client)) 
			{
					map.get(this_client).println("Personal Chat between:"+other_Client+"--"+this_client+" initiated!!");
					map.get(other_Client).println("Personal Chat between:"+other_Client+"--"+this_client+" initiated!!");	
			}
			
			while (true) 
			{
				String str_Personal_Chat = input_obj.readLine().toString();
				if(str_Personal_Chat == null)
				{
					return;
				}
				if(str_Personal_Chat.equalsIgnoreCase("chat exit"))
				{
					System.out.println("before brak personal chat");
					break;
				}
				map.get(other_Client).println("Personal Chat:"+str_Personal_Chat+"\n");
				map.get(this_client).println("Personal Chat:"+str_Personal_Chat+"\n");
				
				continue;
			
			}//End While
			System.out.println("break the function");
		}//End Function

	}//End ClientThread
}//End Class
