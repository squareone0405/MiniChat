package socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
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
	    DataInputStream dis;
	    public ClientThread(Socket clientSocket) {
	    	this.socket = clientSocket;
	        try {
				dis = new DataInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    public void run() {
	    	String id = null;
	    	String type = null;
			int length = 0;
	    	while(true) {
	    		try {
	    			id = dis.readUTF();
	    			type = dis.readUTF();
	    			length = dis.readInt();
	    			Message message = new Message();
					if(type.equals(Config.TextPrefix)){
						String content = dis.readUTF();
						message.friendId = id;
						message.isUser = false;
						message.type = MessageType.Text;
						message.time = Tools.getCurentTime();
						message.content = content;
					}
					mf.recieveMsg(message);
					/*while(true){
						length = reader.read(prefix, 0, 18);
						if(length > 0)
							break;
					}
					str = String.valueOf(prefix);
					if(str == null || str.equals(new String(""))) {
						continue;
					}
					System.out.println(str);
					String id = str.substring(0, 10);
					String typeStr = str.substring(10, 14);
					String lengthStr = str.substring(14, 18);
					int msgLength = Integer.parseInt(lengthStr);
					char[] content = new char[msgLength];
					reader.read(content, 0, msgLength);
					Message message = new Message();	
					if(typeStr.equals(Config.TextPrefix)){
						message.friendId = id;
						message.isUser = false;
						message.type = MessageType.Text;
						message.time = Tools.getCurentTime();
						message.content = String.valueOf(content);
					}
					mf.recieveMsg(message);*/
				} catch (IOException e) {
					//JOptionPane.showMessageDialog(null, "连接断开", "Info", JOptionPane.INFORMATION_MESSAGE);
					try {
						dis.close();
						return;
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}    		
	    	}
	    }
	}
}

