package gui;

import java.awt.Color;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javafx.util.Pair;

import util.*;
import database.*;
import socket.*;

public class MainFrame extends JFrame{	
	private DefaultListModel<ContactLabel> contactsModel;
	private JList<ContactLabel> listContacts;
	private JScrollPane spContacts;
	
	private DefaultListModel<MessagePanel> messageModel;
	private HashMap<String, DefaultListModel<MessagePanel>> messageModelList;
	private JList<MessagePanel> listMessage;
	private JScrollPane spMessage;
	
	private JPanel chatPanel;
	private JButton btnSendMsg;
	private JButton btnSendImage;
	private JButton btnSendEmoji;
	private JButton btnSendFile;
	private JButton btnSendAudio;
	private JButton btnRefresh;
	private JButton btnAddGroup;
	private JTextArea areaMsg;
	private JScrollPane spMsg;
	
	private JPanel optionPanel;
	private JTextField textNewFriend;
	private JButton btnAddFriend;
	private JButton btnDeleteFriend;
	private JButton btnClearHistory;
	private JLabel lblCurrentChat;
	
	private String currentFriend;
	private ArrayList<String> friendList;
	private String userName;
	
	private DatabaseManager dbManager;
	
	private Server server;
	private Client client;
	
	public MainFrame(String userName) {
		super("MiniChat");
		this.userName = userName;
		dbManager = new DatabaseManager(userName);
		initComponent();
		this.setSize(790, 650);
		this.setVisible(true);
		this.setResizable(false);
		this.setLocationRelativeTo(null); 
		loadContacts();
		if(!userName.equals(new String("0000")))
			server = new Server(this);
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
		contactsModel = new DefaultListModel();
		/*for(int i = 0; i < 6; ++i){
			listModelContacts.addElement(new ContactLabel(new Pair<String, Boolean>(Integer.toString(2016011503), true)));
		}*/
		listContacts = new JList(contactsModel);
		listContacts.setCellRenderer(new ContactRenderer());
		listContacts.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
            	ContactLabel lbl = (ContactLabel) listContacts.getSelectedValue();
            	contactItemClicked(lbl);	
            }
        });
		listContacts.setFixedCellHeight(40);
		spContacts = new JScrollPane();
		spContacts.getViewport().add(listContacts);
		spContacts.setBounds(0, 50, 200, 530);
		
		//for messages
		messageModel = new DefaultListModel();
		/*for(int i = 0; i < 2; ++i){
			if(i%2==0){
				listModelMessage.addElement(new MessagePanel(new Message("2016011503", true, "2018:12:5", MessageType.Text, "ºÃµÄ")));
			}
			else
				listModelMessage.addElement(new MessagePanel(new Message("2016011503", false, "2018:12:5", MessageType.Text, 
						"¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ¹þ")));
		}*/
		listMessage = new JList(messageModel);
		listMessage.setCellRenderer(new MessageRenderer());
		listMessage.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
            	MessagePanel msgPnl = (MessagePanel) listMessage.getSelectedValue();
            	if(msgPnl != null)
            		messageItemClicked(msgPnl);
            }
        });
		spMessage = new JScrollPane();
		spMessage.getViewport().add(listMessage);		
		spMessage.setBounds(200, 50, 580, 400);
		
		//for the chatpanel
		chatPanel = new JPanel();
		btnSendImage = new JButton();
		btnSendEmoji = new JButton();
		btnSendAudio = new JButton();
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
		
		ImageIcon iconAudioImage = new ImageIcon(Config.SendAudioPath);
		Image tempSendAudio = iconAudioImage.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		btnSendAudio.setIcon(new ImageIcon(tempSendAudio));
		btnSendAudio.setOpaque(false);
		
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
		btnSendAudio.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						sendAudio();
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
		btnSendAudio.setBounds(305, 450, 30, 30);
		btnSendMsg.setBounds(700, 580, 80, 30);
		spMsg.setBounds(200, 480, 580, 100);
		chatPanel.setBounds(200, 450, 600, 200);
		chatPanel.setLayout(null);
		chatPanel.add(btnSendImage);
		chatPanel.add(btnSendEmoji);
		chatPanel.add(btnSendFile);
		chatPanel.add(btnSendAudio);
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
		lblCurrentChat = new JLabel();
		lblCurrentChat.setFont(new Font("»ªÎÄËÎÌå", Font.BOLD, 22));
		lblCurrentChat.setHorizontalAlignment(JLabel.RIGHT);
		lblCurrentChat.setOpaque(true);
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
		btnAddGroup.setBounds(250, 10, 80, 30);
		btnDeleteFriend.setBounds(350, 10, 80, 30);
		btnClearHistory.setBounds(450, 10, 100, 30);
		lblCurrentChat.setBounds(650, 10, 120, 30);
		optionPanel.setBounds(0, 0, 790, 50);
		optionPanel.setLayout(null);
		optionPanel.add(textNewFriend);
		optionPanel.add(btnRefresh);
		optionPanel.add(btnAddFriend);
		optionPanel.add(btnAddGroup);
		optionPanel.add(btnDeleteFriend);
		optionPanel.add(btnClearHistory);
		optionPanel.add(lblCurrentChat);
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
		for(String cont:contacts){
            System.out.println(cont);
            contactsModel.addElement(new ContactLabel(new Pair<String, Boolean>(cont, true)));
        }
	}

	private void sendMsg() { 
		System.out.println("send msg");
		System.out.println(areaMsg.getText());
		messageModel.addElement(new MessagePanel
				(new Message(userName, true, "2018:12:5", MessageType.Text, areaMsg.getText())));
		listMessage.ensureIndexIsVisible(listMessage.getModel().getSize() - 1);
		dbManager.addMessageItem(new Message(currentFriend, true, "2018:12:5", MessageType.Text, areaMsg.getText()));
		if(userName.equals(new String("0000")))
			client.sendMsg(areaMsg.getText());
		areaMsg.setText("");
	}
	
	private void sendFile(){
		System.out.println("send file");
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.showOpenDialog(this);
		File file = fileChooser.getSelectedFile();
		System.out.println(file.getName());
	}
	
	private void sendImage(){
		System.out.println("send image");
	}
	
	private void sendEmoji(){
		System.out.println("send emoji");
	}
	
	private void sendAudio(){
		System.out.println("send audio");
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
		contactsModel.addElement(new ContactLabel(new Pair<String, Boolean>(friendStr, true)));
		textNewFriend.setText("");
		
		if(userName.equals(new String("0000"))){
			client = new Client(this, "2016011503");
			client.connectServer(new String("127.0.0.1"), 9876);
		}
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
		if(server!=null)
			server.closeServer();
		if(client!=null)
			client.disConnect();
		dbManager.shutDown();
	}
	
	private void contactItemClicked(ContactLabel contactLabel){
		System.out.println(contactLabel.getId());
		currentFriend = contactLabel.getId();
    	lblCurrentChat.setText(currentFriend);
    	ArrayList<Message> msgList = dbManager.getMessageList(currentFriend);
    	messageModel.clear();
    	Iterator<Message> it = msgList.iterator();
	    while (it.hasNext()) {
	    	messageModel.addElement(new MessagePanel(it.next()));
	    }
	}
	
	private void messageItemClicked(MessagePanel msgPanel){
		System.out.println(msgPanel.toString());
	}
	
	public void recieveMsg(Message msg){
		messageModel.addElement(new MessagePanel(msg));
	}
}
