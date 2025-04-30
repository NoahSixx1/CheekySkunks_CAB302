package com.example.assignment1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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
    public void initialize() {
        userColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        rapColumn.setCellValueFactory(new PropertyValueFactory<>("rap"));

        leaderboardTable.setItems(getLeaderboardData());
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
