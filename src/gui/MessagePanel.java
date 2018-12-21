package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import util.*;

public class MessagePanel extends JPanel {
	public MessagePanel(Message msg){
		super();
		JLabel sender = new JLabel(msg.friendId + ":" + "(" + msg.time + ")");
		sender.setFont(new Font("times new roman", Font.PLAIN, 15));
		sender.setBounds(0, 0, 560, 20);
		JTextArea content = new JTextArea();
		content.setBounds(0, 20, 300, 20);
		content.setText(msg.content);
		content.setWrapStyleWord(true);
		content.setLineWrap(true);
		content.setOpaque(true);
		content.setEditable(false);
		content.setFocusable(false);
		content.setBackground(new Color(220, 220, 220));
		content.setFont(new Font("»ªÎÄËÎÌå", Font.PLAIN, 20));
	    content.setSize(content.getPreferredSize());
		this.setLayout(null);
		this.setPreferredSize(new Dimension(560,content.getHeight() + 30));
		if(msg.isUser){
			sender.setHorizontalAlignment(JLabel.RIGHT);
			content.setLocation(260, 20);
		}
		this.add(content);
		this.add(sender);
	}
}
