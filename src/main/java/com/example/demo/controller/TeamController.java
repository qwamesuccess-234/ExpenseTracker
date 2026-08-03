package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.Invite;
import com.example.demo.repo.UserRepo;
import com.example.demo.repo.InviteRepo;
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

import java.time.LocalDateTime;
import java.util.UUID;

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
            return;
        }

        // If not added, either the user does not exist or is already assigned.
        User existing = userRepo.findByEmail(email);
        if (existing == null) {
            // create an invite record and show token for manual delivery (desktop app)
            String token = UUID.randomUUID().toString();
            Invite invite = new Invite();
            invite.setEmail(email);
            invite.setToken(token);
            invite.setInvitedBy(ownerId);
            invite.setOrganizationId(ownerId);
            invite.setCreatedAt(LocalDateTime.now());
            invite.setExpiresAt(LocalDateTime.now().plusDays(7));
            invite.setUsed(false);

            boolean created = new InviteRepo().createInvite(invite);
            if (created) {
                AlertUtil.showInfo("Invite Created", "No account found for that email. An invite token was created:\n" + token + "\nSend this token to the recipient so they can register and paste the token into their app to join your organization.");
                memberEmailField.clear();
            } else {
                AlertUtil.showError("Failed", "Could not create invite. Try again.");
            }
        } else {
            AlertUtil.showError("Failed", "No unassigned account found with that email or user already in an organization.");
        }
    }

    @FXML
    private void goToDashboard() {
        SceneManager.switchScene("dashboard.fxml");
    }
}
