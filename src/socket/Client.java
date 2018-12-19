package socket;

import java.io.*;

import java.net.*;

import javax.swing.JOptionPane;

import gui.MainFrame;
import util.*;

public class Client {
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private MessageThread msgThread;
	private boolean isConnected;
	private MainFrame mf;
	private String id;
	
	public Client(MainFrame mf, String id){
		this.mf = mf;
		this.id = id;
	}
	
	public Client(){
	}
	
	public boolean connectServer(String ip, int port){
		if(isConnected) {
			return true;
		}
		try {
			socket = new Socket(ip, port);
			isConnected = true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		try {
			writer = new PrintWriter(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}  
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		msgThread = new MessageThread(reader);
		msgThread.start();  
		return true;
	}
	
	public boolean sendMsg(String message) {  
		if(isConnected){
			writer.println(Config.TextHeader + message);
			writer.flush();
			return true;
		}
		else
			return false;
	}

	public boolean disConnect() {
		try {  
			sendMsg("");
			if(msgThread != null)
				msgThread.stop();
			if (reader != null) {  
				reader.close();  
			}  
			if (writer != null) {  
				writer.close();  
			}
			if (socket != null) {  
				socket.close();  
			}  
			isConnected = false;  
			return true;  
		} catch (IOException e) {  
			e.printStackTrace();
			isConnected = true;  
			return false;  
		}  
	}  
	
	class MessageThread extends Thread {
		private BufferedReader msgReader;
		public MessageThread(BufferedReader msgReader) {
			this.msgReader = msgReader;
		}
		public void run() {
			String messageStr = null;
			while(true) {
				try {
					System.out.println("message thread running");
					messageStr = msgReader.readLine();
					System.out.println("read");
					if(messageStr == null) {
						System.out.println("continue");
						continue;
					}
					System.out.println(messageStr);
					Message message = new Message();
					String content = null;
					if(messageStr.startsWith("TXT")){
						content = messageStr.substring(3, messageStr.length());
						message.sender = id;
						message.isUser = false;
						message.type = MessageType.Text;
						message.time = "8102";
						message.content = content;
					}
					mf.recieveMsg(message);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Á¬½Ó¶Ï¿ª", "Info",  
							JOptionPane.INFORMATION_MESSAGE);
					try {
						msgReader.close();
						return;
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}
}
