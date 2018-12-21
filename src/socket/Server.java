package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import gui.MainFrame;
import util.*;

public class Server {
	private ServerSocket serverSocket;
	private ServerThread serverThread;
	private HashMap<Socket, ClientThread> clientTable;
	private MainFrame mf;
	
	public Server(MainFrame mf){
		this.mf = mf;
		clientTable = new HashMap<Socket, ClientThread>();
		buildServer();
	}
	
	public void buildServer() {
		int port = Config.LocalServerPort;
		try {
			serverSocket = new ServerSocket(port);
			serverThread = new ServerThread();
			serverThread.start();
        } catch (BindException e) {
            JOptionPane.showMessageDialog(null, "端口号9876已被占用", "Error",
                    JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e1) {  
            e1.printStackTrace(); 
            JOptionPane.showMessageDialog(null, "启动服务器异常！", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } 	
	}
	
	public void closeServer() {
		if (serverSocket != null && !serverSocket.isClosed()) {
	        try {
	        	serverSocket.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
		try {  
			if (serverThread != null)  {
				serverThread.stop();
			}
			Iterator<Entry<Socket, ClientThread>> it = clientTable.entrySet().iterator();
		    while (it.hasNext()) {
		    	Entry<Socket, ClientThread> pair = (Entry<Socket, ClientThread>)it.next();
		    	if(!pair.getKey().isClosed())
		    		pair.getKey().close();
		        pair.getValue().stop();
		        it.remove();
		    }
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
	}
	
	class ServerThread extends Thread {
		public ServerThread() {			
		}  
		public void run(){
			while(true) {
				try {
					Socket client = serverSocket.accept();
					ClientThread clientThread = new ClientThread(client);
					clientThread.start();
					clientTable.put(client, clientThread);
				} catch (IOException e) {
					e.printStackTrace();
				} 				
			}
		}
	}
	
	class ClientThread extends Thread {
		Socket socket;
		BufferedReader reader;  
	    PrintWriter writer; 
	    public ClientThread(Socket clientSocket) {
	    	this.socket = clientSocket;
	    	 try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}  
	         try {
				writer = new PrintWriter(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    public void run() {
	    	String messageStr = null;
	    	while(true) {
	    		try {
	    			messageStr = reader.readLine();
					if(messageStr == null) {
						continue;
					}
					char[] buff = new char[1024];
					int read;
					StringBuilder response= new StringBuilder();
					while((read = reader.read(buff)) != -1) {
					    response.append(buff, 0, read );  
					}
					messageStr = response.toString();
					if(messageStr == null) {
						System.out.println("continue");
						continue;
					}
					System.out.println(messageStr);
					Message message = new Message();
					String content = null;
					if(messageStr.startsWith(Config.TextPrefix)){
						content = messageStr.substring(4, messageStr.length());
						message.friendId = "unknown";
						message.isUser = false;
						message.type = MessageType.Text;
						message.time = Tools.getCurentTime();
						message.content = content;
					}
					mf.recieveMsg(message);
				} catch (IOException e) {
					e.printStackTrace();
				}      		
	    	}
	    }
	}
}

