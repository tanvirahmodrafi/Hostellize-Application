package com.rafi.hostellize.controller;

import com.rafi.hostellize.HostellizeApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DepositController implements Initializable {

    @FXML
    private CheckBox agreeCheckBox;

    @FXML
    private TextField amountTextField;

    @FXML
    private TextArea notesAreaField;

    @FXML
    private ChoiceBox<String> reasonChoiceBox;

    @FXML
    void backButtonAction(ActionEvent event) throws IOException {
        HostellizeApplication.changeScene("normaluser");
    }

    @FXML
    void submitAction(ActionEvent event) throws SQLException {
        String amount = amountTextField.getText();
        String reason = reasonChoiceBox.getValue();
        String notes = notesAreaField.getText();
        if (agreeCheckBox.isSelected()) {
            insertToDB(amount,reason,notes);
            amountTextField.clear();
            notesAreaField.clear();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Agree to deposit");
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reasonChoiceBox.getItems().addAll("Normal Deposit","Damage");
        reasonChoiceBox.getSelectionModel().selectFirst();
    }
    void insertToDB(String amount, String reason, String notes) {
        String id = LoginPageController.id;
        LocalDate d = LocalDate.now();
        String date = d.toString();

        // SQL insert query
        String query = "INSERT INTO transactions (id, date, amount, type, notes) " +
                "VALUES ('" + id + "', '" + date + "', '" + amount + "', '" + reason + "', '" + notes + "');";

        String dbUrl = "jdbc:mysql://localhost:3306/hostellize";
        String dbUser = "root";
        String dbPass = "tanvir@5232112";

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             Statement statement = connection.createStatement()) {

            // Execute the insert operation
            int rowsInserted = statement.executeUpdate(query);
            if (rowsInserted > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Transaction successfully added");
                alert.showAndWait();

            } else {
                System.out.println("Insert failed.");
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

}
