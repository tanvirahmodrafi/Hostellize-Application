package com.rafi.hostellize;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HostellizeApplication extends Application {
    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        showSplashScreen(stage);
        //changeScene("userMealDetails");
        //changeScene("showMealList");
    }

    public static void main(String[] args) {
        launch();
    }

    public static void changeScene(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HostellizeApplication.class.getResource(fxml + ".fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showSplashScreen(Stage stage) throws IOException {
        changeScene("splash-screen");

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(4000);  // 2 seconds
                return null;
            }
            @Override
            protected void succeeded() {
                try {
                    changeScene("login-page");
                } catch (IOException e) {
                    System.out.println("Login page failed in HostellizeApplication class");
                }
            }
        };

        new Thread(task).start();
    }
}
