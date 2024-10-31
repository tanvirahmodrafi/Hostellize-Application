package com.rafi.hostellize.controller;

import com.rafi.hostellize.HostellizeApplication;
import com.rafi.hostellize.model.MealDetails;
import com.rafi.hostellize.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ShowMealListController implements Initializable {

    @FXML
    private DatePicker Date;

    @FXML
    private ImageView cashMemoImageView;

    @FXML
    private TableColumn<MealDetails, String> dateCol;

    @FXML
    private TableColumn<MealDetails, String> menuCol;

    @FXML
    private TableColumn<MealDetails, String> numberOfMealCol;

    @FXML
    private TableColumn<MealDetails, String> priceCol;

    @FXML
    private Tab searchHostellerTab;

    @FXML
    private TabPane tabPane;

    @FXML
    private TableView<MealDetails> userTableView;

    @FXML
    private Tab viewMealListTab;

    private ObservableList<MealDetails> mealList = FXCollections.observableArrayList();

    @FXML
    void backAction(ActionEvent event) throws IOException {
        if(LoginPageController.id.substring(0,3).equals("890")){
            HostellizeApplication.changeScene("manager");
        }else{
            HostellizeApplication.changeScene("normaluser");
        }
    }

    @FXML
    void searchHostellerAction(ActionEvent event) {
        tabPane.getSelectionModel().select(searchHostellerTab);
    }

    @FXML
    void viewAction(ActionEvent event) throws SQLException {
        String date = Date.getValue().toString();
        findImage(date);
    }

    @FXML
    void viewHostellerAction(ActionEvent event) {
        tabPane.getSelectionModel().select(viewMealListTab);
    }
    void findImage(String Date) throws SQLException {
        String dbUrl = "jdbc:mysql://localhost:3306/hostellize";
        String dbUsername = "root";
        String dbPassword = "tanvir@5232112";

        String query = "SELECT image FROM mealdetails WHERE date = '" + Date + "';";

        Connection connection = DriverManager.getConnection(dbUrl,dbUsername,dbPassword);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            cashMemoImageView.setImage(new Image("file:/Applications/PlayGround/Programming/Final Project/Hostellize/"+resultSet.getString("image")));
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Image Not Found");
        }

    }

    public void fetchMealData() {
        try {
            String url = "jdbc:mysql://localhost/hostellize";
            String dbUsername = "root";
            String dbPassword = "tanvir@5232112";

            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            Statement statement = connection.createStatement();
            String query = "SELECT date, mealname, numberOfMeal, price FROM mealdetails;";

            ResultSet resultSet = statement.executeQuery(query);

            mealList.clear();

            while (resultSet.next()) {
                MealDetails mealDetails = new MealDetails(
                        resultSet.getString("date"),
                        resultSet.getString("mealname"),
                        resultSet.getString("numberOfMeal"),
                        resultSet.getString("price")
                );
                mealList.add(mealDetails);
            }

            userTableView.setItems(mealList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateCol.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getDate()));
        menuCol.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getMealName()));
        numberOfMealCol.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getNumberOfMael()));
        priceCol.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getPrice()));

        fetchMealData();
    }
}
