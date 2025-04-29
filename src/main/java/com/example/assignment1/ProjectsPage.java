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
    private ListView<String> projectsListView;
    @FXML
    private Button newButton;
    @FXML
    private Button exitButton;
    @FXML
    private Button selectButton;

    private void syncContacts() {
        projectsListView.getItems().clear();
        List<String> projects = Database.fillProjectsList(Session.getCurrentUserId());
        boolean hasProject = !projects.isEmpty();
        System.out.println(projects);
        if (hasProject) {
            projectsListView.getItems().addAll(projects);
        }
    }

    private ListCell<String> renderCell(ListView<String> contactListView) {
        return new ListCell<>() {
            /**
             * Handles the event when a contact is selected in the list view.
             *
             * @param mouseEvent The event to handle.
             */
            private void onContactSelected(MouseEvent mouseEvent) {
                ListCell<String> clickedCell = (ListCell<String>) mouseEvent.getSource();
                // Get the selected contact from the list view
                String selectedProject = clickedCell.getItem();
                System.out.println(selectedProject);
                if (selectedProject != null) {
                    Session.setCurrentProjectId(selectedProject);
                }
            }
            /**
             * Updates the item in the cell by setting the text to the contact's full name.
             * @param project The project to update the cell with.
             * @param empty Whether the cell is empty.
             */
            @Override
            protected void updateItem(String project, boolean empty) {
                super.updateItem(project, empty);
                // If the cell is empty, set the text to null, otherwise set it to the contact's full name
                if (empty || project == null) {
                    setText(null);
                    super.setOnMouseClicked(this::onContactSelected);
                } else {
                    setText(project);
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
    public void initialize() {
        projectsListView.setCellFactory(this::renderCell);
        syncContacts();
        // Select the first contact and display its information

        projectsListView.getSelectionModel().selectFirst();
        String firstProject = projectsListView.getSelectionModel().getSelectedItem();
        if (firstProject != null) {
            System.out.println(firstProject);
            Session.setCurrentProjectId(firstProject);
        }

    }

}
