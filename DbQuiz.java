package Db_Quiz;
import java.io.*;
import java.util.Scanner;
import java.sql.*;
	

public class DbQuiz {

	public static void main(String[] args) throws Exception {
		Scanner scnr = new Scanner(System.in);
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
		System.out.println("Input your data: ");
		
		System.out.println("Name: ");
		String full_name1 = scnr.nextLine();
		System.out.println("User name: ");
		String user_name1 = scnr.nextLine();
		System.out.println("Password: ");
		String pass_word1 = scnr.nextLine();
		System.out.println("Email: ");
		String email1 = scnr.nextLine();
		

		
		if (full_name.equalsIgnoreCase(full_name1)) {
			System.out.println("Name found");
			if (user_name.equalsIgnoreCase(user_name1)) {
				System.out.println("Username found");
				if (pass_word.equalsIgnoreCase(pass_word1)) {
					System.out.println("Password found");
					if (email.equalsIgnoreCase(email1)) {
						System.out.println("Email found");
						System.out.println("The information matches!");
				}
			}
		}
		}
		
	
		System.out.println("Welcome to Java Quest BUDDY!");
		System.out.println("Level: ");
		String level1 = scnr.nextLine();
		System.out.println(level1 + " Level Selected!");
			
	
		
		st.close(); // close statement
		con.close(); // close connection
		System.out.println("Connection Closed....");
	}
}

       /* int easyScore = 0, mediumScore = 0, hardScore = 0;
        
        
        easyScore = runQuiz(scnr, "EASY", easyQuestions, easyChoices, easyAnswers, explanations, 0);
        mediumScore = runQuiz(scnr, "MEDIUM", mediumQuestions, mediumChoices, mediumAnswers, explanations, easyQuestions.length);
        hardScore = runQuiz(scnr, "HARD", hardQuestions, hardChoices, hardAnswers, explanations, easyQuestions.length + mediumQuestions.length);

		
        
		

	    public static int runQuiz(Scanner scnr, String level, String[] questions, String[][] choices, String[] answers, String[] explanations, int offset) {
	        int score = 0;
	        System.out.println("\n" + level + " Questions:");
	        for (int i = 0; i < questions.length; i++) {
	            System.out.println((i + 1) + ". " + questions[i]);
	            for (String option : choices[i]) {
	                System.out.println(option);
	            }
	            System.out.print("Your answer: ");
	            String userAnswer = scnr.nextLine();
	            if (userAnswer.equalsIgnoreCase(answers[i])) {
	                System.out.println("Correct!\n");
	                score++;
	            } else {
	                System.out.println("Incorrect! The correct answer is: " + answers[i]);
	                System.out.println("Explanation: " + explanations[offset + i] + "\n");
	            }
	        }
	        return score;
	   
        	*/



