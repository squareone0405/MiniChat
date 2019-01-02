package gui;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import util.Config;

public class AudioDialog extends JDialog {
	private boolean isRecording;
	private Image imageStart;
	private Image imagePlay;
	private Image imageStop;
	private JButton btnRecord;
	private JButton btnPlay;
	private JButton btnCancel;
	private JButton btnSend;
	AudioFormat format;
	DataLine.Info info;
	AudioFileFormat.Type fileType;
	TargetDataLine line;
	File wavFile;
	MainFrame mf;

	public AudioDialog(MainFrame mf){
		super();
		this.mf = mf;
		this.setSize(260, 150);
		this.setTitle("Audio");	
		ImageIcon icon = new ImageIcon(Config.SendAudioPath);
		this.setIconImage(icon.getImage());
		this.setLayout(null);
		UIManager.put("Button.background", new Color(200,200,200));
		UIManager.put("Button.border", new Color(0, 0, 0));
		JPanel panel = new JPanel();		
		panel.setLayout(null);
		
		fileType = AudioFileFormat.Type.WAVE;
		float sampleRate = 16000;
		int sampleSizeInBits = 8;
		int channels = 2;
		boolean signed = true;
		boolean bigEndian = true;
		format = new AudioFormat(sampleRate, sampleSizeInBits,
				channels, signed, bigEndian);
		info = new DataLine.Info(TargetDataLine.class, format);
		line = null;
		isRecording = false;
		
		ImageIcon iconStart = new ImageIcon(Config.StartRecordPath);
		imageStart = iconStart.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		ImageIcon iconStop = new ImageIcon(Config.StopRecordPath);
		imageStop = iconStop.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		ImageIcon iconPlay = new ImageIcon(Config.PlayRecordPath);
		imagePlay = iconPlay.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		btnRecord = new JButton();
		btnRecord.setIcon(new ImageIcon(imageStart));
		btnRecord.setOpaque(false);
		btnRecord.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if(isRecording)
							stopRecord();
						else
							startRecord();
					}
				}
				);
		btnPlay = new JButton();
		btnPlay.setIcon(new ImageIcon(imagePlay));
		btnPlay.setOpaque(false);
		btnPlay.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						playRecord();
					}
				}
				);
		btnCancel = new JButton("取消");
		btnCancel.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						cancelRecord();
					}
				}
				);
		btnSend = new JButton("发送");
		btnSend.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						sendRecord();
					}
				}
				);
		btnRecord.setBounds(60, 20, 30, 30);
		btnPlay.setBounds(160, 20, 30, 30);
		btnCancel.setBounds(50, 70, 50, 25);
		btnSend.setBounds(150, 70, 50, 25);
		panel.add(btnRecord);
		panel.add(btnPlay);
		panel.add(btnCancel);
		panel.add(btnSend);
		panel.setBounds(0, 0, this.getWidth(), this.getHeight() );
		this.add(panel);
		this.setVisible(false);
		this.setResizable(false);
		this.setLocationRelativeTo(null); 
		this.addWindowListener(new WindowAdapter() {  
			public void windowClosing(WindowEvent e) {  
				setVisible(false);
			}  
		});
	}
	
	private void startRecord(){
		if(wavFile != null){
			wavFile.delete();
		}
		wavFile = null;
		isRecording = true;
        btnRecord.setIcon(new ImageIcon(imageStop));
        new CaptureThread().start();
	}
	
	class CaptureThread extends Thread{
		public void run(){
			wavFile = new File(Config.ChatFilePath + System.currentTimeMillis() + ".wav");
			if (!AudioSystem.isLineSupported(info)) {
				System.out.println("Line not supported");
				System.exit(0);
			}
			try {
				line = (TargetDataLine) AudioSystem.getLine(info);
				line.open(format);
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
			try{
				line.start();
				AudioSystem.write(new AudioInputStream(line), fileType, wavFile);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	private void stopRecord(){
        line.stop();
        line.close();
        isRecording = false;
        btnRecord.setIcon(new ImageIcon(imageStart));
	}
	
	private void playRecord(){
		if(wavFile == null){
			JOptionPane.showMessageDialog(this, "尚未录音，无法播放", "Waring",  
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
	
	private void cancelRecord(){
		if(wavFile != null){
			wavFile.delete();
		}
		wavFile = null;
		this.setVisible(false);
	}
	
	private void sendRecord(){
		if(wavFile == null){
			JOptionPane.showMessageDialog(this, "尚未录音，无法发送", "Waring",  
					JOptionPane.WARNING_MESSAGE);
				return;
		}
		mf.sendAudio(wavFile);
	}
}
