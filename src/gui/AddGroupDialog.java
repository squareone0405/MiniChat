package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import util.Config;

public class AddGroupDialog extends JDialog {
	private JCheckBox[] checkBoxList;
	private MainFrame mf;
	
	public AddGroupDialog(MainFrame mf, ArrayList<String> idList){
		super();
		this.mf = mf;
		this.setSize(250, 400);
		this.setTitle("AddGroup");	
		ImageIcon icon = new ImageIcon(Config.AddGroupPath);
		this.setIconImage(icon.getImage());
		UIManager.put("Button.background", new Color(200,200,200));
		UIManager.put("Button.border", new Color(0, 0, 0));
		this.setLayout(null);
		JPanel panel = new JPanel();		
		panel.setLayout(null);
		checkBoxList = new JCheckBox[idList.size()];
		for(int i = 0; i < idList.size(); ++i){
			checkBoxList[i] = new JCheckBox(idList.get(i));
			checkBoxList[i].setBounds(0, i * 30, this.getWidth() - 20, 30); 
			panel.add(checkBoxList[i]);
		}
		JButton btnConfirm = new JButton("确定");
		btnConfirm.setBounds(this.getWidth()/2-35, this.getHeight() - 75, 60, 30);
		btnConfirm.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						confirm();
					}
				}
				);
		panel.setPreferredSize(new Dimension(this.getWidth() - 20, idList.size()*30));
		JScrollPane pane = new JScrollPane(panel);
		pane.setBounds(0, 0, this.getWidth(), this.getHeight() - 80);
		this.add(pane);
		this.add(btnConfirm);
		this.setVisible(false);
		this.setResizable(false);
		this.setLocationRelativeTo(mf); 
	}
	
	private void confirm(){
		ArrayList<String> groupList = new ArrayList<String>();
		for(int i = 0; i < checkBoxList.length; ++i){
			if(checkBoxList[i].isSelected()){
				groupList.add(checkBoxList[i].getText());
			}
		}
		if(groupList.size() == 0){
			JOptionPane.showMessageDialog(this, "请选择加入群聊的好友", "Waring", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		if(mf.sendAddGroupMsg(groupList))
			this.dispose();
	}
}
