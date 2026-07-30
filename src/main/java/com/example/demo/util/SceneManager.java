package com.example.demo.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SceneManager {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(String fxmlFile) {
        String path = "/com/example/demo/" + fxmlFile;
        URL url = SceneManager.class.getResource(path);

        if (url == null) {
            System.err.println("FXML NOT FOUND at: " + path);
            System.err.println("Check that " + fxmlFile + " is in src/main/resources/com/example/demo/");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(root);
        if (PreferencesUtil.isDarkModeEnabled()) {
            scene.getStylesheets().add(SceneManager.class.getResource("/com/example/demo/dark-theme.css").toExternalForm());
        }
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}