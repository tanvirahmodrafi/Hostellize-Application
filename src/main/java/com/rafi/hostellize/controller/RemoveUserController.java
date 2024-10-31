package com.rafi.hostellize.controller;

import com.rafi.hostellize.HostellizeApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.*;

public class RemoveUserController {

    @FXML
    private CheckBox checkBox;

    @FXML
    private Label successLabel;

    @FXML
    private TextField userIdField;

    @FXML
    void removeAction(ActionEvent event) {
        boolean success = checkBox.isSelected();
        String userId = userIdField.getText();
        if(LoginPageController.id.substring(0,3).equals("890") && userId.substring(0,3).equals("123")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Manager Cannot Remove Admin");
            alert.showAndWait();
        }else{
            if(success && !userId.isEmpty()){
                removeFromDB(userId);
                userIdField.setText("");
                checkBox.setSelected(false);
            }else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Please ensure the checkbox is selected and User ID is entered.");
                alert.showAndWait();
            }
        }

    }
    @FXML
    void cancelAction(ActionEvent event) throws IOException {
        if(LoginPageController.id.substring(0,3).equals("123")){
            HostellizeApplication.changeScene("admin");
        }else{
            HostellizeApplication.changeScene("manager");
        }
    }

    private void removeFromDB(String userid) {
        String url = "jdbc:mysql://localhost/hostellize"; //database URL
        String dbUsername = "root";  // database username
        String dbPassword = "tanvir@5232112";  // database password

        String query = "DELETE FROM userinfo WHERE id = '" + userid + "';";
        String queryToDeleteIdPass = "DELETE FROM id_password WHERE id = '" + userid + "';";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            Statement statement = connection.createStatement();
            Statement statement2 = connection.createStatement();
            int rowsAffected = statement.executeUpdate(query);
            statement2.executeUpdate(queryToDeleteIdPass);

            if (rowsAffected > 0) {
                successLabel.setText("Successfully Removed!");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("No User Found");
                alert.showAndWait();
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
