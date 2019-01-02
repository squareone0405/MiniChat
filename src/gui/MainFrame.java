package gui;

import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

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
	private JTextPane paneMsg;
	private JScrollPane spMsg;
	
	private EmojiDialog emojiDialog;
	private AudioDialog audioDialog;
	
	private JPanel optionPanel;
	private JTextField textNewFriend;
	private JButton btnAddFriend;
	private JButton btnRefresh;
	private JButton btnAddGroup;
	private JButton btnDeleteFriend;
	private JLabel lblCurrentChat;
	
	private DatabaseManager dbManager;
	
	private String currentFriend;
	private ArrayList<String> friendList;
	private String userName;
	private HashMap<String, String> ipTable;
	private HashMap<String, Boolean> idToBeCheck;
	
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
		Client.setUserName(userName);
		Message.setUserName(userName);
		server = new Server(this);
		friendList = new ArrayList<String>();
		ipTable = new HashMap<String, String>();
		clientTable = new HashMap<String, Client>();
		messageModelList = new HashMap<String, DefaultListModel<MessagePanel>>();
		isResponsed = true;
		currentFriend = "";
		loadAllFormDB();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initComponent(){
		Font fontBtn = new Font("微软雅黑", Font.BOLD, 15);
		Font fontLbl = new Font("微软雅黑", Font.BOLD, 15);
		Font fontMsg = new Font("Segoe UI Emoji", Font.PLAIN, 18);
		Font fontAdd = new Font("微软雅黑", Font.PLAIN, 16);
		Color myLightGray = new Color(240,240,240);
		Color myGray = new Color(200,200,200);
		UIManager.put("Button.font", fontBtn);
		UIManager.put("Button.background", myGray);
		UIManager.put("Button.border", new Color(0, 0, 0));
		UIManager.put("TextPane.font", fontMsg);
		UIManager.put("TextField.font", fontAdd);
		UIManager.put("Label.font", fontLbl);
		UIManager.put("List.background", myLightGray);
		UIManager.put("Panel.background", myLightGray);
		
		ImageIcon icon = new ImageIcon(Config.MiniChatLogoPath);
		this.setIconImage(icon.getImage());
		
		//for contacts
		contactModel = new DefaultListModel();
		contactList = new JList(contactModel);
		contactList.setCellRenderer(new ContactRenderer());
		contactList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				int index = contactList.locationToIndex(event.getPoint());
				ContactLabel lbl = (ContactLabel) contactList.getModel().getElementAt(index);
				if(lbl != null)
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
		messageList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				int index = messageList.locationToIndex(event.getPoint());
				MessagePanel msgPnl = (MessagePanel) messageList.getModel().getElementAt(index);
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
		
		paneMsg = new JTextPane();
		spMsg = new JScrollPane(paneMsg);
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
						recordAudio();
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
		btnDeleteFriend = new JButton("删除聊天");
		lblCurrentChat = new JLabel();
		lblCurrentChat.setFont(new Font("华文宋体", Font.BOLD, 20));
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
		textNewFriend.setBounds(10, 10, 110, 30);
		btnAddFriend.setBounds(130, 10, 70, 30);
		btnRefresh.setBounds(210, 10, 30, 30);
		btnAddGroup.setBounds(250, 10, 80, 30);
		btnDeleteFriend.setBounds(350, 10, 80, 30);
		lblCurrentChat.setBounds(450, 10, 320, 30);
		optionPanel.setBounds(0, 0, 790, 50);
		optionPanel.setLayout(null);
		optionPanel.add(textNewFriend);
		optionPanel.add(btnRefresh);
		optionPanel.add(btnAddFriend);
		optionPanel.add(btnAddGroup);
		optionPanel.add(btnDeleteFriend);
		optionPanel.add(lblCurrentChat);
		
		this.getContentPane().add(BorderLayout.CENTER, spContacts);
		this.getContentPane().add(BorderLayout.CENTER, spMessage);	
		this.getContentPane().add(BorderLayout.CENTER, optionPanel);
		this.getContentPane().add(BorderLayout.CENTER, chatPanel);
		
		emojiDialog = new EmojiDialog(paneMsg);
		audioDialog = new AudioDialog(this);
		
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
			String id = contactModel.getElementAt(0).getId();
			setCurrnetFriend(id);
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
		idToBeCheck = new HashMap<String, Boolean>();
		for(String id:friendList){
			String[] tempArray = id.split(",");
			for(int i = 0; i < tempArray.length; ++i){
				idToBeCheck.put(tempArray[i], tempArray.length == 1);
			}
		}
		if(idToBeCheck.size() > 0)
			csClient.checkOnline(idToBeCheck.keySet().iterator().next());
	}

	private void sendMsg() {
		String content = paneMsg.getText();
		String[] toSend = currentFriend.split(",");
		for(int i = 0; i < toSend.length; ++i){
			if(toSend[i].equals(userName))
				continue;
			if(!checkBeforeSend(toSend[i])){
				JOptionPane.showMessageDialog(this, toSend[i] + "不在线", "Waring", 
						JOptionPane.WARNING_MESSAGE);
				continue;
			}
			if(!clientTable.containsKey(toSend[i]) || !ipTable.get(toSend[i]).equals(clientTable.get(toSend[i]).getIp()))
				clientTable.put(toSend[i], new Client(ipTable.get(toSend[i])));
			if(!clientTable.get(toSend[i]).sendMsg(content, currentFriend)){
				JOptionPane.showMessageDialog(this, toSend[i] + "发送失败", "Waring", 
						JOptionPane.WARNING_MESSAGE);
				continue;
			}
		}
		Message message = new Message(userName, currentFriend, Tools.getCurentTime(), MessageType.Text, content);
		messageModelList.get(currentFriend).addElement(new MessagePanel(message));
		messageList.ensureIndexIsVisible(messageList.getModel().getSize() - 1);
		dbManager.addMessageItem(message);
		paneMsg.setText("");
		this.repaint();
	}
	
	private void sendFile(){
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.showOpenDialog(this);
		File file = fileChooser.getSelectedFile();
		if(file == null || file.length() == 0)
			return;
		String[] toSend = currentFriend.split(",");
		for(int i = 0; i < toSend.length; ++i){
			if(toSend[i].equals(userName))
				continue;
			if(!checkBeforeSend(toSend[i])){
				JOptionPane.showMessageDialog(this, toSend[i] + "不在线", "Waring", 
						JOptionPane.WARNING_MESSAGE);
				continue;
			}
			if(!clientTable.containsKey(toSend[i]) || !ipTable.get(toSend[i]).equals(clientTable.get(toSend[i]).getIp()))
				clientTable.put(toSend[i], new Client(ipTable.get(toSend[i])));
			if(!clientTable.get(toSend[i]).sendFile(file, currentFriend)){
				JOptionPane.showMessageDialog(this, toSend[i] + "发送失败", "Waring", 
						JOptionPane.WARNING_MESSAGE);
				continue;
			}
		}
		Message message = new Message(userName, currentFriend, Tools.getCurentTime(), MessageType.File, file.getName());
		messageModelList.get(currentFriend).addElement(new MessagePanel(message));
		messageList.ensureIndexIsVisible(messageList.getModel().getSize() - 1);
		dbManager.addMessageItem(message);
		this.repaint();
	}
	
	private void sendImage(){
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
			    "Image files", ImageIO.getReaderFileSuffixes());
		fileChooser.setFileFilter(imageFilter);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.showOpenDialog(this);
		File file = fileChooser.getSelectedFile();
		if(file == null || file.length() == 0)
			return;
		String[] toSend = currentFriend.split(",");
		for(int i = 0; i < toSend.length; ++i){
			if(toSend[i].equals(userName))
				continue;
			if(!checkBeforeSend(toSend[i])){
				JOptionPane.showMessageDialog(this, toSend[i] + "不在线", "Waring", 
						JOptionPane.WARNING_MESSAGE);
				continue;
			}
			if(!clientTable.containsKey(toSend[i]) || !ipTable.get(toSend[i]).equals(clientTable.get(toSend[i]).getIp()))
				clientTable.put(toSend[i], new Client(ipTable.get(toSend[i])));
			if(!clientTable.get(toSend[i]).sendImage(file, currentFriend)){
				JOptionPane.showMessageDialog(this, toSend[i] + "发送失败", "Waring", 
						JOptionPane.WARNING_MESSAGE);
				continue;
			}
		}
		Message message = new Message(userName, currentFriend, Tools.getCurentTime(), MessageType.Image, file.getAbsolutePath());
		messageModelList.get(currentFriend).addElement(new MessagePanel(message));
		messageList.ensureIndexIsVisible(messageList.getModel().getSize() - 1);
		dbManager.addMessageItem(message);	
		this.repaint();
	}
	
	private void sendEmoji(){
		emojiDialog.setVisible(true);
	}

	private void recordAudio(){
		audioDialog.setVisible(true);
	}
	
	public void sendAudio(File wavFile){
		String[] toSend = currentFriend.split(",");
		for(int i = 0; i < toSend.length; ++i){
			if(toSend[i].equals(userName))
				continue;
			if(!checkBeforeSend(toSend[i])){
				JOptionPane.showMessageDialog(this, toSend[i] + "不在线", "Waring", 
						JOptionPane.WARNING_MESSAGE);
				continue;
			}
			if(!clientTable.containsKey(toSend[i]) || !ipTable.get(toSend[i]).equals(clientTable.get(toSend[i]).getIp()))
				clientTable.put(toSend[i], new Client(ipTable.get(toSend[i])));
			if(!clientTable.get(toSend[i]).sendAudio(wavFile, currentFriend)){
				JOptionPane.showMessageDialog(this, toSend[i] + "发送失败", "Waring", 
						JOptionPane.WARNING_MESSAGE);
				continue;
			}
		}
		Message message = new Message(userName, currentFriend, Tools.getCurentTime(), MessageType.Audio, wavFile.getName());
		messageModelList.get(currentFriend).addElement(new MessagePanel(message));
		messageList.ensureIndexIsVisible(messageList.getModel().getSize() - 1);
		dbManager.addMessageItem(message);	
		this.repaint();
	}
	
	public boolean sendAddGroupMsg(ArrayList<String> groupList){
		if(!checkBeforeAddGroup(groupList))
			return false;
		String groupStr = userName;
		for(int i = 0; i < groupList.size(); ++i){
			groupStr += ",";
			groupStr += groupList.get(i);
		}
		for(int i = 0; i < groupList.size(); ++i){
			if(!clientTable.containsKey(groupList.get(i)) || !ipTable.get(groupList.get(i)).equals(clientTable.get(groupList.get(i)).getIp()))
				clientTable.put(groupList.get(i), new Client(ipTable.get(groupList.get(i))));
			if(!clientTable.get(groupList.get(i)).sendGroupMsg(groupStr)){
				JOptionPane.showMessageDialog(this, groupList.get(i) + "发送失败，请检查好友在线状态", "Waring", 
						JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}
		if(!friendList.contains(groupStr)){
			contactModel.addElement(new ContactLabel(new Pair<String, Boolean>(groupStr, false)));
			messageModelList.put(groupStr, new DefaultListModel<MessagePanel>());
			dbManager.addContactsItem(groupStr);
			friendList.add(groupStr);
		}
		return true;
	}
	
	private boolean checkBeforeSend(String id){
		if(!ipTable.containsKey(id) || ipTable.get(id).equals(new String("")))
			return false;
		return true;
	}
	
	private boolean checkBeforeAddGroup(ArrayList<String> groupId){
		for(int i = 0; i < groupId.size(); ++i){
			if(!ipTable.containsKey(groupId.get(i)) || ipTable.get(groupId.get(i)).equals(new String(""))) {
				JOptionPane.showMessageDialog(this, "好友" + groupId.get(i) + "不在线，无法建群", "Waring", 
						JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}
		String[] groupArray = (String[]) groupId.toArray(new String[groupId.size() + 1]);
		groupArray[groupArray.length - 1] = userName;
		Arrays.sort(groupArray);
		Iterator<String> it = friendList.iterator();
		while(it.hasNext()){
			String temp = it.next();
			String[] tempArray = temp.split(",");
			if(tempArray.length != groupArray.length)
				continue;
			Arrays.sort(tempArray);
			if(Arrays.equals(groupArray, tempArray)){
				JOptionPane.showMessageDialog(this, "该群聊已存在", "Waring", 
						JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}
		return true;
	}
	
	private void addFriend(){
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
		System.out.println(isResponsed);
		System.out.println(idToBeCheck.size());
		idToBeCheck.put(friendStr, true);
		csClient.checkOnline(idToBeCheck.keySet().iterator().next());
		isResponsed = false;
		textNewFriend.setText("");
	}
	
	private void refresh(){
		updateOnline();
	}
	
	private void addGroup(){
		updateOnline();
		ArrayList<String> list = new ArrayList<String>();
		Iterator<String> it = friendList.iterator();
		while(it.hasNext()){
			String temp = it.next();
			if(!temp.contains(","))
				list.add(temp);
		}
		AddGroupDialog dlg = new AddGroupDialog(this, list);
		dlg.setVisible(true);
	}
	
	private void deleteFriend(){
		Enumeration<ContactLabel> enu = contactModel.elements();
		while (enu.hasMoreElements()) {
			ContactLabel label = enu.nextElement();
			if(label.getId().equals(currentFriend)){
				contactModel.removeElement(label);
				break;
			}
	    }
		friendList.remove(currentFriend);
		dbManager.deleteFriend(currentFriend);
		messageModelList.remove(currentFriend);
		if(contactModel.size() > 0)
			setCurrnetFriend(contactModel.get(0).getId());
		else {
			currentFriend = "";
			lblCurrentChat.setText(currentFriend);
		}
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
		if(isResponsed == true){
			if(!contactLabel.isGroup()){
				idToBeCheck.put(contactLabel.getId(), true);
				csClient.checkOnline(idToBeCheck.keySet().iterator().next());
				isResponsed = false;
			}
		}
		contactLabel.handelClick();
		this.repaint();
		String id = contactLabel.getId();
		setCurrnetFriend(id);
	}
	
	private void messageItemClicked(MessagePanel msgPanel){
		msgPanel.handelClick();
	}
	
	private void setCurrnetFriend(String id){
		currentFriend = id;
    	lblCurrentChat.setText(currentFriend);
    	messageList.setModel(messageModelList.get(id));
    	messageList.ensureIndexIsVisible(messageList.getModel().getSize() - 1);
    	this.repaint();
	}
	
	public void recieveMsg(Message msg){
		String[] tempArray = msg.sender.split(",");
		for(int i = 0; i < tempArray.length; ++i){
			idToBeCheck.put(tempArray[i], tempArray.length == 1);
		}
		csClient.checkOnline(idToBeCheck.keySet().iterator().next());
		String id = msg.sender;
		if(!msg.reciever.equals(userName))
			id = msg.reciever;
		System.out.println(id);
		if(!friendList.contains(id)){
			friendList.add(id);
			messageModelList.put(id, new DefaultListModel<MessagePanel>());
			contactModel.addElement(new ContactLabel(new Pair<String, Boolean>(id, true)));
			dbManager.addContactsItem(id);
		}
		dbManager.addMessageItem(msg);
		messageModelList.get(id).addElement(new MessagePanel(msg));
		if(currentFriend.equals(id)){
			messageList.ensureIndexIsVisible(messageList.getModel().getSize() - 1);
			this.repaint();
		}
		else{
			Enumeration<ContactLabel> enu = contactModel.elements();
			while (enu.hasMoreElements()) {
				ContactLabel label = enu.nextElement();
				if(label.getId().equals(id)){
					label.addUnread();
					this.repaint();
					break;
				}
		    }
		}
	}
	
	public void recieveGroupMsg(String groupStr){
		if(!friendList.contains(groupStr)){
			friendList.add(groupStr);
			contactModel.addElement(new ContactLabel(new Pair<String, Boolean>(groupStr, true)));
			messageModelList.put(groupStr, new DefaultListModel<MessagePanel>());
			dbManager.addContactsItem(groupStr);
		}	
	}
	
	public void recieveOnlineResponse(String id, String ip){
		ipTable.put(id, ip);
		boolean isOnline = true;
		if(ip.equals(new String("")))
			isOnline = false;
		if(!friendList.contains(id) && idToBeCheck.get(id)){
			contactModel.addElement(new ContactLabel(new Pair<String, Boolean>(id, isOnline)));
			messageModelList.put(id, new DefaultListModel<MessagePanel>());
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
					break;
				}		
		    }
		}
		if(idToBeCheck.containsKey(id)){
			idToBeCheck.remove(id);
		}
		isResponsed = true;
		if(idToBeCheck.size()>0){
			csClient.checkOnline(idToBeCheck.keySet().iterator().next());
			isResponsed = false;
		}
	}
	
	public void recieveIncorretNo(){
		JOptionPane.showMessageDialog(this, "请输入正确的学号", "Waring", 
				JOptionPane.WARNING_MESSAGE);
	}
}
