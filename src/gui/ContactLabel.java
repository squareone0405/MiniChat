package gui;

import java.awt.*;

import javax.swing.*;

import javafx.util.Pair;

public class ContactLabel extends JLabel {
	private String id;
	private boolean isOnline;
	
	public ContactLabel(Pair<String, Boolean> content){
		super();
		this.id = content.getKey();
		this.setOnline(content.getValue());
		this.setHorizontalAlignment(SwingConstants.CENTER);
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.setOpaque(true);
	}
	
	public void setOnline(boolean isOnline){
		this.isOnline = isOnline;
		if(isOnline)
			this.setText(id + "°æ‘⁄œﬂ°ø");
		else
			this.setText(id + "°æ¿Îœﬂ°ø");
	}
	
	public boolean getOnline(){
		return isOnline;
	}
}
