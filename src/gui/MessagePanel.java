package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.*;

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
	
	private ImageIcon imageIcon;
	
	public MessagePanel(Message msg){
		super();
		JLabel sender = new JLabel(msg.friendId + ":" + "(" + msg.time + ")");
		sender.setFont(new Font("times new roman", Font.PLAIN, 15));
		sender.setBounds(0, 0, 560, 20);
		JTextPane content = null;
		JLabel imgLabel = null;
		BufferedImage bufferdImg = null;
		imageIcon = null;
		Box fileBox = null;
		this.setLayout(null);
		switch(msg.type){
		case Text:
			content = new JTextPane();
			content.setBounds(0, 20, 300, 20);
			content.setEditorKit(new WrapEditorKit());
			content.setText(msg.content);
			content.setOpaque(true);
			content.setEditable(false);
			content.setFocusable(false);
			content.setBackground(new Color(220, 220, 220));
			content.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 15));
			content.setLocation(0, 20);
			content.setSize(300, 40);
			content.setSize(300, content.getPreferredSize().height);
		    this.add(content);
		    this.setPreferredSize(new Dimension(560, content.getHeight() + 30));
		    break;
		case File:
			fileBox = Box.createHorizontalBox();
			fileBox.setBounds(0, 20, 300, 100);
			JLabel fileLabel = new JLabel(fileImg);
			JLabel nameLabel = new JLabel(msg.content);
			nameLabel.setFont(new Font("»ªÎÄËÎÌå", Font.PLAIN, 15));
			nameLabel.setAutoscrolls(true);
			fileBox.add(fileLabel);
			fileBox.add(nameLabel);
		    this.add(fileBox);
		    this.setPreferredSize(new Dimension(560,fileBox.getHeight() + 10));
		    break;
		case Image:
			imgLabel = new JLabel();
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
			imageIcon = new ImageIcon(img);
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
			if(imgLabel != null)
				imgLabel.setLocation(560 - imgLabel.getWidth(), 20);
			if(fileBox != null)
				fileBox.setLocation(260, 20);
			if(content != null)
				content.setLocation(260, 20);
		}
		this.add(sender);
	}
	
	public void setImageObserver(JList list, int index){
		if(imageIcon != null)
			imageIcon.setImageObserver(new AnimatedObserver(list, index));
	}

	class AnimatedObserver implements ImageObserver
	{
		JList list;
		int index;

		public AnimatedObserver(JList list, int index) {
			this.list = list;
			this.index = index;
		} 

		public boolean imageUpdate (Image img, int infoflags, int x, int y, int width, int height) {
			if ((infoflags & (FRAMEBITS|ALLBITS)) != 0) {
				Rectangle rect = list.getCellBounds(index, index);
				list.repaint(rect);
			}
			return (infoflags & (ALLBITS|ABORT)) == 0;
		}
	}

	class WrapEditorKit extends StyledEditorKit {
		ViewFactory defaultFactory = new WrapColumnFactory();
		public ViewFactory getViewFactory() {
			return defaultFactory;
		}
	}

	class WrapColumnFactory implements ViewFactory {
		public View create(Element elem) {
			String kind = elem.getName();
			if (kind != null) {
				if (kind.equals(AbstractDocument.ContentElementName)) {
					return new WrapLabelView(elem);
				} else if (kind.equals(AbstractDocument.ParagraphElementName)) {
					return new ParagraphView(elem);
				} else if (kind.equals(AbstractDocument.SectionElementName)) {
					return new BoxView(elem, View.Y_AXIS);
				} else if (kind.equals(StyleConstants.ComponentElementName)) {
					return new ComponentView(elem);
				} else if (kind.equals(StyleConstants.IconElementName)) {
					return new IconView(elem);
				}
			}
			return new LabelView(elem);
		}
	}

	class WrapLabelView extends LabelView {
		public WrapLabelView(Element elem) {
			super(elem);
		}
		public float getMinimumSpan(int axis) {
			switch (axis) {
			case View.X_AXIS:
				return 0;
			case View.Y_AXIS:
				return super.getMinimumSpan(axis);
			default:
				throw new IllegalArgumentException("Invalid axis: " + axis);
			}
		}
	}
}
