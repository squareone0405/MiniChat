package database;

import java.sql.*;
import java.util.ArrayList;

import org.apache.derby.jdbc.EmbeddedDriver;

public class DatabaseManager {
	private Connection connContacts;
	private String userName;
	
	public DatabaseManager(String userName){
		this.userName = userName;
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
				String createContacts = "create table history (sender varchar(30) not null, "
						+ "time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)";
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
}
