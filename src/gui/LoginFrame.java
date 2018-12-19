package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import socket.Client;
import util.Config;

public class LoginFrame extends JFrame {
	private JLabel lblUsername;
	private JLabel lblPassword;
	private JTextField textUsername;
	private JPasswordField textPassword;
	private JButton btnLogin;
	private JPanel pnlMain;
	
	public LoginFrame() {
		super();
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
		/*Client client = new Client();
		if(!client.connectServer(Config.serverAddr, Config.serverPort)){
			JOptionPane.showMessageDialog(this, "连接中央服务器失败", "Error",  
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(!textPassword.getText().equals(Config.correctPassword)) {
			JOptionPane.showMessageDialog(this, "密码错误", "Waring",  
				JOptionPane.WARNING_MESSAGE);
			return;
		}
		client.sendMsg(textUsername.getText() + "_" + new String(textPassword.getPassword()));
		System.out.println(textUsername.getText() + "_net2018");
		client.sendMsg(textUsername.getText() + "_net2018");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		client.sendMsg("logout2016011503");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		MainFrame mf = new MainFrame(textUsername.getText());
		this.dispose();
	}
}
