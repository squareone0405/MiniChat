package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;

import util.Config;

public class EmojiDialog extends JDialog {
	public EmojiDialog(JTextPane msgPane){
		super();
		this.setSize(410, 300);
		this.setTitle("Emoji");	
		ImageIcon icon = new ImageIcon(Config.SendEmojiPath);
		this.setIconImage(icon.getImage());
		UIManager.put("Button.background", new Color(240,240,240));
		UIManager.put("Button.border", new Color(0, 0, 0));
		this.setLayout(null);
		JPanel panel = new JPanel();		
		panel.setLayout(null);
		Font font = new Font("Segoe UI Emoji", Font.PLAIN, 20);
		Collection<Emoji> list =  EmojiManager.getAll();
		Iterator<Emoji> it = list.iterator();
		for(int i = 0; i < 50; ++i){
			for(int j = 0; j < 13; ++j){
				JButton temp =  new JButton("<html>" + it.next().getUnicode());
				temp.setBounds(j*30, i*30, 30, 30);
				temp.setFont(font);
				temp.addActionListener(
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							msgPane.setText(msgPane.getText() + temp.getText().substring(6));
						}
					}
					);
				panel.add(temp);
			}
		}
		panel.setPreferredSize(new Dimension(this.getWidth(), 1530));
		JScrollPane pane = new JScrollPane(panel);
		pane.setBounds(0, 0, this.getWidth(), this.getHeight());
		this.add(pane);
		this.setVisible(false);
		this.setResizable(false);
		this.setLocationRelativeTo(null); 
		this.addWindowListener(new WindowAdapter() {  
			public void windowClosing(WindowEvent e) {  
				setVisible(false);
			}  
		});
	}
}
