package com.example.demo.controller;

import com.example.demo.model.Category;
import com.example.demo.service.ExpenseService;
import com.example.demo.repo.CategoryRepo;
import com.example.demo.util.SceneManager;
import com.example.demo.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.List;

public class AddExpenseController {

    @FXML private TextField amountField;
    @FXML private TextField descriptionField;
    @FXML private ComboBox<Category> categoryCombo;
    @FXML private DatePicker datePicker;
    @FXML private Label errorLabel;

    private final ExpenseService expenseService = new ExpenseService();
    private final CategoryRepo categoryRepo = new CategoryRepo();

    @FXML
    public void initialize() {
        List<Category> categories = categoryRepo.findAll();
        categoryCombo.setItems(FXCollections.observableArrayList(categories));
        datePicker.setValue(LocalDate.now()); // sensible default
    }

    @FXML
    private void handleSave() {
        String amountText = amountField.getText();
        String description = descriptionField.getText();
        Category selectedCategory = categoryCombo.getValue();
        LocalDate date = datePicker.getValue();

        if (amountText.isEmpty() || description.isEmpty() || selectedCategory == null || date == null) {
            errorLabel.setText("All fields are required");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            errorLabel.setText("Amount must be a valid number");
            return;
        }

        int userId = SessionManager.getCurrentUser().getId();
        boolean saved = expenseService.addExpense(userId, selectedCategory.getId(), amount, description, date);

        if (saved) {
            SceneManager.switchScene("dashboard.fxml");
        } else {
            errorLabel.setText("Could not save expense. Amount must be greater than 0.");
        }
    }

    @FXML
    private void handleCancel() {
        SceneManager.switchScene("dashboard.fxml");
    }
}