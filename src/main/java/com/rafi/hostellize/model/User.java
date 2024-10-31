package com.rafi.hostellize.model;

import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class User {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String imageURL;
    private String role;
    private String room;
    private String password;
    private String permanentAddress;

    public User(String id, String name, String email, String phoneNumber, String imageURL, String role, String room, String password, String permanentAddress) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.imageURL = imageURL;
        this.role = role;
        this.room = room;
        this.password = password;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getImageURL() {
        return imageURL;
    }
    public String getRole() {
        return role;
    }
    public String getRoom() {
        return room;
    }
    public String getPassword() {
        return password;
    }
    public String getPermanentAddress() {
        return permanentAddress;
    }

    public static void insertAdminToDB(User user) {
        String url = "jdbc:mysql://localhost:3306/hostellize";
        String dbUsername = "root";
        String dbPassword = "tanvir@5232112";

        try {

            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            Statement statement = connection.createStatement();

            String insertQuery = "INSERT INTO userinfo (id, name, email, phoneNumber, image, role, room,address) VALUES ('" +
                    user.getId() + "', '" +
                    user.getName() + "', '" +
                    user.getEmail() + "', '" +
                    user.getPhoneNumber() + "', '" +
                    user.getImageURL() + "', '" +
                    user.getRole() + "', '" +
                    user.getRoom() + "', '"+
                    user.getPermanentAddress() + "')";

            int rowsAffected = statement.executeUpdate(insertQuery);
            if (rowsAffected > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Successfully added user to the database");
                alert.showAndWait();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void insertToIdPass(User user) {
        String url = "jdbc:mysql://localhost:3306/hostellize";
        String dbUsername = "root";
        String dbPassword = "tanvir@5232112";

        String query = "INSERT INTO id_password (id, password) VALUES ('" + user.getId() + "', '" + user.getPassword() + "')";

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(query);

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting into id_password: " + e.getMessage(), e);
        }
    }


}
