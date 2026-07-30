package com.example.demo.controller;

import com.example.demo.model.Expense;
import com.example.demo.model.User;
import com.example.demo.repo.UserRepo;
import com.example.demo.service.ExpenseService;
import com.example.demo.util.SceneManager;
import com.example.demo.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.List;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label totalSpentLabel;

    @FXML private VBox corporateStatsBox;
    @FXML private VBox enterpriseStatsBox;
    @FXML private Label companyNameLabel;
    @FXML private Label teamActivityLabel;

    @FXML private TableView<Expense> expenseTable;
    @FXML private TableColumn<Expense, String> dateColumn;
    @FXML private TableColumn<Expense, String> descriptionColumn;
    @FXML private TableColumn<Expense, String> categoryColumn;
    @FXML private TableColumn<Expense, Double> amountColumn;

    private final ExpenseService expenseService = new ExpenseService();

    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome back, " + SessionManager.getCurrentUser().getName());

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        applyRoleBasedVisibility();
        loadDashboardData();
    }

    private void applyRoleBasedVisibility() {
        boolean isBusiness = SessionManager.isBusinessAccount();
        corporateStatsBox.setVisible(isBusiness);
        corporateStatsBox.setManaged(isBusiness);

        if (isBusiness) {
            String company = SessionManager.getCurrentUser().getCompanyName();
            companyNameLabel.setText(company != null && !company.isEmpty() ? company : "\u2014");
        }

        boolean isEnterprise = SessionManager.isEnterprise();
        enterpriseStatsBox.setVisible(isEnterprise);
        enterpriseStatsBox.setManaged(isEnterprise);

        if (isEnterprise) {
            int ownerId = SessionManager.getOrganizationOwnerId();
            List<User> team = new UserRepo().findTeamMembers(ownerId);
            teamActivityLabel.setText((team.size() + 1) + " member" + (team.size() == 0 ? "" : "s") + " on this account");
        }
    }

    private void loadDashboardData() {
        int userId = SessionManager.getCurrentUser().getId();

        List<Expense> expenses = expenseService.getExpensesForUser(userId);
        ObservableList<Expense> data = FXCollections.observableArrayList(expenses);
        expenseTable.setItems(data);

        double total = expenseService.getTotalSpentThisMonth(userId);
        totalSpentLabel.setText(String.format("$%.2f", total));
    }

    @FXML private void goToAddExpense() { SceneManager.switchScene("add_expense.fxml"); }
    @FXML private void goToExpenseList() { SceneManager.switchScene("expense_list.fxml"); }
    @FXML private void goToBudget() { SceneManager.switchScene("budget.fxml"); }
    @FXML private void goToReports() { SceneManager.switchScene("report.fxml"); }
    @FXML private void goToCategories() { SceneManager.switchScene("categories.fxml"); }
    @FXML private void goToSettings() { SceneManager.switchScene("settings.fxml"); }

    @FXML
    private void logout() {
        SessionManager.clear();
        SceneManager.switchScene("login.fxml");
    }
}