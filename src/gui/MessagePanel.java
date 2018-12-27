package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import util.*;

public class MessagePanel extends JPanel {
	private final static BufferedImage corruptImg = getCorruptImg();
	private final static ImageIcon fileImg = getFileImg();
	private static BufferedImage getCorruptImg(){
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(new File(Config.FileCorruptPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bi;
	}
	private static ImageIcon getFileImg(){
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(new File(Config.File4ChatPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Image img = bi.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		ImageIcon imageIcon = new ImageIcon(img);
		return imageIcon;
	}
	
	public MessagePanel(Message msg){
		super();
		JLabel sender = new JLabel(msg.friendId + ":" + "(" + msg.time + ")");
		sender.setFont(new Font("times new roman", Font.PLAIN, 15));
		sender.setBounds(0, 0, 560, 20);
		JTextArea content = new JTextArea();
		JLabel imgLabel = new JLabel();
		BufferedImage bufferdImg = null;
		Box fileBox = Box.createHorizontalBox();
		this.setLayout(null);
		switch(msg.type){
		case Text:
			content.setBounds(0, 20, 300, 20);
			content.setText(msg.content);
			content.setWrapStyleWord(true);
			content.setLineWrap(true);
			content.setOpaque(true);
			content.setEditable(false);
			content.setFocusable(false);
			content.setBackground(new Color(220, 220, 220));
			content.setFont(new Font("华文宋体", Font.PLAIN, 20));
		    content.setSize(content.getPreferredSize());
		    this.add(content);
		    this.setPreferredSize(new Dimension(560,content.getHeight() + 30));
		    break;
		case File:
			fileBox.setBounds(0, 20, 300, 100);
			JLabel fileLabel = new JLabel(fileImg);
			JLabel nameLabel = new JLabel(msg.content);
			nameLabel.setFont(new Font("华文宋体", Font.PLAIN, 18));
			nameLabel.setAutoscrolls(true);
			fileBox.add(fileLabel);
			fileBox.add(nameLabel);
		    this.add(fileBox);
		    this.setPreferredSize(new Dimension(560,fileBox.getHeight() + 30));
		    break;
		case Image:
			String imgPath = null;
			if(msg.isUser)
				imgPath = msg.content;
			else 
				imgPath = Config.ChatFilePath + msg.content;
			try {
			    bufferdImg = ImageIO.read(new File(imgPath));
			} catch (IOException e) {
				bufferdImg = corruptImg;
			}
			int width = bufferdImg.getWidth();
			int height = bufferdImg.getHeight();
			if(width > 200){
				height = (int)(height * 200.0 / width);
				width = 200;
			}
			Image img = bufferdImg.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			ImageIcon imageIcon = new ImageIcon(img);
			imgLabel.setBounds(0, 20, width, height);
			imgLabel.setHorizontalAlignment(JLabel.CENTER);
			imgLabel.setIcon(imageIcon);
			this.add(imgLabel);
			this.setPreferredSize(new Dimension(560,imgLabel.getHeight() + 30));
		default:
		    break;
		}	
		if(msg.isUser){
			sender.setHorizontalAlignment(JLabel.RIGHT);
			imgLabel.setLocation(560 - imgLabel.getWidth(), 20);
			fileBox.setLocation(260, 20);
			content.setLocation(260, 20);
		}
		this.add(sender);
	}
}
