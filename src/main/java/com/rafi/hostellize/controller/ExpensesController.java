package com.rafi.hostellize.controller;

import com.rafi.hostellize.HostellizeApplication;
import com.rafi.hostellize.model.Transactions;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;


public class ExpensesController implements Initializable {

    @FXML
    private TableView<Transactions> transactionsTableView;

    @FXML
    private TableColumn<Transactions, String> typeCol;

    @FXML
    private TableColumn<Transactions, Double> amountCol;

    @FXML
    private TableColumn<Transactions, String> dateCol;

    @FXML
    private TableColumn<Transactions, String> idCol;

    @FXML
    private TableColumn<Transactions, String> noteCol;

    @FXML
    private TextField individualAmountField;

    @FXML
    private TextField individualIdField;

    @FXML
    private TextArea individualNotesField;

    @FXML
    private ChoiceBox<String> individualTypeChoiceBox;

    @FXML
    private TextField monthlyAmountDeductedField;

    @FXML
    private CheckBox montlyexpencesCheckBox;

    @FXML
    private ChoiceBox<String> purposeChoiceBox;

    @FXML
    private Tab searchHostellerTab;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab viewMealListTab;

    ObservableList<Transactions> transactions = FXCollections.observableArrayList();

    @FXML
    void backAction(ActionEvent event) throws IOException {
        HostellizeApplication.changeScene("manager");
    }

    @FXML
    void deductMoneyAction(ActionEvent event) {
        tabPane.getSelectionModel().select(searchHostellerTab);
    }

    @FXML
    void individualDoneAction(ActionEvent event) {
        String type = individualTypeChoiceBox.getValue();
        Double amount = Double.valueOf(individualAmountField.getText());
        String id = individualIdField.getText();
        String note = individualNotesField.getText();

        try{
            individualExpenses(id,(amount*-1),type,note+"(From Manager)");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void monthlyDoneAction(ActionEvent event) throws IOException, SQLException {
        if (montlyexpencesCheckBox.isSelected()) {
            Double amount = Double.parseDouble(monthlyAmountDeductedField.getText());
            String purpose = purposeChoiceBox.getValue();

            monthlyExpenses((amount)*-1, purpose);
        }
    }

    @FXML
    void viewTransactionsAction(ActionEvent event) {
        tabPane.getSelectionModel().select(viewMealListTab);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            fetchTransactions();
        } catch (IOException e) {
            System.out.println("Fetch transactions failed in ExpensesController");
        }

        individualTypeChoiceBox.getItems().addAll("Monthly Fare", "Damage", "Meal", "Others");
        individualTypeChoiceBox.getSelectionModel().selectFirst();

        purposeChoiceBox.getItems().addAll("Monthly Fare", "Meal Rate", "Others");
        purposeChoiceBox.getSelectionModel().selectFirst();
    }

    void fetchTransactions() throws IOException {
        String dbUrl = "jdbc:mysql://localhost:3306/hostellize";
        String dbUser = "root";
        String dbPass = "tanvir@5232112";

        String query = "SELECT * FROM transactions;";
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
            }
            transactionsTableView.setItems(transactions);

            resultSet.close();
            statement.close();
            connection.close();

            idCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
            dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDate()));
            amountCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getAmount()));
            typeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));
            noteCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNotes()));

        } catch (SQLException e) {
            System.out.println("TransactionController class cannot fetch data: " + e.getMessage());
        }

    }

    void monthlyExpenses(Double amount, String purpose) throws IOException, SQLException {
        String dbUrl = "jdbc:mysql://localhost:3306/hostellize";
        String dbUser = "root";
        String dbPass = "tanvir@5232112";
        LocalDate d = LocalDate.now();
        String date = d.toString();

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {

            if (!purpose.equals("Meal Rate")) {
                // Insert transaction for the current user
                String query = "INSERT INTO transactions (id, date, amount, type, notes) " +
                        "VALUES ('" + LoginPageController.id + "', '" + date + "', '" + amount + "', '" + purpose + "', 'From Manager')";

                try (Statement statement = connection.createStatement()) {
                    int rowsInserted = statement.executeUpdate(query);
                    showAlert(rowsInserted > 0, "Transaction successfully added", "Transaction could not be added");
                }

            } else {
                // Process each user and calculate meal expenses
                String query = "SELECT DISTINCT id FROM transactions";  // Fetch unique user IDs

                try (Statement statement = connection.createStatement();
                     ResultSet resultSet = statement.executeQuery(query)) {

                    while (resultSet.next()) {
                        String id = resultSet.getString("id");
                        if (id.startsWith("890") || id.startsWith("123")) {
                            continue; // Skip admin/special users
                        }

                        // Calculate total meals for each user
                        String totalMealQuery = "SELECT SUM(numberOfMeal) AS total_meal FROM everymeal WHERE id = '" + id + "'";

                        try (Statement mealStatement = connection.createStatement();
                             ResultSet mealResultSet = mealStatement.executeQuery(totalMealQuery)) {

                            if (mealResultSet.next()) {
                                double totalMealFromDB = mealResultSet.getDouble("total_meal");
                                double totalAmount = totalMealFromDB * amount;

                                // Insert transaction for meal cost
                                String insertMeal = "INSERT INTO transactions (id, date, amount, type, notes) " +
                                        "VALUES ('" + id + "', '" + date + "', '" + totalAmount + "', '" + purpose + "', 'Total Meal Cost (From Manager)')";

                                try (Statement insertStatement = connection.createStatement()) {
                                    int mealRowsInserted = insertStatement.executeUpdate(insertMeal);
                                    showAlert(mealRowsInserted > 0, "Transaction successfully added", "Transaction could not be added");
                                }
                            }
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("In monthlyExpenses() main connection is not working in ExpensesController");
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // Utility method to show alerts based on success/failure
    private void showAlert(boolean isSuccess, String successMessage, String failureMessage) {
        Alert alert = new Alert(isSuccess ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(isSuccess ? "Success" : "Error");
        alert.setHeaderText(null);
        alert.setContentText(isSuccess ? successMessage : failureMessage);
        alert.showAndWait();
    }



    void individualExpenses(String id,Double amount,String type,String notes) throws IOException {
        String dbUrl = "jdbc:mysql://localhost:3306/hostellize";
        String dbUser = "root";
        String dbPass = "tanvir@5232112";
        LocalDate d = LocalDate.now();
        String date = d.toString();
        String query = "INSERT INTO transactions (id, date, amount, type, notes) " +
                "VALUES ('" + id + "', '" + date + "', '" + amount + "', '" + type + "', '" + notes + "');";

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             Statement statement = connection.createStatement()) {

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