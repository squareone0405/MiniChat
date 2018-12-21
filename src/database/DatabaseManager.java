package database;

import java.sql.*;
import java.util.ArrayList;

import org.apache.derby.jdbc.EmbeddedDriver;

import util.Message;
import util.MessageType;

public class DatabaseManager {
	private Connection connContacts;
	
	public DatabaseManager(String userName){
		Driver derbyEmbeddedDriver = new EmbeddedDriver();
		try {
			DriverManager.registerDriver(derbyEmbeddedDriver);
			connContacts = DriverManager.getConnection("jdbc:derby:" + userName + ";create=true");
			connContacts.setAutoCommit(false);
			DatabaseMetaData dbmd = connContacts.getMetaData();
			ResultSet rs = dbmd.getTables(null, "APP", "contacts".toUpperCase(), null);
			if(!rs.next()){
				String createContacts = "create table contacts (id varchar(30) primary key)";
				Statement stmt = connContacts.createStatement();
		        stmt.execute(createContacts);
		        connContacts.commit();
			}
			rs = dbmd.getTables(null, "APP", "history".toUpperCase(), null);
			if(!rs.next()){
				String createContacts = "create table history (friendId varchar(30) not null,"
						+ "isUser int not null, "
						+ "time varchar(32) not null, "
						+ "messageType int not null, "
						+ "content varchar(256) not null)";
				Statement stmt = connContacts.createStatement();
		        stmt.execute(createContacts);
		        connContacts.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void shutDown(){
		try {
	         DriverManager.getConnection("jdbc:derby:;shutdown=true");
	      } catch (SQLException ex) {
	         if (((ex.getErrorCode() == 50000) &&
	            ("XJ015".equals(ex.getSQLState())))) {
	               System.out.println("Derby shut down normally");
	         } else {
	            System.err.println("Derby did not shut down normally");
	            System.err.println(ex.getMessage());
	         }
	      }
	}
	
	public void addContactsItem(String id){
		PreparedStatement pstmt;
		try {
			pstmt = connContacts.prepareStatement("insert into contacts (id) values(?)");
			pstmt.setString(1, id);
	        pstmt.executeUpdate();
	        connContacts.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> getContactsList(){
		Statement stmt;
		ArrayList<String> contacts = new ArrayList<String>();
		try {
			stmt = connContacts.createStatement();
			ResultSet rs = stmt.executeQuery("select * from contacts");
	        while (rs.next()) {
	        	contacts.add(rs.getString(1));
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return contacts;
	}
	
	public void addMessageItem(Message msg){
		PreparedStatement pstmt;
		try {
			pstmt = connContacts.prepareStatement("insert into history values(?,?,?,?,?)");
			pstmt.setString(1, msg.friendId);
			pstmt.setBoolean(2, msg.isUser);
			pstmt.setString(3, msg.time);
			pstmt.setInt(4, msg.type.getValue());
			pstmt.setString(5, msg.content);
	        pstmt.executeUpdate();
	        connContacts.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Message> getMessageList(String id){
		Statement stmt;
		ArrayList<Message> history = new ArrayList<Message>();
		try {
			stmt = connContacts.createStatement();
			ResultSet rs = stmt.executeQuery("select * from history where friendId = '" + id + "'");
	        while (rs.next()) {
	        	history.add(new Message(rs.getString(1), rs.getBoolean(2), rs.getString(3), 
	        			MessageType.values()[rs.getInt(4)], rs.getString(5)));
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return history;
	}
}
