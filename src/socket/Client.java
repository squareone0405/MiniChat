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
	DataOutputStream dos;
	private boolean isConnected;
	private String id;
	
	public Client(String id, String ip){
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
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*try {
			writer = new PrintWriter(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		return true;
	}
	
	public boolean sendMsg(String message) {  
		if(isConnected){
			try {
				dos.writeUTF(id);
				dos.writeUTF(Config.TextPrefix);
				dos.writeInt(message.length());
				dos.writeUTF(message);
				dos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			/*char[] charArray = message.toCharArray();
			String lengthStr = String.valueOf(charArray.length);
			while(lengthStr.length() < 4)
				lengthStr = "0" + lengthStr;
			writer.write(id);
			writer.write(Config.TextPrefix);
			writer.write(lengthStr);
			writer.write(charArray, 0, charArray.length);
			writer.flush();*/
			return true;
		}
		else
			return false;
	}
	
	public boolean sendImage(File file) {  
		if(isConnected){
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
			} catch (FileNotFoundException e2) {
				e2.printStackTrace();
			}
	        BufferedInputStream bis = new BufferedInputStream(fis);
	        byte[] byteArray = new byte[(int) file.length()];
	        try {
				bis.read(byteArray, 0, byteArray.length);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			String lengthStr = String.valueOf(byteArray.length);
			while(lengthStr.length() < 4)
				lengthStr = "0" + lengthStr;
			writer.write(id);
			writer.write(Config.TextPrefix);
			writer.write(lengthStr);
			try {
				socket.getOutputStream().write(byteArray);
			} catch (IOException e) {
				e.printStackTrace();
			}
			writer.flush();
			return true;
		}
		else
			return false;
	}

	public boolean disConnect() {
		try {  
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
