package com.example.assignment1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        String prompt = "analyse these rap lyrics and give me a score:" + feedbackTextArea.getText();
        OllamaSyncResponse generateResponse = new OllamaSyncResponse(prompt);
        try {
            String result = generateResponse.ollamaResponse();

            System.out.println("Our feedback: " + result);
            String feedback = "Our feedback: " + result;
            feedbackTextArea.appendText("\n\n" + feedback);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // Dummy feedback: count words
        //String currentText = feedbackTextArea.getText();
        //String feedback = "Feedback: Your text has " + currentText.split("\\s+").length + " words.";
        //feedbackTextArea.appendText("\n\n" + feedback);
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
