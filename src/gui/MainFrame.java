package gui;

import java.awt.Color;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import java.util.ArrayList;

import javafx.util.Pair;

import util.*;
import database.*;
import socket.*;

public class MainFrame extends JFrame{	
	private DefaultListModel<Pair<String, Boolean>> listModelContacts;
	private JList listContacts;
	private JScrollPane spContacts;
	
	private DefaultListModel<Message> listModelMessage;
	private JList listMessage;
	private JScrollPane spMessage;
	
	private JPanel chatPanel;
	private JButton btnSendMsg;
	private JButton btnSendImage;
	private JButton btnSendEmoji;
	private JButton btnSendFile;
	private JButton btnRefresh;
	private JButton btnAddGroup;
	private JTextArea areaMsg;
	private JScrollPane spMsg;
	
	private JPanel optionPanel;
	private JTextField textNewFriend;
	private JButton btnAddFriend;
	private JButton btnDeleteFriend;
	private JButton btnClearHistory;
	
	private DatabaseManager dbManager;
	
	private Server server;
	
	public MainFrame() {
		super("MiniChat");
		dbManager = new DatabaseManager("2016011503");
		initComponent();
		this.setSize(790, 650);
		this.setVisible(true);
		this.setResizable(false);
		this.setLocationRelativeTo(null); 
		loadContacts();
		server = new Server();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initComponent(){
		Font font = new Font("Î¢ÈíÑÅºÚ", Font.BOLD, 15);
		Font fontMsg = new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 18);
		Font fontAdd = new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 16);
		Color myLightGray = new Color(240,240,240);
		Color myGray = new Color(200,200,200);
		UIManager.put("Button.font", font);
		UIManager.put("Button.background", myGray);
		UIManager.put("Button.border", new Color(0, 0, 0));
		UIManager.put("TextArea.font", fontMsg);
		UIManager.put("TextField.font", fontAdd);
		UIManager.put("Label.font", font);
		UIManager.put("List.background", myLightGray);
		UIManager.put("Panel.background", myLightGray);
		
		ImageIcon icon = new ImageIcon(Config.MiniChatLogoPath);
		this.setIconImage(icon.getImage());
		
		//for contacts
		listModelContacts = new DefaultListModel();
		for(int i = 0; i < 6; ++i){
			listModelContacts.addElement(new Pair<String, Boolean>(Integer.toString(2016011503), true));
		}
		listContacts = new JList(listModelContacts);
		listContacts.setCellRenderer(new ContactRenderer());
		listContacts.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
            	Pair<String, Boolean> pair = (Pair<String, Boolean>) listContacts.getSelectedValue();
            	System.out.println(pair.getKey());
            }
        });
		listContacts.setFixedCellHeight(40);
		spContacts = new JScrollPane();
		spContacts.getViewport().add(listContacts);
		spContacts.setBounds(0, 50, 200, 530);
		
		//for messages
		listModelMessage = new DefaultListModel();
		for(int i = 0; i < 2; ++i){
			if(i%2==0){
				listModelMessage.addElement(new Message("2016011503", true, "2018:12:5", MessageType.Text, "ºÃµÄ"));
			}
			else
				listModelMessage.addElement(new Message("2016011503", false, "2018:12:5", MessageType.Text, 
						"¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ"));
		}
		listMessage = new JList(listModelMessage);
		listMessage.setCellRenderer(new MessageRenderer());
		listMessage.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
            	Message msg = (Message) listMessage.getSelectedValue();
            	System.out.println(msg.content);
            }
        });
		spMessage = new JScrollPane();
		spMessage.getViewport().add(listMessage);		
		spMessage.setBounds(200, 50, 580, 400);
		
		//for the chatpanel
		chatPanel = new JPanel();
		btnSendImage = new JButton();
		btnSendEmoji = new JButton();
		btnSendFile = new JButton();
		btnSendMsg = new JButton("·¢ËÍ");
		
		ImageIcon iconSendImage = new ImageIcon(Config.SendImagePath);
		Image tempSendImage = iconSendImage.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		btnSendImage.setIcon(new ImageIcon(tempSendImage));
		btnSendImage.setOpaque(false);
		
		ImageIcon iconFileImage = new ImageIcon(Config.SendFilePath);
		Image tempSendFile = iconFileImage.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		btnSendFile.setIcon(new ImageIcon(tempSendFile));
		btnSendFile.setOpaque(false);
		
		ImageIcon iconEmojiImage = new ImageIcon(Config.SendEmojiPath);
		Image tempSendEmoji = iconEmojiImage.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		btnSendEmoji.setIcon(new ImageIcon(tempSendEmoji));
		btnSendEmoji.setOpaque(false);
		
		areaMsg = new JTextArea();
		areaMsg.setLineWrap(true);
		spMsg = new JScrollPane(areaMsg);
		btnSendFile.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						sendFile();
					}
				}
				);
		btnSendImage.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						sendImage();
					}
				}
				);
		btnSendEmoji.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						sendEmoji();
					}
				}
				);
		btnSendMsg.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						sendMsg();
					}
				}
				);
		btnSendImage.setBounds(200, 450, 30, 30);
		btnSendEmoji.setBounds(235, 450, 30, 30);
		btnSendFile.setBounds(270, 450, 30, 30);
		btnSendMsg.setBounds(700, 580, 80, 30);
		spMsg.setBounds(200, 480, 580, 100);
		chatPanel.setBounds(200, 450, 600, 200);
		chatPanel.setLayout(null);
		chatPanel.add(btnSendImage);
		chatPanel.add(btnSendEmoji);
		chatPanel.add(btnSendFile);
		chatPanel.add(btnSendMsg);
		chatPanel.add(spMsg);
		//chatPanel.setBackground(Color.GREEN);
		
		//for optionpanel
		optionPanel = new JPanel();
		textNewFriend  = new JTextField();
		btnAddFriend = new JButton("Ìí¼ÓºÃÓÑ");
		btnRefresh = new JButton();
		ImageIcon iconRefreshImage = new ImageIcon(Config.RefreshPath);
		Image tempRefresh = iconRefreshImage.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		btnRefresh.setIcon(new ImageIcon(tempRefresh));
		btnRefresh.setOpaque(false);
		btnAddGroup = new JButton("Ìí¼ÓÈºÁÄ");
		btnDeleteFriend = new JButton("É¾³ýºÃÓÑ");
		btnClearHistory = new JButton("Çå¿ÕÁÄÌì¼ÇÂ¼");
		btnAddFriend.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						addFriend();
					}
				}
				);
		btnRefresh.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						refresh();
					}
				}
				);
		btnAddGroup.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						addGroup();
					}
				}
				);
		btnDeleteFriend.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						deleteFriend();
					}
				}
				);
		btnClearHistory.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						clearHistory();
					}
				}
				);
		textNewFriend.setBounds(10, 10, 110, 30);
		btnAddFriend.setBounds(130, 10, 70, 30);
		btnRefresh.setBounds(210, 10, 30, 30);
		btnAddGroup.setBounds(300, 10, 80, 30);
		btnDeleteFriend.setBounds(400, 10, 80, 30);
		btnClearHistory.setBounds(500, 10, 100, 30);
		optionPanel.setBounds(0, 0, 790, 50);
		optionPanel.setLayout(null);
		optionPanel.add(textNewFriend);
		optionPanel.add(btnRefresh);
		optionPanel.add(btnAddFriend);
		optionPanel.add(btnAddGroup);
		optionPanel.add(btnDeleteFriend);
		optionPanel.add(btnClearHistory);
		//optionPanel.setBackground(Color.blue);
			
		//this.setLayout(null);
		this.add(spContacts);
		this.add(spMessage);	
		this.add(optionPanel);
		this.add(chatPanel);
		
		this.addWindowListener(new WindowAdapter() {  
			public void windowClosing(WindowEvent e) {  
				shutDown();
				System.exit(0);
			}  
		});
	}
	
	private void loadContacts(){
		ArrayList<String> contacts = dbManager.getContactsList();
		for(String tmp:contacts){
            System.out.println(tmp);
            listModelContacts.addElement(new Pair<String, Boolean>(tmp, true));
        }
	}

	private void sendMsg() { 
		System.out.println("send msg");
		System.out.println(areaMsg.getText());
		listModelMessage.addElement(new Message("2016011503", true, "2018:12:5", MessageType.Text, areaMsg.getText()));
		listMessage.ensureIndexIsVisible(listMessage.getModel().getSize() - 1);
		areaMsg.setText(null);
	}
	
	private void sendFile(){
		System.out.println("send file");
	}
	
	private void sendImage(){
		System.out.println("send image");
	}
	
	private void sendEmoji(){
		System.out.println("send emoji");
	}
	
	private void addFriend(){
		System.out.println("add friend");
		String friendStr = textNewFriend.getText();
		if(friendStr.equals("")){
			JOptionPane.showMessageDialog(this, "ÇëÊäÈëºÃÓÑÑ§ºÅ", "Waring", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		if(friendStr.length() >= 20){
			JOptionPane.showMessageDialog(this, "ÊäÈëÄÚÈÝ¹ý³¤", "Waring", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		dbManager.addContactsItem(friendStr);
		listModelContacts.addElement(new Pair<String, Boolean>(friendStr, true));
		/*ContactLabel lbl = (ContactLabel)listContacts.getComponent(0);
		lbl.setOnline(false);*/
		textNewFriend.setText("");
	}
	
	private void refresh(){
		System.out.println("refresh");
	}
	
	private void addGroup(){
		System.out.println("add group");
	}
	
	private void deleteFriend(){
		System.out.println("delete friend");
	}
	
	private void clearHistory(){
		System.out.println("clear history");
	}
	
	private void shutDown(){
		server.closeServer();
		dbManager.shutDown();
	}
}
