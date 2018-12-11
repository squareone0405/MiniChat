package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;

import javafx.util.Pair;

import util.*;

public class MessageRenderer implements ListCellRenderer<Object> {

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		Message msg = (Message) value;
		JPanel panel = new JPanel();
		JLabel sender = new JLabel(msg.sender + ":" + "(" + msg.time + ")");
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
		panel.setLayout(null);
		panel.setPreferredSize(new Dimension(560,content.getHeight() + 30));
		if(msg.isUser){
			sender.setHorizontalAlignment(JLabel.RIGHT);
			content.setLocation(260, 20);
		}
		panel.add(content);
		panel.add(sender);
		return panel;
	}
}