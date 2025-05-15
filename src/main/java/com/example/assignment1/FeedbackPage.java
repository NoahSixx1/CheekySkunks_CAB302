package com.example.assignment1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class FeedbackPage {

    @FXML
    private TextArea feedbackTextArea;

    @FXML
    private Button getFeedbackButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button editTextButton;

    @FXML
    public void initialize() {
        feedbackTextArea.setEditable(false);
        editTextButton.setText("Edit Text");

        loadTranscriptFromDatabase();
    }

    private void loadTranscriptFromDatabase() {
        String url = "jdbc:sqlite:skunks.db";
        String query = "SELECT transcript FROM projects WHERE projectid = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            int projectId = Integer.parseInt(Session.getCurrentProjectId()); // get projectId from session
            pstmt.setInt(1, projectId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String transcript = rs.getString("transcript");
                feedbackTextArea.setText(transcript);
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    @FXML
    public void handleGetFeedbackButton() {
        System.out.println("Button is pressed");
        OllamaAPITest test = new OllamaAPITest();
        String prompt = "analyse these rap lyrics and give me a score out of 100:" + feedbackTextArea.getText();
        OllamaSyncResponse generateResponse = new OllamaSyncResponse(prompt);
        try {
            String result = generateResponse.ollamaResponse();

            System.out.println("Our feedback: " + result);
            String feedback = "Our feedback: " + result;
            feedbackTextArea.appendText("\n\n" + feedback);

            // Extract score from AI feedback
            int score = extractScoreFromFeedback(result);
            if (score > 0) {
                // Save score to leaderboard
                saveToLeaderboard(score);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private int extractScoreFromFeedback(String feedback) {
        // Pattern for finding score out of 100
        String[] patterns = {
                "\\b(\\d{1,3})\\s*(?:/|out of)\\s*100\\b",  // Matches "85 out of 100" or "85/100"
                "\\bscore\\s*(?::|is|of)\\s*(\\d{1,3})\\b", // Matches "score: 85" or "score of 85"
                "\\brating\\s*(?::|is|of)\\s*(\\d{1,3})\\b", // Matches "rating: 85"
                "\\b(\\d{1,3})\\s*points\\b",               // Matches "85 points"
                "\\bgrade\\s*(?::|is|of)\\s*(\\d{1,3})\\b", // Matches "grade: 85"
                "\\b(\\d{1,2})\\s*\\/\\s*10\\b"             // Matches "8/10" (multiply by 10)
        };

        for (String pattern : patterns) {
            java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.CASE_INSENSITIVE);
            java.util.regex.Matcher m = r.matcher(feedback);
            if (m.find()) {
                int score = Integer.parseInt(m.group(1));

                // If score is out of 10, convert to out of 100
                if (pattern.contains("\\/\\s*10")) {
                    score *= 10;
                }

                // Validate score is between 0 and 100
                return Math.min(Math.max(score, 0), 100);
            }
        }

        // Default score if no pattern matches
        return 0;
    }
    private void saveToLeaderboard(int score) {
        String url = "jdbc:sqlite:skunks.db";
        String sql = "INSERT OR REPLACE INTO leaderboard (userid, transcript, score) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int userId = Integer.parseInt(Session.getCurrentUserId());
            String transcript = feedbackTextArea.getText();

            pstmt.setInt(1, userId);
            pstmt.setString(2, transcript);
            pstmt.setInt(3, score);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Score saved to leaderboard: " + score);
                // Show success notification
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Score Saved");
                alert.setHeaderText(null);
                alert.setContentText("Your score of " + score + " has been added to the leaderboard!");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleEditTextButton() {
        if (feedbackTextArea.isEditable()) {
            feedbackTextArea.setEditable(false);
            editTextButton.setText("Edit Text");
        } else {
            feedbackTextArea.setEditable(true);
            editTextButton.setText("Finish");
        }
    }
    @FXML
    private void onExit() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/recordPage.fxml"));
            Scene scene = new Scene(loader.load(), App.WIDTH, App.HEIGHT);
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
