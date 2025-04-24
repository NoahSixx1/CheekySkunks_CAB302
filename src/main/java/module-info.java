module org.skunks.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires vosk;
    requires java.desktop;
    requires java.sql;

    exports com.example.assignment1;  // Ensure this exports the App class package
    opens com.example.assignment1 to javafx.fxml;  // Open the package for FXML to access the controller
}
