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
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.*;

import util.*;

public class MessagePanel extends JPanel {
	private final static ImageIcon corruptImg = getCorruptImg();
	private final static ImageIcon fileImg = getFileImg();
	private final static ImageIcon audioImg = getAudioImg();
	
	private static ImageIcon getCorruptImg(){
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(new File(Config.FileCorruptPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Image img = bi.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		ImageIcon imageIcon = new ImageIcon(img);
		return imageIcon;
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
	private static ImageIcon getAudioImg(){
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(new File(Config.PlayRecordPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Image img = bi.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
		ImageIcon imageIcon = new ImageIcon(img);
		return imageIcon;
	}
	
	private ImageIcon imageIcon;
	private Message msg;
	
	public MessagePanel(Message msg){
		super();
		this.msg = msg;
		JLabel sender = new JLabel();
		sender.setText(msg.sender + ":" + "(" + msg.time + ")");
		sender.setFont(new Font("times new roman", Font.PLAIN, 15));
		sender.setBounds(0, 0, 560, 20);
		JTextPane textPane = null;
		JLabel imgLabel = null;
		BufferedImage bufferdImg = null;
		imageIcon = null;
		Box box = null;
		this.setLayout(null);
		switch(msg.type){
		case Text:
			textPane = new JTextPane();
			textPane.setBounds(0, 20, 300, 20);
			textPane.setFont(new Font("微软雅黑", Font.PLAIN, 17));
			textPane.setEditorKit(new WrapEditorKit());
			textPane.setText(msg.content);
			textPane.setOpaque(true);
			textPane.setEditable(false);
			textPane.setFocusable(false);
			textPane.setBackground(new Color(220, 220, 220));
			textPane.setLocation(0, 20);
			textPane.setSize(300, 40);
			textPane.setSize(300, textPane.getPreferredSize().height);
		    this.add(textPane);
		    this.setPreferredSize(new Dimension(560, textPane.getHeight() + 30));
		    break;
		case File:
			box = Box.createHorizontalBox();
			box.setBounds(0, 20, 300, 50);
			JLabel fileImageLabel = new JLabel(fileImg);
			JLabel fileNameLabel = new JLabel(msg.content);
			fileNameLabel.setFont(new Font("华文宋体", Font.BOLD, 15));
			fileNameLabel.setAutoscrolls(true);
			box.add(fileImageLabel);
			box.add(fileNameLabel);
		    this.add(box);
		    this.setPreferredSize(new Dimension(560,box.getHeight() + 20));
		    break;
		case Image:
			imgLabel = new JLabel();
			boolean isCorrupt = false;
			String imgPath = null;
			if(msg.isUser)
				imgPath = msg.content;
			else 
				imgPath = Config.ChatFilePath + msg.content;
			try {
			    bufferdImg = ImageIO.read(new File(imgPath));
			} catch (IOException e) {
				System.out.println("image corrupt");
				isCorrupt = true;
			}
			if(!isCorrupt){
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
				this.setPreferredSize(new Dimension(560, imgLabel.getHeight() + 30));
			}
			else {
				box = Box.createHorizontalBox();
				box.setBounds(0, 20, 300, 50);
				JLabel corruptImageLabel = new JLabel(corruptImg);
				JLabel imageNameLabel = new JLabel("图片文件损坏：" + msg.content);
				imageNameLabel.setFont(new Font("华文宋体", Font.BOLD, 15));
				imageNameLabel.setAutoscrolls(true);
				box.add(corruptImageLabel);
				box.add(imageNameLabel);
			    this.add(box);
			    this.setPreferredSize(new Dimension(560,box.getHeight() + 20));
			    break;
			}
			break;
		case Audio:
			box = Box.createHorizontalBox();
			box.setBounds(0, 20, 300, 50);
			JLabel audioImgLabel = new JLabel(audioImg);
			audioImgLabel.setHorizontalAlignment(JLabel.RIGHT);
			audioImgLabel.setPreferredSize(new Dimension(40, 40));
			box.add(audioImgLabel);
			this.add(box);
			this.setPreferredSize(new Dimension(560,box.getHeight() + 10));
		    break;
		default:
		    break;
		}
		if(msg.isUser){
			sender.setHorizontalAlignment(JLabel.RIGHT);
			if(imgLabel != null)
				imgLabel.setLocation(560 - imgLabel.getWidth(), 20);
			if(box != null)
				box.setLocation(260, 20);
			if(textPane != null)
				textPane.setLocation(260, 20);
		}
		this.add(sender);
	}
	
	public void handelClick(){
		System.out.println("clicked");
		if(msg.type == MessageType.Audio){
			File wavFile = new File(Config.ChatFilePath + msg.content);
			if(wavFile.length() == 0) {
				JOptionPane.showMessageDialog(this, "语音文件损坏", "Waring",  
						JOptionPane.WARNING_MESSAGE);
					return;
			}
			AudioInputStream stream = null;
		    AudioFormat format;
		    DataLine.Info info;
		    Clip clip = null;
		    try {
				stream = AudioSystem.getAudioInputStream(wavFile);
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    format = stream.getFormat();
		    info = new DataLine.Info(Clip.class, format);
		    try {
				clip = (Clip) AudioSystem.getLine(info);
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		    try {
				clip.open(stream);
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    clip.start();
		}
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
