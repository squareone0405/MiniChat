package socket;

import java.io.*;

import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
	
	public Client(MainFrame mf, String id, String ip){
		this.mf = mf;
		this.id = id;
		connectServer(ip, Config.LocalServerPort);
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
			//writer.print(Config.TextHeader + message);
			writer.print(message);
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
					char[] buff = new char[1024];
					int read;
					StringBuilder response = new StringBuilder();
					while((read = msgReader.read(buff)) != -1) {
					    response.append(buff, 0, read);  
					}
					messageStr = response.toString();
					if(messageStr == null || messageStr.equals(new String(""))) {
						System.out.println("continue");
						continue;
					}
					System.out.println(messageStr);
					Message message = new Message();
					String content = null;
					if(messageStr.startsWith(Config.TextPrefix)){
						content = messageStr.substring(4, messageStr.length());
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm::ss");
						Calendar calendar = Calendar.getInstance();
						Date date = calendar.getTime();
						message.friendId = id;
						message.isUser = false;
						message.type = MessageType.Text;
						message.time = sdf.format(date);
						message.content = content;
					}
					mf.recieveMsg(message);
					messageStr = null;
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
