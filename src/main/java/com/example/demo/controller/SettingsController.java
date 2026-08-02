package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repo.UserRepo;
import com.example.demo.service.BackupService;
import com.example.demo.util.*;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class SettingsController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private CheckBox darkModeToggle;

    @FXML private VBox teamSection;
    @FXML private TextField memberEmailField;

    @FXML
    private BorderPane rootPane;

    private final BackupService backupService = new BackupService();

    @FXML private ImageView settingsProfileImageView;

    @FXML
    private void handleChangeProfilePicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(nameField.getScene().getWindow());
        if (file == null) return;

        try {
            java.nio.file.Path profileDir = Paths.get(System.getProperty("user.home"), ".expensetracker", "profile_pictures");
            Files.createDirectories(profileDir);
            String fileName = "user_" + SessionManager.getCurrentUser().getId() + "_" + file.getName();
            java.nio.file.Path destination = profileDir.resolve(fileName);
            Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

            String path = destination.toString();
            new UserRepo().updateProfilePicture(SessionManager.getCurrentUser().getId(), path);
            SessionManager.getCurrentUser().setProfilePicturePath(path); // keep session in sync
            settingsProfileImageView.setImage(new Image(file.toURI().toString()));

            ToastUtil.showSuccess(rootPane, "Profile picture updated");
        } catch (IOException e) {
            AlertUtil.showError("Upload Failed", "Could not update profile picture.");
        }
    }

    @FXML
    private void handleBackup() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("expense_db_backup.sql");
        File file = fileChooser.showSaveDialog(nameField.getScene().getWindow());
        if (file == null) return;

        boolean success = backupService.backupDatabase(
                "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin", "root", "Qwame123success", "expense_db", file
        );
        if (success) ToastUtil.showSuccess(rootPane, "Backup created successfully");
        else AlertUtil.showError("Backup Failed", "Check your MySQL bin path is correct.");
    }

    @FXML
    private void handleRestore() {
        boolean confirmed = AlertUtil.confirm("Restore Database", "This will overwrite current data. Continue?");
        if (!confirmed) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Files", "*.sql"));
        File file = fileChooser.showOpenDialog(nameField.getScene().getWindow());
        if (file == null) return;

        boolean success = backupService.restoreDatabase(
                "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin", "root", "Qwame123success", "expense_db", file
        );
        if (success) ToastUtil.showSuccess(rootPane, "Database restored");
        else AlertUtil.showError("Restore Failed", "Check the file and MySQL bin path.");
    }
    @FXML
    public void initialize() {
        User user = SessionManager.getCurrentUser();
        nameField.setText(user.getName());
        emailField.setText(user.getEmail());

        boolean isBusiness = SessionManager.isBusinessAccount();
        teamSection.setVisible(isBusiness);
        teamSection.setManaged(isBusiness);
        darkModeToggle.setSelected(PreferencesUtil.isDarkModeEnabled());
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
        boolean darkMode = darkModeToggle.isSelected();
        PreferencesUtil.setDarkModeEnabled(darkMode);
        applyTheme(darkMode);
        ToastUtil.showSuccess(rootPane, "Settings saved");
    }
    private void applyTheme(boolean darkMode) {
        Scene scene = darkModeToggle.getScene();
        scene.getStylesheets().clear();
        if (darkMode) {
            scene.getStylesheets().add(getClass().getResource("/com/example/demo/dark-theme.css").toExternalForm());
        }
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