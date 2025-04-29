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
    private Button nextButton;

    private void syncContacts() {
        projectsListView.getItems().clear();
        List<String> projects = Database.fillProjectsList(5);
        boolean hasProject = !projects.isEmpty();
        System.out.println(projects);
        if (hasProject) {
            projectsListView.getItems().addAll(projects);
        }
    }

    @FXML
    private void goToNextPage() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/recordPage.fxml"));
            Scene scene = new Scene(loader.load(), App.WIDTH, App.HEIGHT);
            Stage stage = (Stage) nextButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        syncContacts();
    }

}
