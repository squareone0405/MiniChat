package socket;

import java.io.*;

import java.net.*;

import javax.swing.JOptionPane;
import util.*;

public class Client {
	private Socket socket;
	DataOutputStream dos;
	private String ip;
	private static String userName;
	
	public Client(String ip){
		this.ip = ip;
		connectServer(ip, Config.LocalServerPort);
	}
	
	public static void setUserName(String userName){
		Client.userName = userName;
	}
	
	public boolean connectServer(String ip, int port){
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(ip, port), Config.TimeoutMs);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "无法连接该好友！", "Warning",
                    JOptionPane.WARNING_MESSAGE);
			return false;
		}
		try {
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public String getIp(){
		return ip;
	}
	
	public boolean sendMsg(String message, String reciever) {  
		if(socket.isConnected()){
			try {
				dos.writeUTF(userName);
				dos.writeUTF(reciever);
				dos.writeUTF(Config.TextPrefix);
				dos.writeUTF(message);
				dos.flush();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		else
			return false;
	}
	
	public boolean sendImage(File file, String reciever) {  
		if(socket.isConnected()){
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	        BufferedInputStream bis = new BufferedInputStream(fis);
	        byte[] byteArray = new byte[(int) file.length()];
	        try {
				bis.read(byteArray, 0, byteArray.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				dos.writeUTF(userName);
				dos.writeUTF(reciever);
				dos.writeUTF(Config.ImagePrefix);
				dos.writeInt((int) file.length());
				dos.writeUTF(file.getName());
				dos.write(byteArray);
				dos.flush();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		else
			return false;
	}
	
	public boolean sendFile(File file, String reciever) {  
		if(socket.isConnected()){
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	        BufferedInputStream bis = new BufferedInputStream(fis);
	        byte[] byteArray = new byte[(int) file.length()];
	        try {
				bis.read(byteArray, 0, byteArray.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				dos.writeUTF(userName);
				dos.writeUTF(reciever);
				dos.writeUTF(Config.FilePrefix);
				dos.writeInt((int) file.length());
				dos.writeUTF(file.getName());
				dos.write(byteArray);
				dos.flush();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		else
			return false;
	}
	
	public boolean sendAudio(File file, String reciever) {  
		if(socket.isConnected()){
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	        BufferedInputStream bis = new BufferedInputStream(fis);
	        byte[] byteArray = new byte[(int) file.length()];
	        try {
				bis.read(byteArray, 0, byteArray.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				dos.writeUTF(userName);
				dos.writeUTF(reciever);
				dos.writeUTF(Config.AudioPrefix);
				dos.writeInt((int) file.length());
				dos.writeUTF(file.getName());
				dos.write(byteArray);
				dos.flush();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		else
			return false;
	}
	
	public boolean sendGroupMsg(String groupStr) {  
		if(socket.isConnected()){
			try {
				dos.writeUTF(userName);
				dos.writeUTF("@");
				dos.writeUTF(Config.GroupPrefix);
				dos.writeUTF(groupStr);
				dos.flush();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		else
			return false;
	}

	public boolean disConnect() {
		try {
			if (socket != null) {  
				socket.close();  
			}   
			return true;  
		} catch (IOException e) {  
			e.printStackTrace(); 
			return false;  
		}  
	}
}
