module com.example.tracker {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.example.tracker;
    opens com.example.tracker to javafx.fxml;
    opens com.example.tracker.ui to javafx.fxml;
}