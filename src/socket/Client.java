package socket;

import java.io.*;

import java.net.*;

import javax.swing.JOptionPane;

public class Client {
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private MessageThread msgThread;
	private boolean isConnected;
	
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
			writer.println(message);
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
}
