package com.example.demo.controller;

import com.example.demo.util.SceneManager;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.util.Duration;

public class SplashController {

    @FXML
    public void initialize() {
        PauseTransition delay = new PauseTransition(Duration.seconds(20));
        delay.setOnFinished(e -> SceneManager.switchScene("login.fxml"));
        delay.play();
    }
}