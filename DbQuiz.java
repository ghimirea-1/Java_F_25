package Db_Quiz;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DbQuiz {
    public static void main(String[] args) {
        Scanner scnr = new Scanner(System.in);
        String inputFile = "C:/Users/ghimirea/OneDrive - John Brown University/Java Programming/question.txt";
        String outputFile = "results.txt";

        // Check if questions.txt exists
        File file = new File(inputFile);
        if (!file.exists()) {
            System.err.println("Error: The file 'questions.txt' was not found in the current working directory: " +
                               new File(".").getAbsolutePath());
            System.err.println("Please create 'questions.txt' with the correct format (e.g., 'Correct Answer: b') and place it in the above directory.");
            scnr.close();
            return;
        }

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        // Database validation
        try {
            String url = "jdbc:mysql://localhost:3306/quiz_db";
            String username = "root";
            String password = "";
            String query = "SELECT * FROM user_validation";

            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, username, password);
            System.out.println("Connection Established successfully");
            st = con.createStatement();
            rs = st.executeQuery(query);

            if (!rs.next()) {
                System.out.println("No users found in the database. Exiting.");
                scnr.close();
                return;
            }

            // Get credentials from database
            String fullName = rs.getString("Full Name");
            String userName = rs.getString("Username");
            String passWord = rs.getString("Password");
            String email = rs.getString("Email");

            // Get user input
            System.out.println("Input your data:");
            System.out.print("Name: ");
            String fullNameInput = scnr.nextLine();
            System.out.print("User name: ");
            String userNameInput = scnr.nextLine();
            System.out.print("Password: ");
            String passWordInput = scnr.nextLine();
            System.out.print("Email: ");
            String emailInput = scnr.nextLine();

            // Validate credentials
            if (fullName.equalsIgnoreCase(fullNameInput) &&
                userName.equalsIgnoreCase(userNameInput) &&
                passWord.equalsIgnoreCase(passWordInput) &&
                email.equalsIgnoreCase(emailInput)) {
                System.out.println("The information matches!");
            } else {
                System.out.println("Invalid credentials. Exiting.");
                scnr.close();
                return;
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            scnr.close();
            return;
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
            scnr.close();
            return;
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
                if (con != null) con.close();
                System.out.println("Connection Closed....");
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
            }
        }

        // Prompt user for difficulty level
        System.out.println("Welcome to Java Quest BUDDY!");
        System.out.println("Choose a difficulty level:");
        System.out.println("E - EASY");
        System.out.println("M - MEDIUM");
        System.out.println("H - HARD");
        System.out.println("Q - Quit");
        System.out.print("Enter your choice (E/M/H/Q): ");

        String difficulty = null;
        String userInput = scnr.nextLine().trim().toUpperCase();
        while (!userInput.equals("E") && !userInput.equals("M") && !userInput.equals("H") && !userInput.equals("Q")) {
            System.out.println("Invalid choice. Please enter E, M, H, or Q.");
            System.out.print("Enter your choice (E/M/H/Q): ");
            userInput = scnr.nextLine().trim().toUpperCase();
        }

        if (userInput.equals("Q")) {
            System.out.println("Thank you for playing Java Quest BUDDY!");
            scnr.close();
            return;
        }

        // Map user input to difficulty
        switch (userInput) {
            case "E":
                difficulty = "EASY";
                break;
            case "M":
                difficulty = "MEDIUM";
                break;
            case "H":
                difficulty = "HARD";
                break;
        }

        // Count questions for the selected difficulty
        int questionCount = countQuestions(inputFile, difficulty);
        if (questionCount == 0) {
            System.out.println("No questions found for " + difficulty + " in 'questions.txt'. Exiting.");
            System.out.println("Please ensure 'questions.txt' contains questions for the " + difficulty + " level.");
            scnr.close();
            return;
        }

        // Run quiz for the selected difficulty
        int score = runQuiz(scnr, inputFile, difficulty);

        // Show final score
        System.out.println("\nYour final score:");
        System.out.println(difficulty + ": " + score + "/" + questionCount);

        // Write results to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = now.format(formatter);
            writer.write("Quiz Results - " + timestamp + "\n");
            writer.write(difficulty + ": " + score + "/" + questionCount + "\n");
            writer.write("\n");
        } catch (IOException e) {
            System.err.println("Error writing to results file: " + e.getMessage());
        }

        // Close scanner
        scnr.close();
    }

    // Method to count questions for a given difficulty level
    public static int countQuestions(String filePath, String difficulty) {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean inDifficulty = false;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.equals(difficulty)) {
                    inDifficulty = true;
                    continue;
                }
                if (line.equals("EASY") || line.equals("MEDIUM") || line.equals("HARD")) {
                    inDifficulty = false;
                    continue;
                }
                if (inDifficulty && line.matches("\\d+\\..*")) {
                    count++;
                }
            }
        } catch (IOException e) {
            System.err.println("Error counting questions for " + difficulty + ": " + e.getMessage());
        }
        return count;
    }

    // Method to run quiz for a given difficulty level
    public static int runQuiz(Scanner scnr, String filePath, String difficulty) {
        int score = 0;
        int questionNumber = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean inDifficulty = false;
            String currentQuestion = null;
            String[] choices = new String[4];
            int choiceIndex = 0;
            String correctAnswer = null;

            System.out.println("\n" + difficulty + " Questions:");

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Detect difficulty level
                if (line.equals(difficulty)) {
                    inDifficulty = true;
                    continue;
                }
                if (line.equals("EASY") || line.equals("MEDIUM") || line.equals("HARD")) {
                    inDifficulty = false;
                    continue;
                }

                if (!inDifficulty) continue;

                // Detect question
                if (line.matches("\\d+\\..*")) {
                    // Process previous question if complete
                    if (currentQuestion != null && choiceIndex == 4 && correctAnswer != null) {
                        questionNumber++;
                        // Display question and choices
                        System.out.println(questionNumber + ". " + currentQuestion);
                        for (String option : choices) {
                            System.out.println(option);
                        }
                        // Get user answer
                        System.out.print("Your answer: ");
                        String userAnswer = scnr.nextLine().trim().toLowerCase();
                        // Normalize user input (remove trailing ')', take first character)
                        if (userAnswer.endsWith(")")) {
                            userAnswer = userAnswer.substring(0, userAnswer.length() - 1);
                        }
                        if (userAnswer.length() > 1) {
                            userAnswer = userAnswer.substring(0, 1);
                        }
                        // Check answer
                        if (userAnswer.equals(correctAnswer)) {
                            System.out.println("Correct!\n");
                            score++;
                        } else {
                            System.out.println("Incorrect! The correct answer is: " + correctAnswer + "\n");
                        }
                    }
                    // Start new question
                    currentQuestion = line.substring(line.indexOf(".") + 1).trim();
                    choices = new String[4];
                    choiceIndex = 0;
                    correctAnswer = null;
                }
                // Detect choice
                else if (line.matches("[a-d]\\).*")) {
                    if (choiceIndex < 4) {
                        choices[choiceIndex] = line;
                        choiceIndex++;
                    }
                }
                // Detect correct answer
                else if (line.startsWith("Correct Answer:")) {
                    String fullAnswer = line.substring("Correct Answer:".length()).trim();
                    correctAnswer = fullAnswer.length() > 0 ? fullAnswer.substring(0, 1).toLowerCase() : "";
                }
            }

            // Process the last question
            if (currentQuestion != null && choiceIndex == 4 && correctAnswer != null) {
                questionNumber++;
                // Display question and choices
                System.out.println(questionNumber + ". " + currentQuestion);
                for (String option : choices) {
                    System.out.println(option);
                }
                // Get user answer
                System.out.print("Your answer: ");
                String userAnswer = scnr.nextLine().trim().toLowerCase();
                // Normalize user input (remove trailing ')', take first character)
                if (userAnswer.endsWith(")")) {
                    userAnswer = userAnswer.substring(0, userAnswer.length() - 1);
                }
                if (userAnswer.length() > 1) {
                    userAnswer = userAnswer.substring(0, 1);
                }
                // Check answer
                if (userAnswer.equals(correctAnswer)) {
                    System.out.println("Correct!\n");
                    score++;
                } else {
                    System.out.println("Incorrect! The correct answer is: " + correctAnswer + "\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading quiz file for " + difficulty + ": " + e.getMessage());
        }

        return score;
    }
}
