package com.example.demo.controller;

import com.example.demo.model.Budget;
import com.example.demo.service.BudgetService;
import com.example.demo.util.SceneManager;
import com.example.demo.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

public class BudgetController {

    @FXML private ComboBox<String> periodCombo;
    @FXML private TextField limitField;
    @FXML private ProgressBar budgetProgressBar;
    @FXML private Label progressLabel;
    @FXML private Label remainingLabel;

    private final BudgetService budgetService = new BudgetService();

    @FXML
    public void initialize() {
        periodCombo.setItems(FXCollections.observableArrayList("Monthly", "Weekly"));
        loadBudgetData();
    }

    private void loadBudgetData() {
        int userId = SessionManager.getCurrentUser().getId();
        Budget budget = budgetService.getBudget(userId);
        double spent = budgetService.getSpentThisMonth(userId);

        if (budget != null) {
            periodCombo.setValue(budget.getPeriod());
            limitField.setText(String.valueOf(budget.getLimitAmount()));

            double progress = budget.getLimitAmount() > 0 ? spent / budget.getLimitAmount() : 0;
            budgetProgressBar.setProgress(Math.min(progress, 1.0));

            double remaining = budget.getLimitAmount() - spent;
            progressLabel.setText(String.format("$%.2f spent of $%.2f budget (%.0f%%)",
                    spent, budget.getLimitAmount(), progress * 100));
            remainingLabel.setText(String.format("$%.2f remaining", remaining));
            remainingLabel.setStyle(remaining >= 0
                    ? "-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #21B373;"
                    : "-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #E64D4D;");
        } else {
            progressLabel.setText("No budget set yet");
            remainingLabel.setText("");
        }
    }

    @FXML
    private void handleSaveBudget() {
        try {
            double limit = Double.parseDouble(limitField.getText());
            String period = periodCombo.getValue();
            int userId = SessionManager.getCurrentUser().getId();

            budgetService.setBudget(userId, period, limit);
            loadBudgetData(); // refresh progress bar immediately
        } catch (NumberFormatException e) {
            progressLabel.setText("Enter a valid number for budget limit");
        }
    }
    @FXML private void goToDashboard() {
        SceneManager.switchScene("dashboard.fxml");
    }
    @FXML private void goToExpenseList() {
        SceneManager.switchScene("expense_list.fxml");
    }
    @FXML private void goToReports() {
        SceneManager.switchScene("report.fxml");
    }
    @FXML private void goToCategories() {
        SceneManager.switchScene("categories.fxml");
    }
}