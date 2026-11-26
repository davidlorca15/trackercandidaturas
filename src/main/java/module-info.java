module com.example.tracker {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires mysql.connector.j;

    opens com.example.tracker.ui to javafx.fxml;
    opens com.example.tracker.model to javafx.base;

    exports com.example.tracker;
    exports com.example.tracker.ui;
    exports com.example.tracker.model;
    exports com.example.tracker.business;
    exports com.example.tracker.data;
}