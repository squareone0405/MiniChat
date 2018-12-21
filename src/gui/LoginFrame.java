package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import socket.CentralServerClient;
import util.Config;

public class LoginFrame extends JFrame {
	private JLabel lblUsername;
	private JLabel lblPassword;
	private JTextField textUsername;
	private JPasswordField textPassword;
	private JButton btnLogin;
	private JPanel pnlMain;
	private CentralServerClient client;
	
	public LoginFrame() {
		super();
		client = new CentralServerClient();
		client.setLoginFrame(this);
		this.setSize(400, 300);
		this.setTitle("Login");		
		initComponent();
		this.setVisible(true);
		this.setResizable(false);
		this.setLocationRelativeTo(null); 
	}
	
	private void initComponent(){	
		Font font = new Font("微软雅黑", Font.BOLD, 15);
		UIManager.put("Button.font", font);
		UIManager.put("Button.background", new Color(200,200,200));
		UIManager.put("Button.border", new Color(0, 0, 0));
		UIManager.put("Label.font", font);
		
		ImageIcon icon = new ImageIcon(Config.LoginLogoPath);
		this.setIconImage(icon.getImage());
		
		lblUsername = new JLabel("用户名：");
		lblPassword = new JLabel("密码：");
		textUsername = new JTextField();
		textPassword = new JPasswordField();
		btnLogin = new JButton("登录");	
		
		btnLogin.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						login();
					}
				}
				);
		
		pnlMain = new JPanel();
		pnlMain.setLayout(null);
		pnlMain.setBounds(50, 50, 300, 200);
		pnlMain.add(lblUsername);
		pnlMain.add(lblPassword);
		pnlMain.add(textUsername);
		pnlMain.add(textPassword);
		pnlMain.add(btnLogin);
		lblUsername.setBounds(80, 50, 80, 30);
		lblPassword.setBounds(80, 100, 80, 30);
		textUsername.setBounds(150, 50, 150, 30);
		textPassword.setBounds(150, 100, 150, 30);
		btnLogin.setBounds(150, 150, 80, 30);
		
		textUsername.setText("2016011503");
		textPassword.setText("net2018");
		
		this.add(pnlMain);
	}
	
	private void login(){
		System.out.println("login");
		if(textUsername.getText().length() != 10){
			JOptionPane.showMessageDialog(this, "请输入正确的学号", "Waring",  
					JOptionPane.WARNING_MESSAGE);
				return;
		}
		if(!textPassword.getText().equals(Config.CorrectPassword)) {
			JOptionPane.showMessageDialog(this, "密码错误", "Waring",  
				JOptionPane.WARNING_MESSAGE);
			return;
		}
		if(!client.isConnected()){
			if(!client.connectCentralServer()){
				JOptionPane.showMessageDialog(this, "连接中央服务器失败", "Error",  
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		client.setUserNames(textUsername.getText());
		client.sendLogin();
	}
	
	public void loginConfirm(){
		MainFrame mf = new MainFrame(textUsername.getText(), client);
		client.setMainFrame(mf);
		this.dispose();
	}
	
	public void recieveIncorretLoginNo(){
		JOptionPane.showMessageDialog(this, "请输入正确的学号", "Waring",  
				JOptionPane.WARNING_MESSAGE);
	}
}
