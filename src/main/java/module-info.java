module com.rafi.hostellize {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.rafi.hostellize to javafx.fxml;
    exports com.rafi.hostellize;
    exports com.rafi.hostellize.controller;
    opens com.rafi.hostellize.controller to javafx.fxml;
    exports com.rafi.hostellize.model;
    opens com.rafi.hostellize.model to javafx.fxml;
}