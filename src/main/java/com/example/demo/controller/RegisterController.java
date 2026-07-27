package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repo.UserRepo;
import com.example.demo.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML
    private TextField emailField, passwordField, comfirm_PasswordField, fullnameField;

    @FXML
    private Label error_message_txt;

    private String selectedUserType = "Individual"; // default, wire up your 3 buttons to set this

    @FXML
    private TextField phoneField;

    @FXML
    private void handleregister() {
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = comfirm_PasswordField.getText();
        String name = fullnameField.getText();
        String phone = phoneField.getText();

        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            error_message_txt.setText("All fields are required");
            return;
        }

        if (!password.equals(confirmPassword)) {
            error_message_txt.setText("Passwords do not match");
            return;
        }

        UserRepo userRepo = new UserRepo();
        if (userRepo.existByEmail(email)) {
            error_message_txt.setText("An account with this email already exists");
            return;
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setName(name);
        newUser.setPhone(phone);
        newUser.setUserType(selectedUserType);
        newUser.setStatus(true);

        boolean saved = userRepo.save(newUser);
        if (saved) {
            SceneManager.switchScene("login.fxml");
        } else {
            error_message_txt.setText("Something went wrong. Try again.");
        }
    }

    @FXML
    private void individualSelected() {
        selectedUserType = "Individual";
        highlightSelected(individuaButton);
    }

    @FXML
    private void corporationSelected() {
        selectedUserType = "Corporation";
        highlightSelected(corporationButton);
    }

    @FXML
    private void enterpriseSelected() {
        selectedUserType = "Enterprise";
        highlightSelected(enterpriseButton);
    }

    @FXML
    private Button individuaButton, corporationButton, enterpriseButton;

    private void highlightSelected(Button selected) {
        for (Button b : new Button[]{individuaButton, corporationButton, enterpriseButton}) {
            b.setStyle("-fx-background-color: blue; -fx-background-radius: 8;");
        }
        selected.setStyle("-fx-background-color: #21B373; -fx-background-radius: 8;");
    }
}