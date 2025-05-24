package com.example.assignment1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ProjectsPage {
    @FXML
    private ListView<ProjectEntry> projectsListView;

    @FXML
    private Button newButton;
    @FXML
    private Button exitButton;
    @FXML
    private Button selectButton;

    @FXML
    private Button leaderboardButton;  // Add the leaderboard button reference

    private void syncContacts() {
        projectsListView.getItems().clear();
        List<ProjectEntry> projects = Database.fillProjectsList(Session.getCurrentUserId());
        if (!projects.isEmpty()) {
            projectsListView.getItems().addAll(projects);
        }
    }




    @FXML
    private void goToLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/loginPage.fxml"));
            Scene scene = new Scene(loader.load(), App.WIDTH, App.HEIGHT);
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onLeaderboardClick() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/Leaderboard.fxml"));
            Scene leaderboardScene = new Scene(loader.load(), App.WIDTH, App.HEIGHT);
            Stage stage = (Stage) leaderboardButton.getScene().getWindow();
            stage.setScene(leaderboardScene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally show an alert if the scene can't be loaded
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Unable to load the leaderboard page.");
            alert.show();
        }
    }

    @FXML
    public void initialize() {
        syncContacts();
        // Select the first contact and display its information
        projectsListView.getSelectionModel().selectFirst();
        String firstProject = String.valueOf(projectsListView.getSelectionModel().getSelectedItem());
        if (firstProject != null) {
            System.out.println("firstid: " + firstProject);
            Session.setCurrentProjectId(firstProject);
        }

    }


    @FXML
    private void handleSelectProjectClick() {
        ProjectEntry selected = projectsListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Project Selected");
            alert.setContentText("Please select a project from the list.");
            alert.show();
            return;
        }

        System.out.println("Selected Project: " + selected.getTitle() + " (ID: " + selected.getProjectId() + ")");
        Session.setCurrentProjectId(String.valueOf(selected.getProjectId()));

        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/recordPage.fxml"));
            Scene scene = new Scene(loader.load(), App.WIDTH, App.HEIGHT);
            Stage stage = (Stage) selectButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNewProjectClick() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Project");
        dialog.setHeaderText("Enter a title for your new project:");
        dialog.setContentText("Title:");

        dialog.showAndWait().ifPresent(title -> {
            if (title.trim().isEmpty()) {
                title = "Untitled Project";
            }

            try {
                int userId = Integer.parseInt(Session.getCurrentUserId());
                String dbUrl = "jdbc:sqlite:skunks.db";
                String insertSQL = "INSERT INTO projects (userid, transcript, title, score) VALUES (?, '', ?, 0)";

                int newProjectId = -1;
                try (Connection conn = DriverManager.getConnection(dbUrl);
                     PreparedStatement pstmt = conn.prepareStatement(insertSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {

                    pstmt.setInt(1, userId);
                    pstmt.setString(2, title);
                    pstmt.executeUpdate();

                    try (var rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            newProjectId = rs.getInt(1);
                            System.out.println("New project created with ID: " + newProjectId);
                            Session.setCurrentProjectId(String.valueOf(newProjectId));
                        }
                    }
                }

                // Navigate to recording
                FXMLLoader loader = new FXMLLoader(App.class.getResource("/recordPage.fxml"));
                Scene scene = new Scene(loader.load(), App.WIDTH, App.HEIGHT);
                Stage stage = (Stage) newButton.getScene().getWindow();
                stage.setScene(scene);
                stage.setMaximized(true);

            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        });
    }




}
