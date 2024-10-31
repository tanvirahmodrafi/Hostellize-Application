package com.rafi.hostellize.controller;

import com.rafi.hostellize.HostellizeApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddMealController implements Initializable {

    @FXML
    private CheckBox agreeCheckBox;

    @FXML
    private ImageView cashMemoImageView;

    @FXML
    private TextField costField;

    @FXML
    private DatePicker dateField;

    @FXML
    private TextField mealCountField;

    @FXML
    private TextArea menuField;

    private String mealImage;

    @FXML
    void backButtonAction(ActionEvent event) throws IOException {
        if(LoginPageController.id.substring(0,3).equals("890")){
            HostellizeApplication.changeScene("manager");
        }else{
            HostellizeApplication.changeScene("normaluser");
        }

    }

    @FXML
    void saveAction(ActionEvent event) throws SQLException {
        if (agreeCheckBox.isSelected()) {
            String cost = costField.getText();
            String date = dateField.getValue().toString();
            String mealCount = mealCountField.getText();
            String menu = menuField.getText();
            boolean isPresent = false;

            if (dateField.getValue().isBefore(LocalDate.now())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("You cannot edit a meal from past days.");
                alert.showAndWait();
                return;
            }

            String dbUrl = "jdbc:mysql://localhost:3306/hostellize";
            String dbUsername = "root";
            String dbPassword = "tanvir@5232112";
            String query2 = "SELECT EXISTS (SELECT 1 FROM mealdetails WHERE date = '" + date + "');";
            String query;

            try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                 Statement statement = connection.createStatement()) {

                ResultSet resultSet = statement.executeQuery(query2);

                if (resultSet.next() && resultSet.getInt(1) == 1) {
                    isPresent = true;
                }

                if (isPresent) {
                    query = "UPDATE mealdetails SET mealname = '" + menu + "', numberOfMeal = '" + mealCount + "', price = '" + cost + "', image = '" + mealImage + "' WHERE date = '" + date + "'";
                } else {
                    query = "INSERT INTO mealdetails (date, mealname, numberOfMeal, price, image) VALUES ('" + date + "', '" + menu + "', '" + mealCount + "', '" + cost + "', '" + mealImage + "')";
                }

                statement.execute(query);

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Meal details saved successfully.");
                successAlert.showAndWait();

            } catch (SQLException e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Database error: " + e.getMessage());
                errorAlert.showAndWait();
            }
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
                cashMemoImageView.setImage(image);

                // Store the image path
                mealImage = destinationPath;
                success = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        event.setDropCompleted(success);
        event.consume();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cashMemoImageView.setOnDragOver(event -> {
            if (event.getGestureSource() != cashMemoImageView && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
        cashMemoImageView.setOnDragDropped(event -> handleImageDrop(event));

    }
}
