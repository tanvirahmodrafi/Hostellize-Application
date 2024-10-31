package com.rafi.hostellize.controller;

import com.rafi.hostellize.HostellizeApplication;
import com.rafi.hostellize.model.Transactions;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class TransactionsController implements Initializable {

    @FXML
    private TableColumn<Transactions, Double> amountCol;

    @FXML
    private TableColumn<Transactions, String> dateCol;

    @FXML
    private TableColumn<Transactions, String> idCol;

    @FXML
    private TableColumn<Transactions, String> noteCol;

    @FXML
    private Label totalMoneyField;

    @FXML
    private TableView<Transactions> transactionTableView;

    @FXML
    private TableColumn<Transactions, String> typeCol;

    ObservableList <Transactions> transactions = FXCollections.observableArrayList();
    Double totalMoney = 0.0;

    @FXML
    void backAction(ActionEvent event) throws IOException {
        HostellizeApplication.changeScene("normaluser");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDate()));
        amountCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getAmount()));
        typeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));
        noteCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNotes()));
        try {
            fetchTransactions();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    void fetchTransactions() throws IOException {
        String dbUrl = "jdbc:mysql://localhost:3306/hostellize";
        String dbUser = "root";
        String dbPass = "tanvir@5232112";

        String query = "SELECT * FROM transactions WHERE id = '" + LoginPageController.id + "' " +
                "OR id IN ('890001', '890002', '890003', '890004', '890005', '890006', '890007', '890008', '890009', '890010', '890011');";


        try {
            Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String date = resultSet.getString("date");
                double amount = resultSet.getDouble("amount");
                String type = resultSet.getString("type");
                String notes = resultSet.getString("notes");

                Transactions transaction = new Transactions(id, date, amount, type, notes);
                transactions.add(transaction);
                System.out.println(amount);
                totalMoney += amount;
            }

            transactionTableView.setItems(transactions);
            totalMoneyField.setText(Double.toString(totalMoney));
            if (totalMoney > 0) {
                totalMoneyField.setStyle("-fx-text-fill: green;");
            } else {
                totalMoneyField.setStyle("-fx-text-fill: red;");
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("TransactionController class cannot fetch data: " + e.getMessage());
        }
    }

}
