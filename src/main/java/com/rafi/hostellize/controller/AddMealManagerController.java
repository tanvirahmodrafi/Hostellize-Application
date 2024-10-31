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

public class AddMealManagerController {

    @FXML
    private CheckBox checkBox;

    @FXML
    private Label successLabel;

    @FXML
    private TextField userIdField;

    @FXML
    void cancelAction(ActionEvent event) throws IOException {
        HostellizeApplication.changeScene("manager");
    }

    @FXML
    void addAction(ActionEvent event) {
        String id = userIdField.getText();
        boolean check = checkBox.isSelected();
        if (check) {
            if(id.substring(0,3).equals("123")){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Admin Can't be a meal manager");
                alert.showAndWait();
            }else{
                addMealManagerRoleInDB(id);
                userIdField.clear();
                checkBox.setSelected(false);
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select the check Box");
            alert.showAndWait();
        }


    }
    private void addMealManagerRoleInDB(String userid) {
        String url = "jdbc:mysql://localhost/hostellize"; // database URL
        String dbUsername = "root";  // database username
        String dbPassword = "tanvir@5232112";  // database password

        // Query to find the user who currently has the "meal manager" role
        String checkMealManagerQuery = "SELECT id FROM userinfo WHERE role = 'meal manager';";

        // Query to update a user's role to "normal user"
        String updateToNormalUserQuery = "UPDATE userinfo SET role = 'normal user' WHERE id = ";

        // Query to update the given user's role to "meal manager"
        String updateToMealManagerQuery = "UPDATE userinfo SET role = 'meal manager' WHERE id = '" + userid + "';";

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            Statement statement = connection.createStatement();

            // Step 1: Check if there is already a "meal manager"
            ResultSet resultSet = statement.executeQuery(checkMealManagerQuery);

            String currentMealManagerId = null;
            if (resultSet.next()) {
                currentMealManagerId = resultSet.getString("id"); // Get the current meal manager's ID
            }

            // Step 2: If a "meal manager" exists, update that user's role to "normal user"
            if (currentMealManagerId != null) {
                String updateCurrentManagerQuery = updateToNormalUserQuery + "'" + currentMealManagerId + "';";
                statement.executeUpdate(updateCurrentManagerQuery);
            }

            // Step 3: Update the provided user's role to "meal manager"
            int rowsAffected = statement.executeUpdate(updateToMealManagerQuery);

            if (rowsAffected > 0) {
                successLabel.setText("Successfully Updated to Meal Manager!");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("User not found or update failed.");
                alert.showAndWait();
            }

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
