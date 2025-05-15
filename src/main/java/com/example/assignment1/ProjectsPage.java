package com.example.assignment1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ProjectsPage {
    @FXML
    private ListView<Database.ProjectInfo> projectsListView; // Change type here
    @FXML
    private Button newButton;
    @FXML
    private Button exitButton;
    @FXML
    private Button selectButton;
    @FXML
    private Button leaderboardButton;

    private void syncContacts() {
        projectsListView.getItems().clear();
        List<Database.ProjectInfo> projects = Database.fillProjectsList(Session.getCurrentUserId());
        boolean hasProject = !projects.isEmpty();
        if (hasProject) {
            projectsListView.getItems().addAll(projects);
        }
    }

    private ListCell<Database.ProjectInfo> renderCell(ListView<Database.ProjectInfo> contactListView) {
        return new ListCell<>() {
            private void onContactSelected(MouseEvent mouseEvent) {
                ListCell<Database.ProjectInfo> clickedCell = (ListCell<Database.ProjectInfo>) mouseEvent.getSource();
                // Get the selected contact from the list view
                Database.ProjectInfo selectedProject = clickedCell.getItem();
                if (selectedProject != null) {
                    System.out.println("selectedid: " + selectedProject.getId());
                    Session.setCurrentProjectId(selectedProject.getId());
                }
            }

            @Override
            protected void updateItem(Database.ProjectInfo project, boolean empty) {
                super.updateItem(project, empty);
                if (empty || project == null) {
                    setText(null);
                } else {
                    setText(project.getName());
                    super.setOnMouseClicked(this::onContactSelected);
                }
            }
        };
    }

    @FXML
    private void goToNextPage() {
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
    private void goToLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/loginPage.fxml"));
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
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/Leaderboard.fxml"));
            Scene leaderboardScene = new Scene(loader.load(), App.WIDTH, App.HEIGHT);
            Stage stage = (Stage) leaderboardButton.getScene().getWindow();
            stage.setScene(leaderboardScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Unable to load the leaderboard page.");
            alert.show();
        }
    }

    @FXML
    public void initialize() {
        projectsListView.setCellFactory(this::renderCell);
        syncContacts();

        // Select the first project and display its information
        projectsListView.getSelectionModel().selectFirst();
        Database.ProjectInfo firstProject = projectsListView.getSelectionModel().getSelectedItem();
        if (firstProject != null) {
            System.out.println("firstid: " + firstProject.getId());
            Session.setCurrentProjectId(firstProject.getId());
        }
    }
}