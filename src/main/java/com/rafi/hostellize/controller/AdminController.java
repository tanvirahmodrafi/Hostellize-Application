package com.rafi.hostellize.controller;

import com.rafi.hostellize.HostellizeApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    private Label adminIdLabel;

    @FXML
    private ImageView adminImageView;

    @FXML
    private Label adminNameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label phoneNumberLabel;

    @FXML
    private Label totalEarning;

    @FXML
    private Label totalHosteler;

    @FXML
    private Label totalManager;

    @FXML
    private HBox totalManagerHBox;

    @FXML
    private HBox totalStudentVBox;

    @FXML
    private HBox totalStudentVBox1;

    @FXML
    void createUserAction(ActionEvent event) throws IOException {
        HostellizeApplication.changeScene("createUser");
    }

    @FXML
    void signUpAction(ActionEvent event) throws IOException {
        HostellizeApplication.changeScene("login-page");
    }

    @FXML
    void removeUserAction(ActionEvent event) throws IOException {
        HostellizeApplication.changeScene("removeUser");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String userId = LoginPageController.id;
        loadUserData(userId);
        //setCircularImage(adminImageView);
    }
//    private void setCircularImage(ImageView imageView) {
//        Circle clip = new Circle(100, 100, 100);
//        imageView.setClip(clip);
//    }

    private void loadUserData(String userId) {
        String dbUrl = "jdbc:mysql://localhost:3306/hostellize";
        String dbUsername = "root";
        String dbPassword = "tanvir@5232112";


        String query = "SELECT id, name, email, phoneNumber, image FROM userinfo WHERE id = '" + userId + "'";

        String managerCountQuery = "SELECT COUNT(*) AS managerCount FROM userinfo WHERE role = 'manager'";
        String hostelerCountQuery = "SELECT COUNT(*) AS hostelerCount FROM userinfo WHERE role = 'normal User'";

        try {
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            Statement statement1 = connection.createStatement();
            Statement statement2 = connection.createStatement();
            Statement statement3 = connection.createStatement();

            //For user info
            ResultSet resultSet = statement1.executeQuery(query);
            if (resultSet.next()) {
                adminIdLabel.setText("ID: " + resultSet.getString("id"));
                adminNameLabel.setText(resultSet.getString("name"));
                emailLabel.setText(resultSet.getString("email"));
                phoneNumberLabel.setText(resultSet.getString("phoneNumber"));

                String imageUrl = "file:/Applications/PlayGround/Programming/Final Project/Hostellize/"+resultSet.getString("image");
                Image image = new Image(imageUrl);
                adminImageView.setImage(image);
                // setCircularImage(adminImageView);
            }

            // Count managers
            ResultSet managerResultSet = statement2.executeQuery(managerCountQuery);
            int managerCount=0;
            if (managerResultSet.next()) {
                managerCount = managerResultSet.getInt("managerCount");
                totalManager.setText(String.valueOf(managerCount));
            }

            // Count hostelers
            ResultSet hostelerResultSet = statement3.executeQuery(hostelerCountQuery);
            int hostelerCount=1;
            if (hostelerResultSet.next()) {
                hostelerCount = hostelerResultSet.getInt("hostelerCount");
                totalHosteler.setText(String.valueOf(hostelerCount+1));
            }

            // Calculate and set total earnings
            int totalEarningsValue = (managerCount * 100) + ((hostelerCount+1) * 50);
            totalEarning.setText(String.valueOf(totalEarningsValue));

            // Close the statements and connection
            statement1.close();
            statement2.close();
            statement3.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
