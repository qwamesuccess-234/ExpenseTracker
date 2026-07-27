package com.example.demo.controller;

import com.example.demo.model.Category;
import com.example.demo.model.Expense;
import com.example.demo.repo.CategoryRepo;
import com.example.demo.service.ExpenseService;
import com.example.demo.util.SceneManager;
import com.example.demo.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class EditExpenseController {

    @FXML private TextField amountField;
    @FXML private TextField descriptionField;
    @FXML private ComboBox<Category> categoryCombo;
    @FXML private DatePicker datePicker;
    @FXML private Label errorLabel;

    private final ExpenseService expenseService = new ExpenseService();
    private final CategoryRepo categoryRepo = new CategoryRepo();
    private Expense currentExpense;

    @FXML
    public void initialize() {
        categoryCombo.setItems(FXCollections.observableArrayList(categoryRepo.findAll()));

        currentExpense = SessionManager.getSelectedExpense();
        if (currentExpense != null) {
            amountField.setText(String.valueOf(currentExpense.getAmount()));
            descriptionField.setText(currentExpense.getDescription());
            datePicker.setValue(currentExpense.getDate());

            categoryCombo.getItems().stream()
                    .filter(c -> c.getId() == currentExpense.getCategoryId())
                    .findFirst()
                    .ifPresent(categoryCombo::setValue);
        }
    }

    @FXML
    private void handleUpdate() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            currentExpense.setAmount(amount);
            currentExpense.setDescription(descriptionField.getText());
            currentExpense.setDate(datePicker.getValue());
            currentExpense.setCategoryId(categoryCombo.getValue().getId());

            boolean updated = expenseService.updateExpense(currentExpense);
            if (updated) {
                SceneManager.switchScene("expense_list.fxml");
            } else {
                errorLabel.setText("Update failed. Check amount is greater than 0.");
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Amount must be a valid number");
        }
    }

    @FXML
    private void handleDelete() {
        expenseService.deleteExpense(currentExpense.getId());
        SceneManager.switchScene("expense_list.fxml");
    }

    @FXML
    private void handleCancel() {
        SceneManager.switchScene("expense_list.fxml");
    }
}