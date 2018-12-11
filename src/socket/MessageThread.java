package socket;

import java.io.*;

import javax.swing.JOptionPane;


public class MessageThread extends Thread{
	private BufferedReader msgReader;
	public MessageThread(BufferedReader msgReader) {
		this.msgReader = msgReader;
	}
	public void run() {
		String message = null;
		while(true) {
			try {
				message = msgReader.readLine();
				if(message == null) {
					continue;
				}
				System.out.println(message);
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
