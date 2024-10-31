package com.rafi.hostellize.controller;

import com.rafi.hostellize.HostellizeApplication;
import com.rafi.hostellize.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ResourceBundle;

public class ShowMemberController implements Initializable {
    @FXML
    private TextField editEmailField;

    @FXML
    private TextField editNameField;

    @FXML
    private TextField editPasswordField;

    @FXML
    private TextArea editPermanentAddressField;

    @FXML
    private TextField editPhoneNumberField;

    @FXML
    private ChoiceBox<String> editRoleChoiceBox;


    @FXML
    private TextField editRoomNumberField;

    @FXML
    private TextField editUserIdField;

    @FXML
    private ImageView editUserImage;
    @FXML
    private TableView<User> userTableView;

    @FXML
    private TableColumn<User, String> emailCol;

    @FXML
    private TableColumn<User, String> idCol;

    @FXML
    private TableColumn<User, String> nameCol;

    @FXML
    private TableColumn<User, String> phoneCol;

    @FXML
    private TableColumn<User, String> roleCol;

    @FXML
    private TableColumn<User, String> roomNumberCol;

    @FXML
    private Label emailLabel;

    @FXML
    private ImageView hostellerImageView;

    @FXML
    private TextField idField;

    @FXML
    private Label moneyLabel;

    @FXML
    private Label phoneNumberLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Label roomLabel;

    @FXML
    private Tab searchHostellerTab;

    @FXML
    private Tab editInfoTab;

    @FXML
    private ImageView showDetailsImage;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab viewHostellerTab;

    @FXML
    private Label viewIdLabel;

    @FXML
    private Label permanentAddressField;

    @FXML
    private Label viewNameLabel;

    private String imagePath;

    private String newName,newEmail,newPassword,newRole,newRoom,newAddress,newPhoneNumber;
    private ImageView newImage;

    private ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    void backAction(ActionEvent event) throws IOException {
        HostellizeApplication.changeScene("manager");
    }
    String ids;

    @FXML
    void searchAction(ActionEvent event) throws SQLException {
        String id = idField.getText();
        showDetails(id);
        ids = id;
        showDetailsImage.setVisible(false);
    }

    @FXML
    void searchHostellerAction(ActionEvent event) {
        tabPane.getSelectionModel().select(searchHostellerTab);
    }

    @FXML
    void viewHostellerAction(ActionEvent event) {
        tabPane.getSelectionModel().select(viewHostellerTab);
    }

    @FXML
    void editAction(ActionEvent event) {
        tabPane.getSelectionModel().select(editInfoTab);
    }

    @FXML
    void saveAction(ActionEvent event) throws SQLException {
        updateUserInfo();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        emailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        phoneCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhoneNumber()));
        roleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRole()));
        roomNumberCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRoom()));

        // Fetch and display data
        fetchData();

        editRoleChoiceBox.getItems().addAll("Manager","Meal Manager","Normal User");
        editUserImage.setOnDragOver(event -> {
            if (event.getGestureSource() != editUserImage && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        editUserImage.setOnDragDropped(event -> handleImageDrop(event));
    }

    String i;

    void showDetails(String id) {
        try{
            String url = "jdbc:mysql://localhost/hostellize";
            String dbUsername = "root";
            String dbPassword = "tanvir@5232112";

            String query = "SELECT id, name, email, phonenumber, image, role, room,address FROM userinfo WHERE id = " + id;
            String balanceQuery = "SELECT SUM(amount) AS total_amount FROM transactions WHERE id = '" + id + "';";


            String passQuery = "SELECT password FROM id_password WHERE id = '" + id + "';";

            Connection connection = DriverManager.getConnection(url,dbUsername,dbPassword);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            if (rs.next()) {
                viewIdLabel.setText("ID: "+rs.getString("id"));
                editUserIdField.setText(rs.getString("id"));
                roomLabel.setText("Room No: "+rs.getString("room"));
                editRoomNumberField.setText(rs.getString("room"));
                emailLabel.setText(rs.getString("email"));
                editEmailField.setText(rs.getString("email"));
                phoneNumberLabel.setText(rs.getString("phonenumber"));
                editPhoneNumberField.setText(rs.getString("phonenumber"));
                roleLabel.setText("Role: "+rs.getString("role"));
                editRoleChoiceBox.getSelectionModel().select(rs.getString("role"));
                viewNameLabel.setText(rs.getString("name"));
                editNameField.setText(rs.getString("name"));
                Image image = new Image("file:/Applications/PlayGround/Programming/Final Project/Hostellize/"+rs.getString("image"));
                hostellerImageView.setImage(image);
                editUserImage.setImage(image);
                i = rs.getString("image");
                permanentAddressField.setText("Permanent Address: "+rs.getString("address"));
                editPermanentAddressField.setText(rs.getString("address"));
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Wrong User ID!!");
                alert.showAndWait();
            }
            ResultSet rs2 = statement.executeQuery(balanceQuery);
            if(rs2.next()){
                double totalAmount = rs2.getDouble("total_amount");

                String formattedAmount = String.format("%.0f", totalAmount);
                moneyLabel.setText(formattedAmount);
                if (totalAmount > 0) {
                    moneyLabel.setStyle("-fx-text-fill: green;");
                } else {
                    moneyLabel.setStyle("-fx-text-fill: red;");
                }
            }
            ResultSet rs4 = statement.executeQuery(passQuery);
            if (rs4.next()) {
                editPasswordField.setText(rs4.getString("password"));
            }

        }catch(Exception e){
            System.out.println("showDetails method not working!");
        }


    }

    public void updateUserInfo() throws SQLException {
        // Fetch updated values from the UI fields
        newName = editNameField.getText();
        newEmail = editEmailField.getText();
        newPhoneNumber = editPhoneNumberField.getText();
        newPassword = editPasswordField.getText();
        newRole = editRoleChoiceBox.getValue();
        newRoom = editRoomNumberField.getText();
        newAddress = editPermanentAddressField.getText();
        String imageFilePath;
        if (imagePath != null) {
            imageFilePath = imagePath;
        } else {
            imageFilePath = i;
        }


        // Database connection setup
        String url = "jdbc:mysql://localhost/hostellize";
        String dbUsername = "root";
        String dbPassword = "tanvir@5232112";

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            Statement statement = connection.createStatement();

            // Construct the SQL query to update user information
            String editInfo = "UPDATE userinfo SET "
                    + "name = '" + newName + "', "
                    + "email = '" + newEmail + "', "
                    + "phonenumber = '" + newPhoneNumber + "', "
                    + "image = '" + imageFilePath + "', "
                    + "role = '" + newRole + "', "
                    + "room = '" + newRoom + "', "
                    + "address = '" + newAddress + "' "
                    + "WHERE id = " + ids + ";";

            // Execute the update
            int rowsAffected = statement.executeUpdate(editInfo);

            // Provide feedback based on success
            if (rowsAffected > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("User information updated successfully.");
                alert.showAndWait();

                // Optionally, update the password in the `id_password` table if needed
                if (newPassword != null && !newPassword.isEmpty()) {
                    String updatePasswordQuery = "UPDATE id_password SET password = '" + newPassword + "' WHERE id = " + ids + ";";
                    statement.executeUpdate(updatePasswordQuery);
                }
                // Optionally, refresh the UI or data displayed
                fetchData();
            } else {
                System.out.println("No user information was updated.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void fetchData() {
        userList.clear();  // Clear previous data
        try {
            String url = "jdbc:mysql://localhost/hostellize";
            String dbUsername = "root";
            String dbPassword = "tanvir@5232112";

            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            Statement statement = connection.createStatement();
            String query = "SELECT id, name, email, phonenumber, role, room FROM userinfo";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                User user = new User(
                        resultSet.getString("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("phonenumber"),
                        null,
                        resultSet.getString("role"),
                        resultSet.getString("room"),
                        null,
                        null
                );
                userList.add(user);
            }

            userTableView.setItems(userList);

        } catch (Exception e) {
            System.out.println("fetchData method not working!");
        }
    }

    private void handleImageDrop(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        boolean success = false;

        if (dragboard.hasFiles()) {
            File file = dragboard.getFiles().get(0);
            try {
                // Create the "Images" directory if it doesn't exist
                String imagesDirectory = "Image";
                Files.createDirectories(Paths.get(imagesDirectory));

                // Generate a unique file name if the file already exists
                String destinationPath = imagesDirectory + "/" + file.getName();
                File destFile = new File(destinationPath);
                int counter = 1;
                while (destFile.exists()) {
                    String newFileName = file.getName().replaceFirst("(\\.[^.]+$)", "(" + counter + ")$1");
                    destinationPath = imagesDirectory + "/" + newFileName;
                    destFile = new File(destinationPath);
                    counter++;
                }

                // Copy the file to the "Images" folder
                Files.copy(file.toPath(), Paths.get(destinationPath));

                // Update the ImageView with the new image
                Image image = new Image(destFile.toURI().toString());
                editUserImage.setImage(image);

                // Store the image path
                imagePath = destinationPath;
                success = true;
            } catch (IOException e) {
                e.printStackTrace();
                // You could show an error dialog to the user here
            }
        }

        event.setDropCompleted(success);
        event.consume();
    }
}
