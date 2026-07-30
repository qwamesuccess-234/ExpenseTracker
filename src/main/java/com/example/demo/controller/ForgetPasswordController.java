package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repo.UserRepo;
import com.example.demo.util.PasswordUtil;
import com.example.demo.util.SceneManager;
import com.example.demo.util.ValidationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ForgetPasswordController {

    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label error_message_txt;

    private final UserRepo userRepo = new UserRepo();

    @FXML
    private void handleReset() {
        String email = emailField.getText();
        String phone = phoneField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (ValidationUtil.isEmpty(email) || ValidationUtil.isEmpty(phone) || ValidationUtil.isEmpty(newPassword)) {
            error_message_txt.setText("All fields are required");
            return;
        }

        if (!ValidationUtil.isValidPassword(newPassword)) {
            error_message_txt.setText("Password must be at least 6 characters");
            return;
        }

        if (!ValidationUtil.passwordsMatch(newPassword, confirmPassword)) {
            error_message_txt.setText("Passwords do not match");
            return;
        }

        User user = userRepo.findByEmail(email);

        // Identity check: email AND phone must both match the same account
        if (user == null || user.getPhone() == null || !user.getPhone().trim().equals(phone.trim())) {
            error_message_txt.setText("We couldn't verify an account with that email and phone");
            return;
        }

        boolean updated = userRepo.updatePassword(user.getId(), PasswordUtil.hash(newPassword));
        if (updated) {
            error_message_txt.setStyle("-fx-text-fill: #21B373;");
            error_message_txt.setText("Password reset! Redirecting to login...");
            SceneManager.switchScene("login.fxml");
        } else {
            error_message_txt.setText("Something went wrong. Try again.");
        }
    }

    @FXML
    private void goToLogin() {
        SceneManager.switchScene("login.fxml");
    }
}