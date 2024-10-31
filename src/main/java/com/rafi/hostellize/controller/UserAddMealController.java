package com.rafi.hostellize.controller;

import com.rafi.hostellize.HostellizeApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.time.LocalDate;

public class UserAddMealController implements Initializable {

    @FXML
    private Label Dinner;

    @FXML
    private ChoiceBox<String> breakfastChoicebox;

    @FXML
    private ChoiceBox<String> dinnerChoiceBox;

    @FXML
    private ChoiceBox<String> lunchChoiceBox;

    @FXML
    private DatePicker mealDate;

    @FXML
    void backButtonAction(ActionEvent event) throws IOException {
        HostellizeApplication.changeScene("normaluser");
    }

    @FXML
    void submitAction(ActionEvent event) {
        try {
            // Get selected meal values and ensure they are not null
            String breakfastStr = breakfastChoicebox.getValue();
            String lunchStr = lunchChoiceBox.getValue();
            String dinnerStr = dinnerChoiceBox.getValue();

            if (breakfastStr == null || lunchStr == null || dinnerStr == null) {
                showAlert("Please ensure all meal choices are selected.");
                return;
            }

            // Parse meal values
            double breakfast = Double.parseDouble(breakfastStr);
            double lunch = Double.parseDouble(lunchStr);
            double dinner = Double.parseDouble(dinnerStr);
            double totalMeals = breakfast + lunch + dinner;

            // Check date selection
            if (mealDate.getValue() == null) {
                showAlert("Please select a valid date.");
                return;
            }

            LocalDate selectedDate = mealDate.getValue();
            LocalDate today = LocalDate.now();

            // Check if the selected date is in the past
            if (selectedDate.isBefore(today)) {
                showAlert("Cannot update meals for past dates.");
                return;
            }

            String dateString = selectedDate.toString();
            String id = LoginPageController.id;

            // Connect to the database
            String dbUrl = "jdbc:mysql://localhost:3306/hostellize";
            String dbUser = "root";
            String dbPass = "tanvir@5232112";

            try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
                // Check if a record for the selected date exists in `mealdetails`
                String checkQuery = "SELECT numberOfMeal FROM mealdetails WHERE date = ?";
                try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                    checkStatement.setString(1, dateString);
                    ResultSet resultSet = checkStatement.executeQuery();

                    if (!resultSet.next()) {
                        showAlert("No record found for the selected date.");
                        return;
                    }

                    // Update meal count in `mealdetails`
                    double currentMealCount = resultSet.getDouble("numberOfMeal");
                    double updatedMealCount = currentMealCount + totalMeals;

                    String updateQuery = "UPDATE mealdetails SET numberOfMeal = ? WHERE date = ?";
                    try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                        updateStatement.setDouble(1, updatedMealCount);
                        updateStatement.setString(2, dateString);
                        updateStatement.executeUpdate();
                        showAlert("Meal count updated successfully.");
                    }

                    // Insert into `everymeal` table, or update if it exists
                    String insertEveryMealQuery = "INSERT INTO everymeal (id, date, numberOfMeal) VALUES (?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE numberOfMeal = ?";
                    try (PreparedStatement insertStatement = connection.prepareStatement(insertEveryMealQuery)) {
                        insertStatement.setString(1, id);
                        insertStatement.setString(2, dateString);
                        insertStatement.setDouble(3, totalMeals);
                        insertStatement.setDouble(4, totalMeals);
                        insertStatement.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                showAlert("Database error: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            showAlert("Please ensure all meal choices are selected and valid.");
        }
    }


    // Helper method to display alerts
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        breakfastChoicebox.getItems().addAll("0.5","1","2","3");
        breakfastChoicebox.getSelectionModel().selectFirst();
        dinnerChoiceBox.getItems().addAll("1","2","3");
        dinnerChoiceBox.getSelectionModel().selectFirst();
        lunchChoiceBox.getItems().addAll("1","2","3");
        lunchChoiceBox.getSelectionModel().selectFirst();
    }
}
