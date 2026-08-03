package com.example.demo;

import com.example.demo.util.SceneManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        SceneManager.setPrimaryStage(stage);

        Parent root = FXMLLoader.load(
                getClass().getResource("/com/example/demo/splash.fxml")
        );

        Scene scene = new Scene(root, 920, 520);

        // Attach the global stylesheet
        scene.getStylesheets().add(getClass().getResource("/com/example/demo/styles.css").toExternalForm());

        stage.setTitle("ExpenseTracker");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }
}