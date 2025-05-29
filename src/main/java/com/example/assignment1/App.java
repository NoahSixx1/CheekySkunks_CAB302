package com.example.assignment1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * The main class of the program, used to initiate the app.
 */
public class App extends Application {
    // Constants defining the window title and size
    public static final String TITLE = "Skunk's Rap";
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 750;

    /**
     * Begins the fxmlLoader for the main app
     * @param stage fxml stage of the app
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/loginPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    /**
     * Launches the application
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }
}