package Db_Quiz;
import java.io.*;
import java.sql.*;
	

public class DbQuiz {

	public static void main(String[] args) throws Exception {
		String url = "jdbc:mysql://localhost:3306/quiz_db"; // DB details
		String username = "root"; // MySQL credentials
		String password = "";
		String query = "select *from user_validation"; // query to be run
		
		Class.forName("com.mysql.cj.jdbc.Driver"); // Driver name
		Connection con = DriverManager.getConnection(url, username, password);
		System.out.println("Connection Established successfully");
		Statement st = con.createStatement();
		ResultSet rs= st.executeQuery(query); // Execute query
		rs.next();
		String full_name = rs.getString("Full Name");
		String user_name = rs.getString("Username");
		String pass_word = rs.getString("Password");
		String email = rs.getString("Email");
		
		System.out.println(full_name); // Print result on console
		System.out.println(user_name);
		System.out.println(pass_word);
		System.out.println(email);
		st.close(); // close statement
		con.close(); // close connection
		System.out.println("Connection Closed....");
	}

}
