package com.example.demo.util;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class ToastUtil {

    public static void show(StackPane rootPane, String message) {
        show(rootPane, message, "#1C2130");
    }

    public static void showSuccess(StackPane rootPane, String message) {
        show(rootPane, message, "#21B373");
    }

    public static void showError(StackPane rootPane, String message) {
        show(rootPane, message, "#E64D4D");
    }

    private static void show(StackPane rootPane, String message, String colorHex) {
        Label toastLabel = new Label(message);
        toastLabel.setStyle(
                "-fx-background-color: " + colorHex + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 12 20 12 20;" +
                        "-fx-font-size: 13px;"
        );

        StackPane.setAlignment(toastLabel, Pos.BOTTOM_CENTER);
        StackPane.setMargin(toastLabel, new javafx.geometry.Insets(0, 0, 30, 0));

        rootPane.getChildren().add(toastLabel);

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
}