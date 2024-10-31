package com.rafi.hostellize.controller;

import com.rafi.hostellize.HostellizeApplication;
import com.rafi.hostellize.model.Meals;
import com.rafi.hostellize.model.Transactions;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class UserMealDetailsController implements Initializable {

    @FXML
    private TableColumn<Meals, String> dateCol;

    @FXML
    private TableColumn<Meals, String> idCol;

    @FXML
    private TableColumn<Meals, Double> mealNumberCol;

    @FXML
    private TableView<Meals> mealTableView;

    ObservableList <Meals> meal = FXCollections.observableArrayList();

    @FXML
    void backAction(ActionEvent event) throws IOException {
        String ids = LoginPageController.id;
        if(ids.substring(0,3).equals("890")){
            HostellizeApplication.changeScene("manager");
        }else{
            HostellizeApplication.changeScene("normaluser");
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getID()));
        dateCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDate()));
        mealNumberCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getAmount()));

        // Load data from database into the meal ObservableList
        loadMealData();
        mealTableView.setItems(meal);
    }

    private void loadMealData() {
        String dbUrl = "jdbc:mysql://localhost:3306/hostellize";
        String dbUser = "root";
        String dbPass = "tanvir@5232112";
        String ids = LoginPageController.id;
        String query;
        if(ids.substring(0,3).equals("890")){
            query = "SELECT id, date, numberOfMeal FROM everymeal";
        }else{
            query = "SELECT id, date, numberOfMeal FROM everymeal WHERE id = '" + ids + "'";
        }



        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String date = resultSet.getString("date");
                double mealNumber = resultSet.getDouble("numberOfMeal");

                Meals mealRecord = new Meals(id, date, mealNumber);
                meal.add(mealRecord);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error loading meal data from database.");
        }
    }
}
