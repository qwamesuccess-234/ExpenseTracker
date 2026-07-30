package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repo.UserRepo;
import com.example.demo.util.PasswordUtil;
import com.example.demo.util.SceneManager;
import com.example.demo.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField emailField, passwordField;
    @FXML private Label error_message_txt;

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            error_message_txt.setText("All fields are required");
            return;
        }

        UserRepo userRepo = new UserRepo();
        User user = userRepo.findByEmail(email);

        if (user == null || !PasswordUtil.matches(password, user.getPassword())) {
            error_message_txt.setText("Invalid email or password");
            return;
        }

        if (!user.isStatus()) {
            error_message_txt.setText("This account has been deactivated");
            return;
        }

        SessionManager.setCurrentUser(user);
        SceneManager.switchScene("dashboard.fxml");
    }
    @FXML
    private void goToForgotPassword() {
        SceneManager.switchScene("forgot_password.fxml");
    }

    @FXML
    private void goToRegister(ActionEvent event) {
        SceneManager.switchScene("register.fxml");
    }
}