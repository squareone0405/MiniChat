package gui;

import java.awt.*;

import javax.swing.*;

import javafx.util.Pair;

public class ContactRenderer extends JLabel implements ListCellRenderer<Object> {
	private String id;
	private boolean isOnline;
	
	public ContactRenderer(){
		super();	
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
	
	public String getId(){
		return id;
	}
	
	public boolean getOnline(){
		return isOnline;
	}
	
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		@SuppressWarnings("unchecked")
		Pair<String, Boolean> content = (Pair<String, Boolean>) value;
		this.id = content.getKey();
		this.setOnline(content.getValue());
		return this;
	}
}
