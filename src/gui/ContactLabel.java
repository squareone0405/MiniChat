package gui;

import java.awt.*;

import javax.swing.*;

import javafx.util.Pair;

public class ContactLabel extends JLabel {
	private String id;
	private boolean isOnline;
	private int unread;
	private boolean isGroup;
	
	public ContactLabel(Pair<String, Boolean> content){
		super();
		this.setFont(new Font("Œ¢»Ì—≈∫⁄", Font.BOLD, 13));
		this.id = content.getKey();
		isGroup = false;
		if(id.contains(","))
			isGroup = true;
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
		String statusStr = "";
		if(!isGroup){
			if(isOnline)
				statusStr = "°æ‘⁄œﬂ°ø";
			else
				statusStr = "°æ¿Îœﬂ°ø";
		}
		this.setText(unreadStr + id + statusStr);
	}
	
	public boolean isGroup(){
		return isGroup;
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
