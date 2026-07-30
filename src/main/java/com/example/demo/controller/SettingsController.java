package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repo.UserRepo;
import com.example.demo.util.AlertUtil;
import com.example.demo.util.SceneManager;
import com.example.demo.util.SessionManager;
import com.example.demo.util.ValidationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class SettingsController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private CheckBox darkModeToggle;

    @FXML private VBox teamSection;
    @FXML private TextField memberEmailField;

    @FXML
    public void initialize() {
        User user = SessionManager.getCurrentUser();
        nameField.setText(user.getName());
        emailField.setText(user.getEmail());

        boolean isBusiness = SessionManager.isBusinessAccount();
        teamSection.setVisible(isBusiness);
        teamSection.setManaged(isBusiness);
    }

    @FXML
    private void handleAddTeamMember() {
        String email = memberEmailField.getText();
        if (ValidationUtil.isEmpty(email)) return;

        boolean added = new UserRepo().addTeamMember(SessionManager.getCurrentUser().getId(), email);
        if (added) {
            AlertUtil.showInfo("Success", "Team member added.");
            memberEmailField.clear();
        } else {
            AlertUtil.showError("Failed", "No unassigned account found with that email.");
        }
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

    @FXML private void goToDashboard() {
        SceneManager.switchScene("dashboard.fxml");
    }
}