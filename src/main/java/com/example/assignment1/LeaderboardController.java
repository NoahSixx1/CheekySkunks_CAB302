package com.example.assignment1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Control;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class LeaderboardController {

    @FXML
    private TableView<LeaderboardEntry> leaderboardTable;

    @FXML
    private TableColumn<LeaderboardEntry, String> userColumn;

    @FXML
    private TableColumn<LeaderboardEntry, Integer> scoreColumn;

    @FXML
    private TableColumn<LeaderboardEntry, String> rapColumn;

    @FXML
    private Button leaderboardExitButton;


    @FXML
    public void initialize() {
        userColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        rapColumn.setCellValueFactory(new PropertyValueFactory<>("rap"));

        // Add cell factories for wrapping text in rap column
        rapColumn.setCellFactory(tc -> {
            TableCell<LeaderboardEntry, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(rapColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });

        leaderboardTable.setItems(getLeaderboardData());
    }
    @FXML
    private void goToMainPage() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/ProjectsPage.fxml"));
            Scene leaderboardScene = new Scene(loader.load(), App.WIDTH, App.HEIGHT);
            Stage stage = (Stage) leaderboardExitButton.getScene().getWindow();
            stage.setScene(leaderboardScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally show an alert if the scene can't be loaded
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Unable to load the leaderboard page.");
            alert.show();
        }
    }

    private ObservableList<LeaderboardEntry> getLeaderboardData() {
        ObservableList<LeaderboardEntry> data = FXCollections.observableArrayList();

        String url = "jdbc:sqlite:skunks.db";
        String sql = """
            SELECT u.username, l.transcript, l.score
            FROM leaderboard l
            JOIN users u ON l.userid = u.id
            ORDER BY l.score DESC
            LIMIT 10
        """;

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String user = rs.getString("username");
                String rap = rs.getString("transcript");
                int score = rs.getInt("score");

                data.add(new LeaderboardEntry(user, rap, score));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

}
