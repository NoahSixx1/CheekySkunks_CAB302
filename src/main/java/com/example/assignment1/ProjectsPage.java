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

import static java.lang.String.*;

public class ProjectsPage {
    @FXML
    private ListView<Project> projectsListView;
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
        List<Project> projects = Database.fillProjectsList(Session.getCurrentUserId());
        boolean hasProject = !projects.isEmpty();
        //System.out.println(projects);
        if (hasProject) {
            projectsListView.getItems().addAll(projects);
        }
    }

    private ListCell<Project> renderCell(ListView<Project> contactListView) {
        return new ListCell<Project>() {
            /**
             * Handles the event when a contact is selected in the list view.
             *
             * @param mouseEvent The event to handle.
             */
            private void onContactSelected(MouseEvent mouseEvent) {
                ListCell<Project> clickedCell = (ListCell<Project>) mouseEvent.getSource();
                // Get the selected contact from the list view
                Project selectedProject = clickedCell.getItem();
                System.out.println("selectedid: " + selectedProject.getProjectid());
                if (selectedProject != null) {
                    Session.setCurrentProjectId(String.valueOf(selectedProject.getProjectid()));
                }
            }
            /**
             * Updates the item in the cell by setting the text to the contact's full name.
             * @param project The project to update the cell with.
             * @param empty Whether the cell is empty.
             */
            @Override
            protected void updateItem(Project project, boolean empty) {
                super.updateItem(project, empty);
                // If the cell is empty, set the text to null, otherwise set it to the contact's full name
                if (empty || project == null) {
                    setText(null);
                    super.setOnMouseClicked(this::onContactSelected);
                } else {
                    setText(project.getProjectname());
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
            // Optionally show an alert if the scene can't be loaded
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Unable to load the leaderboard page.");
            alert.show();
        }
    }

    @FXML
    public void initialize() {
        projectsListView.setCellFactory(this::renderCell);
        syncContacts();
        // Select the first contact and display its information
        projectsListView.getSelectionModel().selectFirst();
        Project firstProject = projectsListView.getSelectionModel().getSelectedItem();
        if (firstProject != null) {
            System.out.println("firstid: " + firstProject);
            Session.setCurrentProjectId(String.valueOf(firstProject.getProjectid()));
        }

    }

}
