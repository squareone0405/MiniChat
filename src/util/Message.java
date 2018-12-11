package util;

public class Message {
	public String sender;
	public boolean isUser;
	public String time;
	public MessageType type;
	public String content;
	public Message(String sender, boolean isUser, String time, MessageType type, String content){
		this.sender = sender;
		this.isUser = isUser;
		this.time = time;
		this.type = type;
		this.content = content;
	}
}
