package com.example.assignment1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.vosk.Recognizer;
import org.vosk.Model;
import javax.sound.sampled.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class RealtimeSTT {

    @FXML
    private Text statusText;

    @FXML
    private TextArea transcriptArea;

    @FXML
    private Button recordButton;

    @FXML
    private Button downloadButton;

    @FXML
    private Button FeedbackPageButton;

    @FXML
    private Button exitButton;

    private boolean isRecording = false;
    private Recognizer recognizer;
    private TargetDataLine microphone;
    private Thread recordingThread;

    private static final int SAMPLE_RATE = 16000;
    private Model model;
    private String transcript = "";
    String userId = Session.getCurrentUserId();


    public RealtimeSTT() throws IOException {
        model = new Model("src/main/resources/model");
    }

    @FXML
    public void initialize() throws IOException {
        recognizer = new Recognizer(model, SAMPLE_RATE);
    }

    @FXML
    public void handleRecordButton() {
        if (isRecording) {
            stopRecording();
            recordButton.setText("Start Recording");
            statusText.setText("Recording stopped.");
        } else {
            startRecording();
            recordButton.setText("Stop Recording");
            statusText.setText("Recording...");
        }
    }

    private void startRecording() {
        try {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);

            transcript = "";  // Clear previous transcript
            transcriptArea.clear();  // Clear the UI text area

            recordingThread = new Thread(() -> {
                microphone.start();
                byte[] buffer = new byte[4096];
                int bytesRead;

                String currentLine = ""; // New: track current partial text

                while (isRecording && (bytesRead = microphone.read(buffer, 0, buffer.length)) != -1) {
                    if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                        // FINAL result received
                        String resultJson = recognizer.getResult();
                        String finalText = extractTextFromJson(resultJson);

                        if (!finalText.isBlank()) {
                            transcript += finalText + "\n"; // Add final recognized text + newline
                            String finalTranscript = transcript + currentLine; // Display all together

                            Platform.runLater(() -> transcriptArea.setText(finalTranscript));
                        }

                        currentLine = ""; // Reset partial line after final
                    } else {
                        // Partial result received
                        String partialJson = recognizer.getPartialResult();
                        String partialText = extractPartialTextFromJson(partialJson);

                        if (!partialText.isBlank()) {
                            currentLine = partialText;
                            String finalTranscript = transcript + currentLine; // Combine final + partials
                            Platform.runLater(() -> transcriptArea.setText(finalTranscript));
                        }
                    }
                }

                // After recording stopped, add final result
                String finalResultJson = recognizer.getFinalResult();
                String finalText = extractTextFromJson(finalResultJson);

                if (!finalText.isBlank()) {
                    transcript += finalText + "\n";
                }

                Platform.runLater(() -> transcriptArea.setText(transcript.trim()));

                saveTranscriptToFile("transcript.txt");
            });

            isRecording = true;
            recordingThread.start();

        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private String extractTextFromJson(String json) {

        String strippedText = json.replace("{\n", "").replace("\n}", "");
        String output = strippedText.replaceAll("\"text\"\\s*:\\s*\"([^\"]+)\"", "$1");
        return output;
    }

    private String extractPartialTextFromJson(String json) {
        String strippedText = json.replace("{\n", "").replace("\n}", "");
        String output = strippedText.replaceAll("\"partial\"\\s*:\\s*\"([^\"]+)\"", "$1");
        return output;
    }




    private void saveTranscriptToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8))) {
            writer.write(transcript);  // Save the full transcript
            System.out.println("Transcript saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void stopRecording() {
        if (microphone != null && microphone.isOpen()) {
            microphone.stop();
            microphone.close();
        }
        isRecording = false;
        try {
            recordingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Save to database after stopping recording
        saveTranscriptToDatabase();
    }
    private void saveTranscriptToDatabase() {
        String url = "jdbc:sqlite:skunks.db";
        String insertSQL = "INSERT INTO projects (userid, transcript) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {

            int userId = Integer.parseInt(Session.getCurrentUserId());

            pstmt.setInt(1, userId);
            pstmt.setString(2, transcript);

            pstmt.executeUpdate();

            // Get the auto-generated project id
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int newProjectId = rs.getInt(1);
                Session.setCurrentProjectId(String.valueOf(newProjectId));
                System.out.println("Transcript saved to database. Project ID: " + newProjectId);
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }







    @FXML
    private void handleDownloadButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Transcript");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setInitialFileName("transcript.txt");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            saveTranscriptToFile(file.getAbsolutePath());
            statusText.setText("Transcript saved to: " + file.getAbsolutePath());
        }
    }
    private void goToFeedbackPage() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/feedbackPage.fxml"));
            Scene scene = new Scene(loader.load(), App.WIDTH, App.HEIGHT);
            Stage stage = (Stage) FeedbackPageButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void onFeedbackButtonClick() {
        goToFeedbackPage();

    }

    @FXML
    private void onExit() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/ProjectsPage.fxml"));
            Scene scene = new Scene(loader.load(), App.WIDTH, App.HEIGHT);
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
