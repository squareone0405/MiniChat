package gui;

import java.awt.*;

import javax.swing.*;

import javafx.util.Pair;

public class ContactLabel extends JLabel {
	private String id;
	private boolean isOnline;
	private int unread;
	
	public ContactLabel(Pair<String, Boolean> content){
		super();
		this.setFont(new Font("Œ¢»Ì—≈∫⁄", Font.BOLD, 13));
		this.id = content.getKey();
		unread = 0;
		this.setOnline(content.getValue());
		this.setHorizontalAlignment(SwingConstants.CENTER);
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.setOpaque(true);
	}
	
	public void setOnline(boolean isOnline){
		this.isOnline = isOnline;
		String unreadStr = "";
		if(unread > 0){
			unreadStr = "£®" + String.valueOf(unread) + "£©";
		}
		if(isOnline)
			this.setText(id + "°æ‘⁄œﬂ°ø" + unreadStr);
		else
			this.setText(id + "°æ¿Îœﬂ°ø" + unreadStr);
	}
	
	public void addUnread(){
		unread++;
		setOnline(this.isOnline);
	}
	
	public void handelClick(){
		unread = 0;
		setOnline(this.isOnline);
	}
	
	public String getId(){
		return id;
	}
	
	public boolean getOnline(){
		return isOnline;
	}
}
