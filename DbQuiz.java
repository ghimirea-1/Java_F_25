package Db_Quiz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import javax.imageio.ImageIO;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DbQuiz {
    private static JFrame frame;
    private static JTextField nameField, usernameField, emailField;
    private static JPasswordField passwordField;
    private static String difficulty;
    private static int currentQuestionIndex;
    private static int score;
    private static List<Question> questions;
    private static List<QuizResult> quizHistory = new ArrayList<>();
    private static String inputFile = "C:/Users/ghimirea/OneDrive - John Brown University/Java Programming/question.txt";
    private static double scaleFactor = 1.0;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Java Quest BUDDY");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 450);
            frame.setLocationRelativeTo(null);
            frame.setResizable(true);

            // Add resize listener to adjust component sizes
            frame.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    Dimension size = frame.getSize();
                    scaleFactor = Math.min(size.width / 500.0, size.height / 450.0);
                    updateCurrentScreen();
                }
            });

            createAndShowLoginGUI();
            frame.setVisible(true);
        });
    }

    private static void updateCurrentScreen() {
        // Rebuild the current screen based on the frame's title
        String title = frame.getTitle();
        if (title.contains("Login")) {
            createAndShowLoginGUI();
        } else if (title.contains("Select Difficulty")) {
            createAndShowDifficultyGUI();
        } else if (title.contains("Quiz")) {
            showQuestion();
        } else if (title.contains("Results")) {
            showResults();
        }
    }

    private static Font getScaledFont(int baseSize) {
        return new Font("Roboto", Font.PLAIN, (int) (baseSize * scaleFactor));
    }

    private static Font getScaledBoldFont(int baseSize) {
        return new Font("Roboto", Font.BOLD, (int) (baseSize * scaleFactor));
    }

    private static void createAndShowLoginGUI() {
        frame.getContentPane().removeAll();
        frame.setTitle("Java Quest BUDDY - Login");

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets((int) (10 * scaleFactor), (int) (10 * scaleFactor), (int) (10 * scaleFactor), (int) (10 * scaleFactor));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel titleLabel = new JLabel("Welcome to Java Quest BUDDY");
        titleLabel.setFont(getScaledBoldFont(20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        JButton guestButton = new JButton("Continue as Guest");
        guestButton.setFont(getScaledFont(14));
        guestButton.setBackground(new Color(255, 159, 64));
        guestButton.setForeground(Color.WHITE);
        guestButton.setBorder(BorderFactory.createEmptyBorder((int) (10 * scaleFactor), (int) (20 * scaleFactor), (int) (10 * scaleFactor), (int) (20 * scaleFactor)));
        guestButton.addActionListener(e -> createAndShowDifficultyGUI());
        gbc.gridy++;
        panel.add(guestButton, gbc);

        JLabel loginLabel = new JLabel("Or Login with Credentials");
        loginLabel.setFont(getScaledFont(16));
        gbc.gridy++;
        panel.add(loginLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(new JLabel("Name:") {{ setFont(getScaledFont(14)); }}, gbc);
        nameField = new JTextField(20);
        nameField.setFont(getScaledFont(14));
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Username:") {{ setFont(getScaledFont(14)); }}, gbc);
        usernameField = new JTextField(20);
        usernameField.setFont(getScaledFont(14));
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Password:") {{ setFont(getScaledFont(14)); }}, gbc);
        passwordField = new JPasswordField(20);
        passwordField.setFont(getScaledFont(14));
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Email:") {{ setFont(getScaledFont(14)); }}, gbc);
        emailField = new JTextField(20);
        emailField.setFont(getScaledFont(14));
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(getScaledFont(14));
        loginButton.setBackground(new Color(66, 133, 244));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorder(BorderFactory.createEmptyBorder((int) (10 * scaleFactor), (int) (20 * scaleFactor), (int) (10 * scaleFactor), (int) (20 * scaleFactor)));
        loginButton.addActionListener(e -> validateCredentials());
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        frame.add(panel);
        frame.revalidate();
        frame.repaint();
    }

    private static void validateCredentials() {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            String url = "jdbc:mysql://localhost:3306/quiz_db";
            String username = "root";
            String password = "";
            String query = "SELECT * FROM user_validation";

            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, username, password);
            st = con.createStatement();
            rs = st.executeQuery(query);

            boolean userFound = false;
            while (rs.next()) {
                String fullName = rs.getString("Full Name");
                String userName = rs.getString("Username");
                String passWord = rs.getString("Password");
                String email = rs.getString("Email");

                if (fullName.equalsIgnoreCase(nameField.getText()) &&
                    userName.equalsIgnoreCase(usernameField.getText()) &&
                    passWord.equalsIgnoreCase(new String(passwordField.getPassword())) &&
                    email.equalsIgnoreCase(emailField.getText())) {
                    userFound = true;
                    break;
                }
            }

            if (!userFound) {
                JOptionPane.showMessageDialog(frame, "Invalid credentials or no matching user found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            createAndShowDifficultyGUI();
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(frame, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(frame, "Error closing database resources: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void createAndShowDifficultyGUI() {
        frame.getContentPane().removeAll();
        frame.setTitle("Java Quest BUDDY - Select Difficulty");

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets((int) (10 * scaleFactor), (int) (10 * scaleFactor), (int) (10 * scaleFactor), (int) (10 * scaleFactor));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel titleLabel = new JLabel("Choose a Difficulty Level");
        titleLabel.setFont(getScaledBoldFont(20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);

        ButtonGroup difficultyGroup = new ButtonGroup();
        JRadioButton easyButton = createRadioButton("Easy");
        JRadioButton mediumButton = createRadioButton("Medium");
        JRadioButton hardButton = createRadioButton("Hard");
        difficultyGroup.add(easyButton);
        difficultyGroup.add(mediumButton);
        difficultyGroup.add(hardButton);

        gbc.gridy++;
        panel.add(easyButton, gbc);
        gbc.gridy++;
        panel.add(mediumButton, gbc);
        gbc.gridy++;
        panel.add(hardButton, gbc);

        JButton startButton = new JButton("Start Quiz");
        startButton.setFont(getScaledFont(14));
        startButton.setBackground(new Color(66, 133, 244));
        startButton.setForeground(Color.WHITE);
        startButton.setBorder(BorderFactory.createEmptyBorder((int) (10 * scaleFactor), (int) (20 * scaleFactor), (int) (10 * scaleFactor), (int) (20 * scaleFactor)));
        startButton.addActionListener(e -> {
            if (easyButton.isSelected()) difficulty = "EASY";
            else if (mediumButton.isSelected()) difficulty = "MEDIUM";
            else if (hardButton.isSelected()) difficulty = "HARD";
            else {
                JOptionPane.showMessageDialog(frame, "Please select a difficulty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            startQuiz();
        });
        gbc.gridy++;
        panel.add(startButton, gbc);

        JButton quitButton = new JButton("Quit");
        quitButton.setFont(getScaledFont(14));
        quitButton.setBackground(new Color(219, 68, 55));
        quitButton.setForeground(Color.WHITE);
        quitButton.setBorder(BorderFactory.createEmptyBorder((int) (10 * scaleFactor), (int) (20 * scaleFactor), (int) (10 * scaleFactor), (int) (20 * scaleFactor)));
        quitButton.addActionListener(e -> System.exit(0));
        gbc.gridy++;
        panel.add(quitButton, gbc);

        frame.add(panel);
        frame.revalidate();
        frame.repaint();
    }

    private static JRadioButton createRadioButton(String text) {
        JRadioButton radioButton = new JRadioButton(text);
        radioButton.setFont(getScaledFont(16));
        radioButton.setBackground(Color.WHITE);
        radioButton.setFocusPainted(false);
        return radioButton;
    }

    private static void startQuiz() {
        File file = new File(inputFile);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(frame, "Error: The file 'questions.txt' was not found.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        questions = loadQuestions(inputFile, difficulty);
        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No questions found for " + difficulty + " in 'questions.txt'.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        currentQuestionIndex = 0;
        score = 0;
        showQuestion();
    }

    private static void showQuestion() {
        frame.getContentPane().removeAll();
        frame.setTitle("Java Quest BUDDY - " + difficulty + " Quiz");

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets((int) (10 * scaleFactor), (int) (10 * scaleFactor), (int) (10 * scaleFactor), (int) (10 * scaleFactor));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;

        Question question = questions.get(currentQuestionIndex);
        JLabel questionLabel = new JLabel("<html><b>" + (currentQuestionIndex + 1) + ". " + question.text + "</b></html>");
        questionLabel.setFont(getScaledBoldFont(16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(questionLabel, gbc);

        ButtonGroup optionsGroup = new ButtonGroup();
        JRadioButton[] optionButtons = new JRadioButton[4];
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = createRadioButton(question.choices[i]);
            optionsGroup.add(optionButtons[i]);
            gbc.gridy++;
            gbc.gridwidth = 1;
            panel.add(optionButtons[i], gbc);
        }

        JButton submitButton = new JButton(currentQuestionIndex < questions.size() - 1 ? "Next" : "Finish");
        submitButton.setFont(getScaledFont(14));
        submitButton.setBackground(new Color(66, 133, 244));
        submitButton.setForeground(Color.WHITE);
        submitButton.setBorder(BorderFactory.createEmptyBorder((int) (10 * scaleFactor), (int) (20 * scaleFactor), (int) (10 * scaleFactor), (int) (20 * scaleFactor)));
        submitButton.addActionListener(e -> {
            String selectedAnswer = null;
            for (int i = 0; i < optionButtons.length; i++) {
                if (optionButtons[i].isSelected()) {
                    selectedAnswer = String.valueOf((char) ('a' + i));
                    break;
                }
            }
            if (selectedAnswer == null) {
                JOptionPane.showMessageDialog(frame, "Please select an option.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (selectedAnswer.equals(question.correctAnswer)) {
                score++;
            }
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                showQuestion();
            } else {
                showResults();
            }
        });
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(submitButton, gbc);

        frame.add(panel);
        frame.revalidate();
        frame.repaint();
    }

    private static void showResults() {
        // Store the current quiz result
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = now.format(formatter);
        quizHistory.add(new QuizResult(difficulty, score, questions.size(), timestamp));

        frame.getContentPane().removeAll();
        frame.setTitle("Java Quest BUDDY - Results");

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets((int) (10 * scaleFactor), (int) (10 * scaleFactor), (int) (10 * scaleFactor), (int) (10 * scaleFactor));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;

        // Calculate score percentage
        double scorePercentage = (double) score / questions.size() * 100;
        String memeUrl;
        String memeAltText;
        if (scorePercentage <= 39) {
            memeUrl = "https://en.meming.world/images/en/thumb/2/28/Sad_Affleck_meme_1.jpg/240px-Sad_Affleck_meme_1.jpg";
            memeAltText = "Sad Affleck meme";
        } else if (scorePercentage <= 69) {
            memeUrl = "https://thunderdungeon.com/wp-content/uploads/2024/12/ADHD-memes-23-20241205-600x621.jpg";
            memeAltText = "ADHD meme";
        } else if (scorePercentage <= 89) {
            memeUrl = "https://i.imgflip.com/ypp2a.jpg";
            memeAltText = "Not Bad meme";
        } else {
            memeUrl = "https://i.imgflip.com/9qhv2n.jpg";
            memeAltText = "Success Kid meme";
        }

        // Load and display meme
        try {
            URL url = new URL(memeUrl);
            Image image = ImageIO.read(url);
            int memeWidth = (int) (200 * scaleFactor);
            Image scaledImage = image.getScaledInstance(memeWidth, -1, Image.SCALE_SMOOTH);
            JLabel memeLabel = new JLabel(new ImageIcon(scaledImage));
            memeLabel.setToolTipText(memeAltText);
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(memeLabel, gbc);
        } catch (IOException e) {
            JLabel errorLabel = new JLabel("Failed to load meme image");
            errorLabel.setFont(getScaledFont(14));
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(errorLabel, gbc);
        }

        JLabel resultLabel = new JLabel("Your Final Score: " + score + "/" + questions.size());
        resultLabel.setFont(getScaledBoldFont(20));
        gbc.gridy++;
        panel.add(resultLabel, gbc);

        JLabel difficultyLabel = new JLabel("Difficulty: " + difficulty);
        difficultyLabel.setFont(getScaledFont(16));
        gbc.gridy++;
        panel.add(difficultyLabel, gbc);

        JButton playAgainButton = new JButton("Play Again");
        playAgainButton.setFont(getScaledFont(14));
        playAgainButton.setBackground(new Color(66, 133, 244));
        playAgainButton.setForeground(Color.WHITE);
        playAgainButton.setBorder(BorderFactory.createEmptyBorder((int) (10 * scaleFactor), (int) (20 * scaleFactor), (int) (10 * scaleFactor), (int) (20 * scaleFactor)));
        playAgainButton.addActionListener(e -> createAndShowDifficultyGUI());
        gbc.gridy++;
        panel.add(playAgainButton, gbc);

        JButton historyButton = new JButton("View History");
        historyButton.setFont(getScaledFont(14));
        historyButton.setBackground(new Color(255, 159, 64));
        historyButton.setForeground(Color.WHITE);
        historyButton.setBorder(BorderFactory.createEmptyBorder((int) (10 * scaleFactor), (int) (20 * scaleFactor), (int) (10 * scaleFactor), (int) (20 * scaleFactor)));
        historyButton.addActionListener(e -> showHistory());
        gbc.gridy++;
        panel.add(historyButton, gbc);

        JButton exitButton = new JButton("Exit");
        exitButton.setFont(getScaledFont(14));
        exitButton.setBackground(new Color(219, 68, 55));
        exitButton.setForeground(Color.WHITE);
        exitButton.setBorder(BorderFactory.createEmptyBorder((int) (10 * scaleFactor), (int) (20 * scaleFactor), (int) (10 * scaleFactor), (int) (20 * scaleFactor)));
        exitButton.addActionListener(e -> System.exit(0));
        gbc.gridy++;
        panel.add(exitButton, gbc);

        frame.add(panel);
        frame.revalidate();
        frame.repaint();
    }

    private static void showHistory() {
        JDialog historyDialog = new JDialog(frame, "Quiz History", true);
        historyDialog.setSize((int) (600 * scaleFactor), (int) (400 * scaleFactor));
        historyDialog.setLocationRelativeTo(frame);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JTextArea historyArea = new JTextArea();
        historyArea.setFont(getScaledFont(14));
        historyArea.setEditable(false);
        historyArea.setBackground(Color.WHITE);

        if (quizHistory.isEmpty()) {
            historyArea.setText("No quiz history available.");
        } else {
            StringBuilder historyText = new StringBuilder();
            for (QuizResult result : quizHistory) {
                historyText.append("Date: ").append(result.timestamp)
                           .append("\nDifficulty: ").append(result.difficulty)
                           .append("\nScore: ").append(result.score).append("/").append(result.totalQuestions)
                           .append("\n\n");
            }
            historyArea.setText(historyText.toString());
        }

        JScrollPane scrollPane = new JScrollPane(historyArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.setFont(getScaledFont(14));
        closeButton.setBackground(new Color(66, 133, 244));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBorder(BorderFactory.createEmptyBorder((int) (10 * scaleFactor), (int) (20 * scaleFactor), (int) (10 * scaleFactor), (int) (20 * scaleFactor)));
        closeButton.addActionListener(e -> historyDialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        historyDialog.add(panel);
        historyDialog.setVisible(true);
    }

    private static List<Question> loadQuestions(String filePath, String difficulty) {
        List<Question> questions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean inDifficulty = false;
            Question currentQuestion = null;
            String[] choices = new String[4];
            int choiceIndex = 0;

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

                if (!inDifficulty) continue;

                if (line.matches("\\d+\\..*")) {
                    if (currentQuestion != null && choiceIndex == 4 && currentQuestion.correctAnswer != null) {
                        currentQuestion.choices = choices;
                        questions.add(currentQuestion);
                    }
                    currentQuestion = new Question();
                    currentQuestion.text = line.substring(line.indexOf(".") + 1).trim();
                    choices = new String[4];
                    choiceIndex = 0;
                } else if (line.matches("[a-d]\\).*")) {
                    if (choiceIndex < 4) {
                        choices[choiceIndex] = line;
                        choiceIndex++;
                    }
                } else if (line.startsWith("Correct Answer:")) {
                    String fullAnswer = line.substring("Correct Answer:".length()).trim();
                    currentQuestion.correctAnswer = fullAnswer.length() > 0 ? fullAnswer.substring(0, 1).toLowerCase() : "";
                }
            }

            if (currentQuestion != null && choiceIndex == 4 && currentQuestion.correctAnswer != null) {
                currentQuestion.choices = choices;
                questions.add(currentQuestion);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error reading quiz file for " + difficulty + ": " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return questions;
    }

    private static class Question {
        String text;
        String[] choices;
        String correctAnswer;
    }

    private static class QuizResult {
        String difficulty;
        int score;
        int totalQuestions;
        String timestamp;

        QuizResult(String difficulty, int score, int totalQuestions, String timestamp) {
            this.difficulty = difficulty;
            this.score = score;
            this.totalQuestions = totalQuestions;
            this.timestamp = timestamp;
        }
    }
}
