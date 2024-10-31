package com.rafi.hostellize.controller;

import com.rafi.hostellize.HostellizeApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class LoginPageController implements Initializable {
    public static String id;
    @FXML
    private ImageView imageView;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField userIdField;

    @FXML
    void signInAction(ActionEvent event) throws IOException {
        String userId = userIdField.getText();
        String password = passwordField.getText();
        if(validateLogin(userId, password)) {
            String userType = userId.substring(0, 3);
            id = userId;
            if(userType.equals("123")) {
                HostellizeApplication.changeScene("admin");
            }else if(userType.equals("890")) {
                try{
                    HostellizeApplication.changeScene("manager");
                }catch(Exception e){
                    System.out.println("manager file problem");
                }

            }else{
                HostellizeApplication.changeScene("normaluser");
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid username or password");
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Image image = new Image("file:/Applications/PlayGround/Programming/Final Project/Hostellize/Image/Screenshot 2024-10-11 at 2.34.55 PM.png");
        imageView.setImage(image);
    }


    private boolean validateLogin(String userid, String password) {
        String url = "jdbc:mysql://localhost/hostellize"; //database URL
        String dbUsername = "root";  // database username
        String dbPassword = "tanvir@5232112";  // database password

        String query = "SELECT password FROM id_password WHERE id = '" + userid + "'";


        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                String pass = resultSet.getString("password");
                    return pass.equals(password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
