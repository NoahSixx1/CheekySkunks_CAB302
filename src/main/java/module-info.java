module org.skunks.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires vosk;
    requires java.sql;
    requires javafx.graphics;
    requires java.desktop;
    requires ollama4j;
    requires org.slf4j;
    requires java.net.http;
    requires org.json;


    exports com.example.assignment1;  // Ensure this exports the App class package
    opens com.example.assignment1 to javafx.fxml;  // Open the package for FXML to access the controller
}

