package util;

public class Message {
	public String sender;
	public String reciever;
	public boolean isUser;
	public String time;
	public MessageType type;
	public String content;
	private static String userName;
	public Message(){
	}
	
	public Message(String sender, String reciever, String time, MessageType type, String content){
		this.sender = sender;
		this.reciever = reciever;
		this.isUser = sender.equals(userName);
		this.time = time;
		this.type = type;
		this.content = content;
	}
	
	public static void setUserName(String userName){
		Message.userName = userName;
	}
}
