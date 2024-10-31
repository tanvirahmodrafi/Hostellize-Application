package com.rafi.hostellize.controller;

import com.rafi.hostellize.HostellizeApplication;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import static com.rafi.hostellize.controller.LoginPageController.id;

public class NormalUserController implements Initializable {

    @FXML
    private Label NameLabel;

    @FXML
    private Label accountBalanceField;

    @FXML
    private Label emailLabel;

    @FXML
    private ImageView userImageView;

    @FXML
    private Label mealManagerNameField;

    @FXML
    private Label mealManagerPhoneField;

    @FXML
    private Label mealManagerRoomFiled;

    @FXML
    private ImageView menu;

    @FXML
    private ImageView menuBack;

    @FXML
    private Label phoneNumberLabel;

    @FXML
    private AnchorPane slider;

    @FXML
    private Label userIdLabel;

    @FXML
    private Label totalMealsField;

    @FXML
    void viewMealDetailsAction(ActionEvent event) throws IOException {
        HostellizeApplication.changeScene("userMealDetails");
    }

    @FXML
    void addMealAction(ActionEvent event) throws IOException {

        if(isMealManager()){
            HostellizeApplication.changeScene("addMeal");
        }else{
            HostellizeApplication.changeScene("userAddMeal");
        }
    }

    @FXML
    void viewTransactionAction(ActionEvent event) throws IOException {
        HostellizeApplication.changeScene("transactions");
    }

    @FXML
    void depositAction(ActionEvent event) throws IOException {
        HostellizeApplication.changeScene("deposit");
    }

    @FXML
    void showMealListAction(ActionEvent event) throws IOException {
        HostellizeApplication.changeScene("showMealList");
    }

    @FXML
    void signOutAction(ActionEvent event) throws IOException {
        HostellizeApplication.changeScene("login-page");
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        slider.setTranslateX(-186);
        menu.setOnMouseClicked(event -> toggleMenu(true));
        menuBack.setOnMouseClicked(event -> toggleMenu(false));

        String id = LoginPageController.id;
        loadUserData(id);

    }

    private void toggleMenu(boolean show) {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.4));
        slide.setNode(slider);
        slide.setToX(show ? 0 : -186);
        slide.play();
        slide.setOnFinished((ActionEvent e) -> {
            menu.setVisible(!show);
            menuBack.setVisible(show);
        });
    }

    void loadUserData(String id){
        String dbUrl = "jdbc:mysql://localhost:3306/hostellize";
        String dbUsername = "root";
        String dbPassword = "tanvir@5232112";

        String query = "SELECT id, name, email, phoneNumber, image FROM userinfo WHERE id = '" + id + "';";
        String mealManagerViewQuery = "SELECT name, phoneNumber, room FROM userinfo WHERE role = 'meal manager';";
        String balanceQuery = "SELECT SUM(amount) AS total_amount FROM transactions WHERE id = '" + id + "' " +
                "OR id IN ('890001', '890002', '890003', '890004', '890005', '890006', '890007', '890008', '890009', '890010', '890011');";

        String totalMeal = "SELECT SUM(numberOfMeal) AS total_meal FROM everymeal WHERE id = '" + id + "';";

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {

            // Load user data
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                if (resultSet.next()) {
                    NameLabel.setText("Name: "+resultSet.getString("name"));
                    userIdLabel.setText("User ID: "+resultSet.getString("id"));
                    emailLabel.setText("Email: "+resultSet.getString("email"));
                    phoneNumberLabel.setText("Contact Number: "+resultSet.getString("phoneNumber"));

                    // Load user image
                    String imageUrl = "file:/Applications/PlayGround/Programming/Final Project/Hostellize/" + resultSet.getString("image");
                    try {
                        Image image = new Image(imageUrl);
                        userImageView.setImage(image);
                    } catch (Exception e) {
                        System.out.println("Error loading image: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }

            // Load meal manager data
            try (Statement mealStatement = connection.createStatement();
                 ResultSet rs = mealStatement.executeQuery(mealManagerViewQuery)) {

                if (rs.next()) {
                    mealManagerNameField.setText(rs.getString("name"));
                    mealManagerPhoneField.setText("Phone: "+rs.getString("phoneNumber"));
                    mealManagerRoomFiled.setText("Room Number: "+rs.getString("room"));
                }
            }

            // Load balance data
            try (Statement balanceStatement = connection.createStatement();
                 ResultSet rs2 = balanceStatement.executeQuery(balanceQuery)) {

                if (rs2.next()) {
                    double totalAmount = rs2.getDouble("total_amount");

                    String formattedAmount = String.format("%.0f", totalAmount);
                    accountBalanceField.setText(formattedAmount);
                    if (totalAmount > 0) {
                        accountBalanceField.setStyle("-fx-text-fill: green;");
                    } else {
                        accountBalanceField.setStyle("-fx-text-fill: red;");
                    }
                }
            }

            // Load total meal data
            try (Statement mealStatement = connection.createStatement();
                 ResultSet rs3 = mealStatement.executeQuery(totalMeal)) {

                if (rs3.next()) {
                    totalMealsField.setText(rs3.getString("total_meal"));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error in loadUserData method: " + e.getMessage());
            e.printStackTrace();
        }

    }
    boolean isMealManager(){
        String dbUrl = "jdbc:mysql://localhost:3306/hostellize";
        String dbUsername = "root";
        String dbPassword = "tanvir@5232112";
        String r = "";

        String mealManagerQuery = "SELECT role FROM userinfo WHERE id = '"+ id+"';";
        try{
            Connection connection = DriverManager.getConnection(dbUrl,dbUsername,dbPassword);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(mealManagerQuery);

            if(resultSet.next()){
                r = resultSet.getString("role");
            }
        } catch (SQLException e) {
            System.out.println("isMealManager is not working");
        }

        if(r.equals( "meal manager")){
            return true;
        }
        return false;
    }
}
