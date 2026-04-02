module com.example.passmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.h2database;
    requires java.desktop;


    opens com.example.passmanager to javafx.fxml;
    exports com.example.passmanager;
}