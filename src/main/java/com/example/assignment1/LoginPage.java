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
        termsAndConditions.setText(
                "1. Use of the App\n" +
                        "The App is intended for users aged 13 and above. By using the App, you confirm that you meet this age requirement. The App provides users with tools to learn, practise, and create rap music, including features powered by artificial intelligence (AI).\n\n" +
                        "2. User Responsibilities\n" +
                        "You agree to:\n" +
                        "- Use the App for personal, non-commercial purposes unless explicitly authorised.\n" +
                        "- Not use the App for any unlawful, harmful, or abusive activity.\n" +
                        "- Respect other users and avoid offensive, hateful, or discriminatory language.\n" +
                        "- Ensure that any content you create does not infringe on intellectual property rights of others.\n\n" +
                        "3. AI-Generated Content Disclaimer\n" +
                        "The App includes features powered by artificial intelligence to assist with lyric generation, flow analysis, feedback, and other creative suggestions. While we strive for high-quality assistance, AI-generated content may be inaccurate, incomplete, outdated, or unsuitable for certain contexts. You are solely responsible for verifying and using this content appropriately.\n\n" +
                        "4. Intellectual Property\n" +
                        "All content provided by us in the App (excluding user-generated content), including graphics, text, logos, and sound samples, is the property of SkunkFlow and protected by copyright and other laws. You may not reproduce, distribute, or modify any of our content without permission.\n\n" +
                        "5. User-Generated Content\n" +
                        "By creating or uploading content to the App, including lyrics, recordings, or performances (\"User Content\"), you retain full ownership of your creations. We do not claim ownership of your raps and we will not use, share, distribute, or commercialise your content without your explicit permission.\n" +
                        "You are solely responsible for your User Content and agree that it does not infringe on the rights of others or violate any laws. While we may store your content locally or temporarily for the purpose of providing the App’s features (e.g. playback, editing, or practice tools), we do not access or use your raps for any other purpose.\n\n" +
                        "6. Limitation of Liability\n" +
                        "To the maximum extent permitted by law, SkunkFlow shall not be liable for any damages arising from your use or inability to use the App, including but not limited to loss of data, reputation, or revenue, even if we have been advised of the possibility of such damages.\n\n" +
                        "7. Changes to the App or Terms\n" +
                        "We reserve the right to modify or discontinue the App at any time without notice. We may also update these Terms from time to time. Your continued use of the App after any changes constitutes your acceptance of the revised Terms.\n\n" +
                        "8. Termination\n" +
                        "We may suspend or terminate your access to the App if you violate these Terms or engage in harmful behaviour, with or without notice.");

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
                String userId = String.valueOf(Database.getUserId(username));// <-- get the user's id
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
