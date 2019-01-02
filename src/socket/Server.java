package socket;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
            JOptionPane.showMessageDialog(null, "本地服务器端口号9876已被占用", "Error",
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
	    	String sender = null;
	    	String reciever = null;
	    	String type = null;
			int length = 0;
	    	while(true) {
	    		try {
	    			sender = dis.readUTF();
	    			reciever = dis.readUTF();
	    			type = dis.readUTF();
	    			Message message = new Message(sender, reciever, Tools.getCurentTime(), MessageType.Text, null);
					if(type.equals(Config.TextPrefix)){
						String content = dis.readUTF();
						message.type = MessageType.Text;
						message.content = content;
					}
					else if(type.equals(Config.ImagePrefix)){
						length = dis.readInt();
						String fileName = dis.readUTF();    
						byte[] fileBuffer = new byte[length];
						File dir = new File(Config.ChatFilePath);
					    if (!dir.exists())
					    	dir.mkdir();
						FileOutputStream output = new FileOutputStream(new File(dir, fileName));
				        int size = length;
				        int bytesRead;
				        while (size > 0 && (bytesRead = dis.read(fileBuffer, 0, (int)Math.min(fileBuffer.length, size))) != -1){
						    output.write(fileBuffer, 0, bytesRead);
						    size -= bytesRead;
						    System.out.println(bytesRead);
						}
				        output.close();
						message.type = MessageType.Image;
						message.content = fileName;
					}
					else if(type.equals(Config.FilePrefix)){
						length = dis.readInt();
						String fileName = dis.readUTF();		    
						byte[] fileBuffer = new byte[length];
						File dir = new File(Config.ChatFilePath);
					    if (!dir.exists())
					    	dir.mkdir();
						FileOutputStream output = new FileOutputStream(new File(dir, fileName));
				        int size = length;
				        int bytesRead;
				        while (size > 0 && (bytesRead = dis.read(fileBuffer, 0, (int)Math.min(fileBuffer.length, size))) != -1){
						    output.write(fileBuffer, 0, bytesRead);
						    size -= bytesRead;
						    System.out.println(bytesRead);
						}
				        output.close();
						message.type = MessageType.File;
						message.content = fileName;
					}
					else if(type.equals(Config.AudioPrefix)){
						length = dis.readInt();
						String fileName = dis.readUTF();		    
						byte[] fileBuffer = new byte[length];
						File dir = new File(Config.ChatFilePath);
					    if (!dir.exists())
					    	dir.mkdir();
						FileOutputStream output = new FileOutputStream(new File(dir, fileName));
				        int size = length;
				        int bytesRead;
				        while (size > 0 && (bytesRead = dis.read(fileBuffer, 0, (int)Math.min(fileBuffer.length, size))) != -1){
						    output.write(fileBuffer, 0, bytesRead);
						    size -= bytesRead;
						    System.out.println(bytesRead);
						}
				        output.close();
						message.type = MessageType.Audio;
						message.content = fileName;
					}
					else if(type.equals(Config.GroupPrefix)){
						String groupStr = dis.readUTF();		    
						mf.recieveGroupMsg(groupStr);
						continue;
					}
					mf.recieveMsg(message);
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

