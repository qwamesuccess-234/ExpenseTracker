package com.example.demo.controller;

import com.example.demo.model.Budget;
import com.example.demo.model.Expense;
import com.example.demo.model.User;
import com.example.demo.repo.UserRepo;
import com.example.demo.service.BudgetService;
import com.example.demo.service.ExpenseService;
import com.example.demo.util.SceneManager;
import com.example.demo.util.SessionManager;
import com.example.demo.util.ToastUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.InputStream;
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

    @FXML
    private BorderPane rootPane;

    private final ExpenseService expenseService = new ExpenseService();
    private final BudgetService budgetService = new BudgetService();

    @FXML private ImageView profileImageView;
    @FXML private Label sidebarNameLabel;
    @FXML private Label sidebarEmailLabel;
    @FXML private Label remainingLabel;

    @FXML
    public void initialize() {
        User user = SessionManager.getCurrentUser();
        welcomeLabel.setText("Welcome back, " + user.getName());

        sidebarNameLabel.setText(user.getName());
        sidebarEmailLabel.setText(user.getEmail());
        loadProfileImage(user.getProfilePicturePath());
        welcomeLabel.setText("Welcome back, " + SessionManager.getCurrentUser().getName());

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        applyRoleBasedVisibility();
        loadDashboardData();
    }
    private void loadProfileImage(String path) {
        try {
            if (path != null && !path.isEmpty() && new File(path).exists()) {
                profileImageView.setImage(new Image(new File(path).toURI().toString()));
            } else {
                // fallback placeholder avatar bundled in resources
                InputStream defaultAvatar = getClass().getResourceAsStream("/com/example/demo/images/default_avatar.png");
                if (defaultAvatar != null) {
                    profileImageView.setImage(new Image(defaultAvatar));
                } else {
                    System.err.println("default_avatar.png not found on classpath at /com/example/demo/images/");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        Budget budget = budgetService.getBudget(userId);
        double spent = budgetService.getSpentThisMonth(userId);

        String warning = budgetService.checkBudgetStatus(userId);
        if (warning != null) {
            ToastUtil.showError(rootPane, warning);
        }

        List<Expense> expenses = expenseService.getExpensesForUser(userId);
        ObservableList<Expense> data = FXCollections.observableArrayList(expenses);
        expenseTable.setItems(data);

        double total = expenseService.getTotalSpentThisMonth(userId);
        totalSpentLabel.setText(String.format("$%.2f", total));

        if (budget != null) {
            double remaining = budget.getLimitAmount() - spent;
            remainingLabel.setText(String.format("$%.2f", remaining));
        } else {
            remainingLabel.setText("No budget set");
        }
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