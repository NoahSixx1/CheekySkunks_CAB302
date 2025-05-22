package com.example.assignment1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
