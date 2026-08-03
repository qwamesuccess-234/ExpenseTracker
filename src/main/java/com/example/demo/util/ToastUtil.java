package com.example.demo.util;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class ToastUtil {

    public static void show(Pane rootPane, String message) {
        show(rootPane, message, "#1C2130");
    }

    public static void showSuccess(Pane rootPane, String message) {
        show(rootPane, message, "#21B373");
    }

    public static void showError(Pane rootPane, String message) {
        show(rootPane, message, "#E64D4D");
    }

    private static void show(Pane rootPane, String message, String colorHex) {
        Label toastLabel = new Label(message);
        toastLabel.setStyle(
                "-fx-background-color: " + colorHex + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 12 20 12 20;" +
                        "-fx-font-size: 13px;"
        );

        rootPane.getChildren().add(toastLabel);

        // Position near the bottom-center of rootPane once the label's size is known.
        toastLabel.widthProperty().addListener((obs, oldW, newW) -> {
            toastLabel.setLayoutX((rootPane.getWidth() - newW.doubleValue()) / 2);
        });
        toastLabel.setLayoutY(rootPane.getHeight() - 30 - toastLabel.getHeight());
        toastLabel.heightProperty().addListener((obs, oldH, newH) -> {
            toastLabel.setLayoutY(rootPane.getHeight() - 30 - newH.doubleValue());
        });

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), toastLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition stay = new PauseTransition(Duration.seconds(2));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), toastLabel);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> rootPane.getChildren().remove(toastLabel));

        fadeIn.setOnFinished(e -> stay.play());
        stay.setOnFinished(e -> fadeOut.play());
        fadeIn.play();
    }

    public static void show(String expenseSaved) {
    }
}