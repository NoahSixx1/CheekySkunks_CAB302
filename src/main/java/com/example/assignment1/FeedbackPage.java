package com.example.assignment1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Page for receiving AI powered feedback
 */
public class FeedbackPage {

    @FXML
    private TextArea feedbackTextArea;

    @FXML
    private Button getFeedbackButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button editTextButton;

    @FXML private Label scoreLabel;

    @FXML private Button saveFeedbackButton;


    /**
     * Initializes necessary values
     */
    @FXML
    public void initialize() {
        feedbackTextArea.setEditable(false);
        editTextButton.setText("Edit Text");

        loadTranscriptFromDatabase();
    }

    /**
     * Collects the necessary transcript.txt from database
     */
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

    /**
     * Prompts AI for feedback on text input
     */
    @FXML
    public void handleGetFeedbackButton() {
        String lyrics = feedbackTextArea.getText();
        String prompt = "Analyse these rap lyrics and respond exactly like this:\n" +
                "score:<number between 0 and 100>\n" +
                "feedback:<your feedback text>\n\nLyrics:\n" +
                lyrics;
        OllamaSyncResponse resp = new OllamaSyncResponse(prompt);

        try {
            String raw = resp.ollamaResponse().trim();
            System.out.println("Raw response:\n" + raw);

            // Split into lines
            String[] lines = raw.split("\\r?\\n");
            for (String line : lines) {
                if (line.toLowerCase().startsWith("score:")) {
                    String num = line.substring(line.indexOf(":") + 1).trim();
                    scoreLabel.setText("Score: " + num);
                }
                else if (line.toLowerCase().startsWith("feedback:")) {
                    String fb = line.substring(line.indexOf(":") + 1).trim();
                    feedbackTextArea.appendText("\n\nFeedback: " + fb);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Logic for handling user text editing
     */
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

    /**
     * Returns user to previous page
     */
    @FXML
    private void onExit() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/recordPage.fxml"));
            Scene scene = new Scene(loader.load(), App.WIDTH, App.HEIGHT);
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves feedback to database
     */
    @FXML
    public void handleSaveFeedback() {
        String fullText = feedbackTextArea.getText();
        String scoreText = scoreLabel.getText().replace("Score:", "").trim();
        int score = 0;

        try {
            score = Integer.parseInt(scoreText);
        } catch (NumberFormatException e) {
            System.out.println("Invalid score format.");
            return;
        }

        int userId = Integer.parseInt(Session.getCurrentUserId());
        int projectId = Integer.parseInt(Session.getCurrentProjectId());

        // Save to file
        String filename = "transcript_project_" + projectId + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(fullText);
            System.out.println("Updated transcript.txt with feedback.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String dbUrl = "jdbc:sqlite:skunks.db";
        String updateProject = "UPDATE projects SET transcript = ?, score = ? WHERE projectid = ?";
        String projectTitle = getProjectTitleById(projectId); // 🔍 Add this helper below
        String updateLeaderboard = """
        INSERT OR REPLACE INTO leaderboard (userid, projectid, score, title) VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = DriverManager.getConnection(dbUrl)) {
            try (PreparedStatement pstmt = conn.prepareStatement(updateProject)) {
                pstmt.setString(1, fullText);
                pstmt.setInt(2, score);
                pstmt.setInt(3, projectId);
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt2 = conn.prepareStatement(updateLeaderboard)) {
                pstmt2.setInt(1, userId);
                pstmt2.setInt(2, projectId);
                pstmt2.setInt(3, score);
                pstmt2.setString(4, projectTitle); // ✅ Now you're actually passing it in
                pstmt2.executeUpdate();
            }

            // 🎉 Success notification
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Feedback and score have been saved successfully!");
            alert.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets title of desired project
     * @param projectId ID of desired project
     * @return project title
     */
    private String getProjectTitleById(int projectId) {
        String title = "Untitled";
        String sql = "SELECT title FROM projects WHERE projectid = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:skunks.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                title = rs.getString("title");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return title;
    }

    /**
     * Downloads transcript as txt file
     */
    @FXML
    private void handleDownloadTranscript() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Transcript and Feedback");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setInitialFileName("feedback_transcript.txt");

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(feedbackTextArea.getText());
                System.out.println("Transcript + feedback saved to: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




}
