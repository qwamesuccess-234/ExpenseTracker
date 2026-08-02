package com.example.demo.controller;

import com.example.demo.model.Expense;
import com.example.demo.repo.ExpenseRepo;
import com.example.demo.util.AlertUtil;
import com.example.demo.util.SceneManager;
import com.example.demo.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class ApprovalsController {

    @FXML private TableView<Expense> pendingTable;
    @FXML private TableColumn<Expense, String> dateColumn;
    @FXML private TableColumn<Expense, String> descriptionColumn;
    @FXML private TableColumn<Expense, String> departmentColumn;
    @FXML private TableColumn<Expense, Double> amountColumn;
    @FXML private TableColumn<Expense, Void> actionColumn;

    private final ExpenseRepo expenseRepo = new ExpenseRepo();

    @FXML
    public void initialize() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        addActionButtons();
        loadPending();
    }

    private void loadPending() {
        // Resolves to the current user's own id if they're an owner, or to their
        // organization owner's id if they somehow land on this screen as a team
        // member — keeps this screen correct even if nav guarding changes later.
        int ownerId = SessionManager.getOrganizationOwnerId();
        pendingTable.setItems(FXCollections.observableArrayList(expenseRepo.findPendingApprovals(ownerId)));
    }

    private void addActionButtons() {
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final HBox box = new HBox(8, approveBtn, rejectBtn);

            {
                approveBtn.setStyle("-fx-background-color: #21B373; -fx-text-fill: white; -fx-background-radius: 6;");
                rejectBtn.setStyle("-fx-background-color: #E64D4D; -fx-text-fill: white; -fx-background-radius: 6;");

                approveBtn.setOnAction(e -> {
                    Expense expense = getTableView().getItems().get(getIndex());
                    expenseRepo.updateApprovalStatus(expense.getId(), "Approved");
                    loadPending();
                });

                rejectBtn.setOnAction(e -> {
                    Expense expense = getTableView().getItems().get(getIndex());
                    boolean confirmed = AlertUtil.confirm("Reject Expense", "Reject this expense submission?");
                    if (confirmed) {
                        expenseRepo.updateApprovalStatus(expense.getId(), "Rejected");
                        loadPending();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    @FXML
    private void goToDashboard() {
        SceneManager.switchScene("dashboard.fxml");
    }
}