package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.UIManager;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;

import util.Config;

public class EmojiFrame extends JFrame {
	private JTextPane msgPane;
	public EmojiFrame(JTextPane msgPane){
		super();
		this.setSize(400, 300);
		this.setTitle("Emoji");	
		ImageIcon icon = new ImageIcon(Config.SendEmojiPath);
		this.setIconImage(icon.getImage());
		UIManager.put("Button.background", new Color(240,240,240));
		UIManager.put("Button.border", new Color(0, 0, 0));
		this.setLayout(null);
		this.msgPane = msgPane;
		Collection<Emoji> list =  EmojiManager.getAll();
		Iterator<Emoji> it = list.iterator();
		for(int i = 0; i < 10; ++i){
			for(int j = 0; j < 10; ++j){
				JButton temp =  new JButton("<html>" + it.next().getUnicode());
				temp.setBounds(i*20, j*20, 20, 20);
				temp.addActionListener(
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							msgPane.setText(msgPane.getText() + temp.getText().substring(6));
						}
					}
					);
				this.add(temp);
			}
		}
		/*Iterator<Emoji> it = list.iterator();
		while(it.hasNext()){
			System.out.println(it.next().getUnicode());
		}*/
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
