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

public class ManagerController implements Initializable {

    @FXML
    private Label emailLabel;

    @FXML
    private Label managerIdLabel;

    @FXML
    private ImageView managerImage;

    @FXML
    private Label managerNameLabel;

    @FXML
    private Label mealManagerName;

    @FXML
    private ImageView menu;

    @FXML
    private ImageView menuBack;

    @FXML
    private Label phoneNumberLabel;

    @FXML
    private AnchorPane slider;

    @FXML
    private Label totalHosteler;

    @FXML
    private Label totalManager;

    @FXML
    private Label mealManagerPhoneLabel;

    @FXML
    private HBox totalManagerHBox;


    @FXML
    private HBox totalStudentVBox;

    @FXML
    private HBox totalStudentVBox1;

    @FXML
    void showUsersMealDetails(ActionEvent event) throws IOException {
        HostellizeApplication.changeScene("userMealDetails");
    }

        @FXML
    void addMealAction(ActionEvent event) throws IOException {
        HostellizeApplication.changeScene("addMeal");
    }

    @FXML
    void addMemberAction(ActionEvent event) throws IOException {
        HostellizeApplication.changeScene("createUser");
    }

    @FXML
    void showMealListAction(ActionEvent event) throws IOException {
        HostellizeApplication.changeScene("showMealList");
    }

    @FXML
    void removeMemberAction(ActionEvent event) throws IOException {
        HostellizeApplication.changeScene("removeUser");
    }

    @FXML
    void showMemberAction(ActionEvent event) throws IOException {
        HostellizeApplication.changeScene("showMember");
    }

    @FXML
    void signOutAction(ActionEvent event) throws IOException {
        HostellizeApplication.changeScene("login-page");
    }

    @FXML
    void addMealManagerAction(ActionEvent event) throws IOException {
        HostellizeApplication.changeScene("addMealManager");
    }

    @FXML
    void expensesAction(ActionEvent event) throws IOException {
        HostellizeApplication.changeScene("expenses");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        slider.setTranslateX(-186);
        menu.setOnMouseClicked(event -> toggleMenu(true));
        menuBack.setOnMouseClicked(event -> toggleMenu(false));

        String userId = LoginPageController.id;
        loadUserData(userId);
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

    void loadUserData(String userId) {
        String dbUrl = "jdbc:mysql://localhost:3306/hostellize";
        String dbUsername = "root";
        String dbPassword = "tanvir@5232112";

        String query = "SELECT id, name, email, phoneNumber, image FROM userinfo WHERE id = ?";
        String managerCountQuery = "SELECT COUNT(*) AS managerCount FROM userinfo WHERE role = 'manager'";
        String hostelerCountQuery = "SELECT COUNT(*) AS hostelerCount FROM userinfo WHERE role = 'normal User'";
        String mealManagerNameQuery = "SELECT name, phoneNumber FROM userinfo WHERE role = 'meal manager'";

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement userStatement = connection.prepareStatement(query);
             PreparedStatement managerCountStatement = connection.prepareStatement(managerCountQuery);
             PreparedStatement hostelerCountStatement = connection.prepareStatement(hostelerCountQuery);
             PreparedStatement mealManagerNameStatement = connection.prepareStatement(mealManagerNameQuery)) {

            // Set user ID for the query
            userStatement.setString(1, userId);

            // Fetch user data
            try (ResultSet resultSet = userStatement.executeQuery()) {
                if (resultSet.next()) {
                    managerIdLabel.setText("ID: " + resultSet.getString("id"));
                    managerNameLabel.setText("Name: "+resultSet.getString("name"));
                    emailLabel.setText("Email: "+resultSet.getString("email"));
                    phoneNumberLabel.setText("Contact Number: "+resultSet.getString("phoneNumber"));

                    String imageUrl = "file:/Applications/PlayGround/Programming/Final Project/Hostellize/"+(resultSet.getString("image"));
                    try {
                        Image image = new Image(imageUrl);
                        managerImage.setImage(image);
                    } catch (Exception e) {
                        System.out.println("Error loading image: " + e.getMessage());
                        e.printStackTrace();
                    }

                }
            }

            try (ResultSet managerResultSet = managerCountStatement.executeQuery()) {
                if (managerResultSet.next()) {
                    int managerCount = managerResultSet.getInt("managerCount");
                    totalManager.setText(String.valueOf(managerCount));
                }
            }

            // Count hostelers
            try (ResultSet hostelerResultSet = hostelerCountStatement.executeQuery()) {
                if (hostelerResultSet.next()) {
                    int hostelerCount = hostelerResultSet.getInt("hostelerCount");
                    totalHosteler.setText(String.valueOf(hostelerCount));

                }
            }

            // Fetch meal manager's name
            try (ResultSet mealManagerResultSet = mealManagerNameStatement.executeQuery()) {
                if (mealManagerResultSet.next()) {
                    mealManagerName.setText(mealManagerResultSet.getString("name"));
                    mealManagerPhoneLabel.setText("Phone: "+mealManagerResultSet.getString("phoneNumber"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Improved error handling
        }
    }
}



