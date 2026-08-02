package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repo.UserRepo;
import com.example.demo.util.AlertUtil;
import com.example.demo.util.SceneManager;
import com.example.demo.util.SessionManager;
import com.example.demo.util.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class TeamController {

    @FXML private TextField memberEmailField;
    @FXML private TableView<User> membersTable;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> typeColumn;

    private final UserRepo userRepo = new UserRepo();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("userType"));
        loadMembers();
    }

    private void loadMembers() {
        int ownerId = SessionManager.getCurrentUser().getId();
        membersTable.setItems(FXCollections.observableArrayList(userRepo.findTeamMembers(ownerId)));
    }

    @FXML
    private void handleAddMember() {
        String email = memberEmailField.getText();
        if (ValidationUtil.isEmpty(email)) return;

        int ownerId = SessionManager.getCurrentUser().getId();
        boolean added = userRepo.addTeamMember(ownerId, email);

        if (added) {
            AlertUtil.showInfo("Success", "Team member added.");
            memberEmailField.clear();
            loadMembers();
        } else {
            AlertUtil.showError("Failed", "No unassigned account found with that email.");
        }
    }

    @FXML
    private void goToDashboard() {
        SceneManager.switchScene("dashboard.fxml");
    }
}