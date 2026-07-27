package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.util.SceneManager;
import com.example.demo.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class SettingsController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private CheckBox darkModeToggle;

    @FXML
    public void initialize() {
        User user = SessionManager.getCurrentUser();
        nameField.setText(user.getName());
        emailField.setText(user.getEmail());
    }

    @FXML
    private void handleSave() {
        // TODO: call a UserService.updateProfile() once you build it
    }

    @FXML
    private void handleLogout() {
        SessionManager.clear();
        SceneManager.switchScene("login.fxml");
    }
}