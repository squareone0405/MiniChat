package gui;

import java.awt.Color;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileFilter;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javafx.util.Pair;

import util.*;
import database.*;
import socket.*;

public class MainFrame extends JFrame{	
	private DefaultListModel<ContactLabel> contactModel;
	private JList<ContactLabel> contactList;
	private JScrollPane spContacts;
	
	private HashMap<String, DefaultListModel<MessagePanel>> messageModelList;
	private JList<MessagePanel> messageList;
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
	
	private DatabaseManager dbManager;
	
	private String currentFriend;
	private ArrayList<String> friendList;
	private String userName;
	private HashMap<String, String> ipTable;
	private ArrayList<String> idToBeCheck;
	
	private Server server;
	private CentralServerClient csClient;
	private HashMap<String, Client> clientTable;
	private boolean isResponsed;
	
	public MainFrame(String userName, CentralServerClient csClient) {
		super("MiniChat");
		this.userName = userName;
		this.csClient = csClient;
		dbManager = new DatabaseManager(userName);
		initComponent();
		this.setSize(790, 650);
		this.setVisible(true);
		this.setResizable(false);
		this.setLocationRelativeTo(null); 
		server = new Server(this);
		friendList = new ArrayList<String>();
		ipTable = new HashMap<String, String>();
		clientTable = new HashMap<String, Client>();
		messageModelList = new HashMap<String, DefaultListModel<MessagePanel>>();
		isResponsed = true;
		loadAllFormDB();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initComponent(){
		Font font = new Font("微软雅黑", Font.BOLD, 15);
		Font fontMsg = new Font("微软雅黑", Font.PLAIN, 18);
		Font fontAdd = new Font("微软雅黑", Font.PLAIN, 16);
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
		contactModel = new DefaultListModel();
		contactList = new JList(contactModel);
		contactList.setCellRenderer(new ContactRenderer());
		contactList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
            	ContactLabel lbl = (ContactLabel) contactList.getSelectedValue();
            	contactItemClicked(lbl);	
            }
        });
		contactList.setFixedCellHeight(40);
		spContacts = new JScrollPane();
		spContacts.getViewport().add(contactList);
		spContacts.setBounds(0, 50, 200, 530);
		
		//for messages
		messageList = new JList();
		messageList.setCellRenderer(new MessageRenderer());
		messageList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
            	MessagePanel msgPnl = (MessagePanel) messageList.getSelectedValue();
            	if(msgPnl != null)
            		messageItemClicked(msgPnl);
            }
        });
		spMessage = new JScrollPane();
		spMessage.getViewport().add(messageList);		
		spMessage.setBounds(200, 50, 580, 400);
		
		//for the chatpanel
		chatPanel = new JPanel();
		btnSendImage = new JButton();
		btnSendEmoji = new JButton();
		btnSendAudio = new JButton();
		btnSendFile = new JButton();
		btnSendMsg = new JButton("发送");
		
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
		
		//for optionpanel
		optionPanel = new JPanel();
		textNewFriend  = new JTextField();
		btnAddFriend = new JButton("添加好友");
		btnRefresh = new JButton();
		ImageIcon iconRefreshImage = new ImageIcon(Config.RefreshPath);
		Image tempRefresh = iconRefreshImage.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		btnRefresh.setIcon(new ImageIcon(tempRefresh));
		btnRefresh.setOpaque(false);
		btnAddGroup = new JButton("添加群聊");
		btnDeleteFriend = new JButton("删除好友");
		btnClearHistory = new JButton("清空聊天记录");
		lblCurrentChat = new JLabel();
		lblCurrentChat.setFont(new Font("华文宋体", Font.BOLD, 22));
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

		this.getContentPane().add(spContacts);
		this.getContentPane().add(spMessage);	
		this.getContentPane().add(optionPanel);
		this.getContentPane().add(chatPanel);
		
		this.addWindowListener(new WindowAdapter() {  
			public void windowClosing(WindowEvent e) {  
				shutDown();
				System.exit(0);
			}  
		});
	}
	
	private void loadAllFormDB(){
		loadContacts();
		updateOnline();
		loadHistory();
		if(friendList.size() > 0){
			contactItemClicked(contactModel.getElementAt(0));
		}
	}
	
	private void loadContacts(){
		friendList = dbManager.getContactsList();
		for(String friend:friendList){
            contactModel.addElement(new ContactLabel(new Pair<String, Boolean>(friend, false)));
        }
	}
	
	private void loadHistory(){
		for(String friend:friendList){
			DefaultListModel<MessagePanel> model = new DefaultListModel<MessagePanel>();
			ArrayList<Message> msgList = dbManager.getMessageList(friend);
			for(Message msg:msgList){
				model.addElement(new MessagePanel(msg));
			}
			messageModelList.put(friend, model);
        }
	}
	
	private void updateOnline(){
		idToBeCheck = new ArrayList<String>();
		for(String id:friendList){
			idToBeCheck.add(id);
		}
		if(idToBeCheck.size() > 0)
			csClient.checkOnline(idToBeCheck.get(0));
	}

	private void sendMsg() {
		if(!checkBeforeSend())
			return;
		String content = areaMsg.getText();
		Message message = new Message(currentFriend, true, Tools.getCurentTime(), MessageType.Text, content);
		messageModelList.get(currentFriend).addElement(new MessagePanel(message));
		messageList.ensureIndexIsVisible(messageList.getModel().getSize() - 1);
		dbManager.addMessageItem(message);
		if(clientTable.containsKey(currentFriend)){
			clientTable.get(currentFriend).sendMsg(content);
		}
		else {
			clientTable.put(currentFriend, new Client(userName, ipTable.get(currentFriend)));
			clientTable.get(currentFriend).sendMsg(content);
		}
		areaMsg.setText("");
	}
	
	private void sendFile(){
		if(!checkBeforeSend())
			return;
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.showOpenDialog(this);
		File file = fileChooser.getSelectedFile();
		if(file == null || file.length() == 0)
			return;
		Message message = new Message(currentFriend, true, Tools.getCurentTime(), MessageType.File, file.getName());
		messageModelList.get(currentFriend).addElement(new MessagePanel(message));
		messageList.ensureIndexIsVisible(messageList.getModel().getSize() - 1);
		dbManager.addMessageItem(message);
		if(clientTable.containsKey(currentFriend)){
			clientTable.get(currentFriend).sendFile(file);
		}
		else {
			clientTable.put(currentFriend, new Client(userName, ipTable.get(currentFriend)));
			clientTable.get(currentFriend).sendFile(file);
		}		
	}
	
	private void sendImage(){
		if(!checkBeforeSend())
			return;
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
			    "Image files", ImageIO.getReaderFileSuffixes());
		fileChooser.setFileFilter(imageFilter);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.showOpenDialog(this);
		File file = fileChooser.getSelectedFile();
		if(file == null || file.length() == 0)
			return;
		Message message = new Message(currentFriend, true, Tools.getCurentTime(), MessageType.Image, file.getName());
		messageModelList.get(currentFriend).addElement(new MessagePanel(message));
		messageList.ensureIndexIsVisible(messageList.getModel().getSize() - 1);
		dbManager.addMessageItem(message);
		if(clientTable.containsKey(currentFriend)){
			clientTable.get(currentFriend).sendImage(file);
		}
		else {
			clientTable.put(currentFriend, new Client(userName, ipTable.get(currentFriend)));
			clientTable.get(currentFriend).sendImage(file);
		}		
	}
	
	private void sendEmoji(){
		if(!checkBeforeSend())
			return;
	}
	
	private void sendAudio(){
		if(!checkBeforeSend())
			return;
	}
	
	private boolean checkBeforeSend(){
		if(!ipTable.containsKey(currentFriend) || ipTable.get(currentFriend).equals(new String(""))) {
			JOptionPane.showMessageDialog(this, "该好友不在线，无法发送消息", "Waring", 
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}
	
	private void addFriend(){
		System.out.println("add friend");
		String friendStr = textNewFriend.getText();
		if(friendStr.length() != 10){
			JOptionPane.showMessageDialog(this, "请输入好友学号", "Waring", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		if(friendStr.equals(userName)){
			JOptionPane.showMessageDialog(this, "查询学号为本机", "Waring", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		if(isResponsed == true)
			csClient.checkOnline(friendStr);
		isResponsed = false;
		textNewFriend.setText("");
	}
	
	private void refresh(){
		System.out.println("refresh");
		updateOnline();
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
		if(csClient!=null)
			csClient.sendLogout();
		if(server!=null)
			server.closeServer();
		if(clientTable!=null)
			for (Map.Entry<String, Client> entry : clientTable.entrySet()){
			    entry.getValue().disConnect();
			}
		dbManager.shutDown();
	}
	
	private void contactItemClicked(ContactLabel contactLabel){
		String id = contactLabel.getId();
		setCurrnetFriend(id);
	}
	
	private void messageItemClicked(MessagePanel msgPanel){
		System.out.println(msgPanel.toString());
	}
	
	private void setCurrnetFriend(String id){
		currentFriend = id;
    	lblCurrentChat.setText(currentFriend);
    	messageList.setModel(messageModelList.get(id));
    	messageList.ensureIndexIsVisible(messageList.getModel().getSize() - 1);
	}
	
	public void recieveMsg(Message msg){
		String id = msg.friendId;
		if(!messageModelList.containsKey(id)){
			messageModelList.put(id, new DefaultListModel<MessagePanel>());
			this.contactModel.addElement(new ContactLabel(new Pair<String, Boolean>(id, true)));
		}
		/*Iterator<Entry<String, DefaultListModel<MessagePanel>>> it = messageModelList.entrySet().iterator();
	    while (it.hasNext()) {
	    	Entry<String, DefaultListModel<MessagePanel>> pair = (Entry<String, DefaultListModel<MessagePanel>>)it.next();
	    	System.out.println(pair.getKey());
	    }*/
		dbManager.addMessageItem(msg);
		messageModelList.get(id).addElement(new MessagePanel(msg));
		messageList.setModel(messageModelList.get(id));
		messageList.ensureIndexIsVisible(messageList.getModel().getSize() - 1);
	}
	
	public void recieveOnlineResponse(String id, String ip){
		ipTable.put(id, ip);
		boolean isOnline = true;
		if(ip.equals(new String("")))
			isOnline = false;
		if(!friendList.contains(id)){
			contactModel.addElement(new ContactLabel(new Pair<String, Boolean>(id, isOnline)));
			dbManager.addContactsItem(id);
			friendList.add(id);
		}
		else {
			Enumeration<ContactLabel> enu = contactModel.elements();
			while (enu.hasMoreElements()) {
				ContactLabel label = enu.nextElement();
				if(label.getId().equals(id)){
					label.setOnline(isOnline);
					this.repaint();
				}		
		    }
		}
		if(idToBeCheck.contains(id)){
			idToBeCheck.remove(id);
		}
		isResponsed = true;
		if(idToBeCheck.size()>0){
			csClient.checkOnline(idToBeCheck.get(0));
			isResponsed = false;
		}
	}
	
	public void recieveIncorretNo(){
		JOptionPane.showMessageDialog(this, "请输入正确的学号", "Waring", 
				JOptionPane.WARNING_MESSAGE);
	}
}
