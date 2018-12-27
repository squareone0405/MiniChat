package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.swing.JOptionPane;

import util.Config;

import gui.LoginFrame;
import gui.MainFrame;

public class CentralServerClient {
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private MessageThread thread;
	private String userName;
	private String idToCheck;
	private LoginFrame lf;
	private MainFrame mf;
	
	public CentralServerClient(){
		connectCentralServer();
	}
	
	public boolean connectCentralServer(){
		try {
			socket = new Socket();   
			socket.connect(new InetSocketAddress(Config.CentralServerAddr, Config.CentralServerPort), Config.TimeoutMs); 
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
		thread = new MessageThread();
		thread.start();
		return true;
	}
	
	public void setLoginFrame(LoginFrame lf){
		this.lf = lf;
	}
	
	public void setMainFrame(MainFrame mf){
		this.mf = mf;
	}
	
	public void setUserNames(String userName){
		this.userName = userName;
		System.out.println(this.userName);
	}
	
	public void sendLogin(){
		writer.print(userName + Config.LoginSuffix);
		writer.flush();
	}
	
	public void sendLogout(){
		writer.print(Config.LogoutPrefix + userName);
		writer.flush();
	}
	
	public void checkOnline(String id){
		this.idToCheck = id;
		writer.print(Config.CheckOnlinePrefix + id);
		writer.flush();
	}
	
	public boolean isConnected(){
		return socket.isConnected();
	}

	public boolean disConnect() {
		try {
			if(thread != null)
				thread.stop();
			if (reader != null) {  
				reader.close();  
			}  
			if (writer != null) {  
				writer.close();  
			}
			if (socket != null) {
				socket.close();
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace(); 
			return false;
		}
	}
	
	class MessageThread extends Thread {
		public void run() {
			String str = null;
			int length = 0;
			char[] buffer = new char[20];
			while(true) {
				try {
					while(true){
						length = reader.read(buffer, 0, 20);
						if(length > 0)
							break;
					}
					str = String.valueOf(buffer);
					str = str.substring(0, length);
					if(str == null || str.equals(new String(""))) {
						continue;
					}
					System.out.println(str);
					if(str.equals(Config.LoginConfirm)) {
						lf.loginConfirm();
					}
					else if(str.equals(Config.LogoutConfirm)){
						socket.close();
					}
					else if(str.equals(Config.NotOnline)){
						mf.recieveOnlineResponse(idToCheck, "");
					}
					else if(str.equals(Config.IncorrectNo)){
						mf.recieveIncorretNo();
					}
					else if(str.equals(Config.IncorrectLoginNo)){
						lf.recieveIncorretLoginNo();
					}
					else{
						int count = str.length() - str.replace(".", "").length();
						if(count == 3)
							mf.recieveOnlineResponse(idToCheck, str);
					}
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Á¬½Ó¶Ï¿ª", "Info",  
							JOptionPane.INFORMATION_MESSAGE);
					try {
						reader.close();
						return;
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}
}
