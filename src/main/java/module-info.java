module org.skunks.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires vosk;
    requires java.desktop;
    requires java.sql;

    exports org.skunks.demo;  // Ensure this exports the App class package
    opens org.skunks.demo to javafx.fxml;  // Open the package for FXML to access the controller
}
