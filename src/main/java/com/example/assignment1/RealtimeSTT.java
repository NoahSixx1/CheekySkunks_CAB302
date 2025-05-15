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
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;
public class RealtimeSTT {

    @FXML private Text statusText;
    @FXML private TextArea transcriptArea;
    @FXML private Button recordButton;
    @FXML private Button downloadButton;
    @FXML private Button FeedbackPageButton;
    @FXML private Button exitButton;
    @FXML private Button leaderboardButton;
    @FXML private Button chooseBeatButton;

    private boolean isRecording = false;
    private Recognizer recognizer;
    private TargetDataLine microphone;
    private Thread recordingThread;
    private static final int SAMPLE_RATE = 16000;
    private Model model;
    private String transcript = "";
    private File beatFile;
    private BeatPlayer beatPlayer = new BeatPlayer();

    String userId = Session.getCurrentUserId();

    public RealtimeSTT() throws IOException {
        model = new Model("src/main/resources/model");
    }

    @FXML
    public void initialize() throws IOException {
        recognizer = new Recognizer(model, SAMPLE_RATE);
    }

    @FXML
    private void handleChooseBeat() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Beat (WAV only)");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("WAV Files", "*.wav"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            beatFile = selectedFile;
            statusText.setText("Beat selected: " + beatFile.getName());
        } else {
            statusText.setText("No beat selected.");
        }
    }

    @FXML
    public void handleRecordButton() {
        if (isRecording) {
            stopRecording();
            recordButton.setText("Start Recording");
            statusText.setText("Recording stopped.");
        } else {
            if (beatFile != null) {
                beatPlayer.playBeat(beatFile, 0.8f); // Volume 0.0 to 1.0
            }
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

            transcript = "";
            transcriptArea.clear();

            recordingThread = new Thread(() -> {
                microphone.start();
                byte[] buffer = new byte[4096];
                int bytesRead;
                String currentLine = "";

                while (isRecording && (bytesRead = microphone.read(buffer, 0, buffer.length)) != -1) {
                    if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                        String resultJson = recognizer.getResult();
                        String finalText = extractTextFromJson(resultJson);

                        if (!finalText.isBlank()) {
                            transcript += finalText + "\n";
                            String finalTranscript = transcript + currentLine;
                            Platform.runLater(() -> transcriptArea.setText(finalTranscript));
                        }
                        currentLine = "";
                    } else {
                        String partialJson = recognizer.getPartialResult();
                        String partialText = extractPartialTextFromJson(partialJson);

                        if (!partialText.isBlank()) {
                            currentLine = partialText;
                            String finalTranscript = transcript + currentLine;
                            Platform.runLater(() -> transcriptArea.setText(finalTranscript));
                        }
                    }
                }

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

    private void stopRecording() {
        beatPlayer.stopBeat();

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

        saveTranscriptToDatabase();
    }

    private String extractTextFromJson(String json) {
        String strippedText = json.replace("{\n", "").replace("\n}", "");
        return strippedText.replaceAll("\"text\"\\s*:\\s*\"([^\"]+)\"", "$1");
    }

    private String extractPartialTextFromJson(String json) {
        String strippedText = json.replace("{\n", "").replace("\n}", "");
        return strippedText.replaceAll("\"partial\"\\s*:\\s*\"([^\"]+)\"", "$1");
    }

    private void saveTranscriptToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8))) {
            writer.write(transcript);
            System.out.println("Transcript saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveTranscriptToDatabase() {
        // First, prompt the user for a name
        TextInputDialog dialog = new TextInputDialog("My Rap");
        dialog.setTitle("Name Your Rap");
        dialog.setHeaderText("Give your rap a name");
        dialog.setContentText("Enter a name:");

        // Get the result and process it outside the lambda
        String rapName;
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            rapName = result.get().trim();
        } else {
            rapName = "Untitled Rap"; // Default name if user cancels or enters nothing
        }

        // Now save to database with the name
        String url = "jdbc:sqlite:skunks.db";
        String insertSQL = "INSERT INTO projects (userid, name, transcript) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {

            int userId = Integer.parseInt(Session.getCurrentUserId());

            pstmt.setInt(1, userId);
            pstmt.setString(2, rapName);  // Add the name here
            pstmt.setString(3, transcript);

            pstmt.executeUpdate();

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

    @FXML
    public void onFeedbackButtonClick() {
        goToFeedbackPage();
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

    @FXML
    private void onLeaderboardClick() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/leaderboard.fxml"));
            Scene scene = new Scene(loader.load(), App.WIDTH, App.HEIGHT);
            Stage stage = (Stage) leaderboardButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveToLeaderboard(int userId, String rap, int score) {
        String url = "jdbc:sqlite:skunks.db";
        String sql = "INSERT INTO leaderboard (userid, transcript, score) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, rap);
            pstmt.setInt(3, score);
            pstmt.executeUpdate();
            System.out.println("Rap added to leaderboard with score: " + score);
        } catch (SQLException e) {
            System.out.println("Leaderboard error: " + e.getMessage());
        }
    }

    private int calculateScore(String transcript) {
        String[] words = transcript.trim().split("\\s+");
        return words.length;
    }

    // ✅ Inner BeatPlayer class with looping + volume
    static class BeatPlayer {
        private Clip clip;

        public void playBeat(File beatFile, float volume) {
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(beatFile);
                clip = AudioSystem.getClip();
                clip.open(audioStream);

                // Loop continuously
                clip.loop(Clip.LOOP_CONTINUOUSLY);

                // Volume control (0.0 to 1.0)
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float min = volumeControl.getMinimum();
                float max = volumeControl.getMaximum();
                float gain = min + (max - min) * volume; // map [0.0 - 1.0] to min-max
                volumeControl.setValue(gain);

                clip.start();
            } catch (Exception e) {
                System.out.println("Error playing beat: " + e.getMessage());
            }
        }

        public void stopBeat() {
            if (clip != null && clip.isRunning()) {
                clip.stop();
                clip.close();
            }
        }
    }
}
