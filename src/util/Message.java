package util;

public class Message {
	public String friendId;
	public boolean isUser;
	public String time;
	public MessageType type;
	public String content;
	public Message(){
	}
	public Message(String friendId, boolean isUser, String time, MessageType type, String content){
		this.friendId = friendId;
		this.isUser = isUser;
		this.time = time;
		this.type = type;
		this.content = content;
	}
}
