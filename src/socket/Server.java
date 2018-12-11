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

import util.*;

public class Server {
	private ServerSocket serverSocket;
	private ServerThread serverThread;
	private HashMap<Socket, ClientThread> clientTable;
	
	public Server(){
		clientTable = new HashMap<Socket, ClientThread>();
		buildServer();
	}
	
	public void buildServer() {
		int port = Config.localServerPort;
		try {
			serverSocket = new ServerSocket(port);
			serverThread = new ServerThread();
			serverThread.start();
			JOptionPane.showMessageDialog(null, "�����������ɹ�", "Info",
                    JOptionPane.INFORMATION_MESSAGE); 
        } catch (BindException e) {
            JOptionPane.showMessageDialog(null, "�˿ں��ѱ�ռ�ã��뻻һ����", "Error",
                    JOptionPane.ERROR_MESSAGE); 
        } catch (Exception e1) {  
            e1.printStackTrace(); 
            JOptionPane.showMessageDialog(null, "�����������쳣��", "Error",
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
	    	String message = null;
	    	while(true) {
	    		try {
					message = reader.readLine();
					System.out.println("client:" + message);
				} catch (IOException e) {
					e.printStackTrace();
				}      		
	    	}
	    }
	}
}

