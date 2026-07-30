package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repo.UserRepo;
import com.example.demo.util.SceneManager;
import com.example.demo.util.ValidationUtil;
import javafx.event.ActionEvent;
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
        String companyName = companyNameField.getText();

        if (ValidationUtil.isEmpty(name) || ValidationUtil.isEmpty(email) || ValidationUtil.isEmpty(password)) {
            error_message_txt.setText("All fields are required");
            return;
        }
        if (!ValidationUtil.isValidEmail(email)) {
            error_message_txt.setText("Enter a valid email address");
            return;
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            error_message_txt.setText("Enter a valid phone number");
            return;
        }
        if (!ValidationUtil.isValidPassword(password)) {
            error_message_txt.setText("Password must be at least 6 characters");
            return;
        }
        if (!ValidationUtil.passwordsMatch(password, confirmPassword)) {
            error_message_txt.setText("Passwords do not match");
            return;
        }
        if (!ValidationUtil.isCompanyNameValid(selectedUserType, companyName)) {
            error_message_txt.setText("Company name is required for Corporation/Enterprise accounts");
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

        if (!selectedUserType.equals("Individual") && companyName.isEmpty()) {
            error_message_txt.setText("Company name is required for Corporation/Enterprise accounts");
            return;
        }

        newUser.setCompanyName(companyName);
    }

    @FXML private Label companyNameLabel;
    @FXML private TextField companyNameField;

    @FXML
    private void individualSelected() {
        selectedUserType = "Individual";
        highlightSelected(individuaButton);
        toggleCompanyField(false);
    }

    @FXML
    private void corporationSelected() {
        selectedUserType = "Corporation";
        highlightSelected(corporationButton);
        toggleCompanyField(true);
    }

    @FXML
    private void enterpriseSelected() {
        selectedUserType = "Enterprise";
        highlightSelected(enterpriseButton);
        toggleCompanyField(true);
    }

    private void toggleCompanyField(boolean show) {
        companyNameLabel.setVisible(show);
        companyNameLabel.setManaged(show);
        companyNameField.setVisible(show);
        companyNameField.setManaged(show);
    }


    @FXML
    private Button individuaButton, corporationButton, enterpriseButton;

    private void highlightSelected(Button selected) {
        for (Button b : new Button[]{individuaButton, corporationButton, enterpriseButton}) {
            b.setStyle("-fx-background-color: blue; -fx-background-radius: 8;");
        }
        selected.setStyle("-fx-background-color: #21B373; -fx-background-radius: 8;");
    }
    @FXML
    private void goToLogin() {
        SceneManager.switchScene("login.fxml");
    }

}