package com.example.assignment1;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class ProjectsPage {
    @FXML
    private ListView<String> projectsListView;
    @FXML
    private Button newButton;

    private void syncContacts() {
        projectsListView.getItems().clear();
        List<String> projects = Database.fillProjectsList();
        boolean hasProject = !projects.isEmpty();
        if (hasProject) {
            projectsListView.getItems().addAll();
        }
    }

    @FXML
    public void initialize() {
        syncContacts();
    }

}
