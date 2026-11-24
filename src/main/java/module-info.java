module com.example.tracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    // aquí irán también los requires de javafx cuando uses JavaFX, p.ej.:
    // requires javafx.controls;
    // requires javafx.fxml;

    opens com.example.tracker to javafx.fxml;
    exports com.example.tracker;
}