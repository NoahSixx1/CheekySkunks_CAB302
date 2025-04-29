package com.example.assignment1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginPage {
    @FXML private TextArea termsAndConditions;
    @FXML private CheckBox agreeCheckBox;
    @FXML private Button nextButton;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private Button toggleButton;
    @FXML private Label modeLabel;

    private String currentUserid = "";

    private boolean isRegisterMode = false;

    @FXML
    public void initialize() {
        termsAndConditions.setText("Lorem ipsum dolor sit amet...");
        toggleMode();
    }

    @FXML
    protected void onAgreeCheckBoxClick() {
        nextButton.setDisable(!agreeCheckBox.isSelected());
    }

    @FXML
    protected void onNextButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (isRegisterMode) {
            String name = nameField.getText();
            String email = emailField.getText();
            if (Database.registerUser(username, password, name, email)) {
                showAlert("Success", "Registration successful.");
                toggleMode(); // Switch to login
            } else {
                showAlert("Error", "Registration failed. Username may exist.");
            }
        } else {
            if (Database.authenticateUser(username, password)) {
                String userId = Database.getUserId(username);  // <-- get the user's id
                Session.setCurrentUserId(userId); // <-- save into session
                showAlert("Success", "Login successful!");     // <-- fix the alert
                goToNextPage();
            } else {
                showAlert("Error", "Invalid username or password.");
            }
        }
    }


    @FXML
    protected void onCancelButtonClick() {
        ((Stage) nextButton.getScene().getWindow()).close();
    }

    @FXML
    protected void onToggleButtonClick() {
        isRegisterMode = !isRegisterMode;
        toggleMode();
    }

    private void toggleMode() {
        nameField.setVisible(isRegisterMode);
        emailField.setVisible(isRegisterMode);
        toggleButton.setText(isRegisterMode ? "Switch to Login" : "Switch to Register");
        nextButton.setText(isRegisterMode ? "Register" : "Login");
        modeLabel.setText(isRegisterMode ? "Register New Account" : "Login");
    }

    private void goToNextPage() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/ProjectsPage.fxml"));
            Scene scene = new Scene(loader.load(), App.WIDTH, App.HEIGHT);
            Stage stage = (Stage) nextButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
