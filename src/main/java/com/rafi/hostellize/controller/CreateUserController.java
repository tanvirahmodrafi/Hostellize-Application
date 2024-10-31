package com.rafi.hostellize.controller;

import com.rafi.hostellize.HostellizeApplication;
import com.rafi.hostellize.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
import java.util.ResourceBundle;

public class CreateUserController implements Initializable {

    @FXML
    private TextField emailField;

    @FXML
    private TextArea permanentAddressField;

    @FXML
    private TextField name;

    @FXML
    private TextField phoneNumber;

    @FXML
    private ChoiceBox<String> roleChoiceBox;

    @FXML
    private TextField roomNumberField;

    @FXML
    private TextField userId;

    @FXML
    private ImageView userImage;

    @FXML
    private TextField passwordField;

    private String imagePath;

    @FXML
    void addAction(ActionEvent event) {
        String email = emailField.getText();
        String userName = name.getText();
        String phone = phoneNumber.getText();
        String role = roleChoiceBox.getValue();
        String roomNumber = roomNumberField.getText();
        String id = userId.getText();
        String password = passwordField.getText();
        String permanentAddress = permanentAddressField.getText();
        if(roomNumber.isEmpty()){
            roomNumber = null;
        }
        User user = new User(id,userName,email,phone,imagePath,role,roomNumber,password,permanentAddress);
        if(email.isEmpty() || userName.isEmpty()||phone.isEmpty()|| role.isEmpty()|| roomNumber.isEmpty()||id.isEmpty()||password.isEmpty()||permanentAddress.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all the fields");
            alert.showAndWait();
        }else{
            User.insertAdminToDB(user);
            User.insertToIdPass(user);
        }

    }
    @FXML
    void cancelAction(ActionEvent event) throws IOException {
        if(LoginPageController.id.substring(0,3).equals("123")){
            HostellizeApplication.changeScene("admin");
        }else{
            HostellizeApplication.changeScene("manager");
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(LoginPageController.id.substring(0,3).equals("123")){
            roleChoiceBox.getItems().addAll("Admin", "Manager", "Normal User");
            roleChoiceBox.getSelectionModel().selectFirst();
        }else{
            roleChoiceBox.getItems().addAll("Manager","Meal Manager","Normal User");
            roleChoiceBox.getSelectionModel().selectLast();
        }
        userImage.setOnDragOver(event -> {
            if (event.getGestureSource() != userImage && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        userImage.setOnDragDropped(event -> handleImageDrop(event));
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
                userImage.setImage(image);

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
