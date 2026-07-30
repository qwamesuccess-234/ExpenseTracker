package com.example.demo.controller;

import com.example.demo.model.Category;
import com.example.demo.repo.CategoryRepo;
import com.example.demo.service.ExpenseService;
import com.example.demo.util.AlertUtil;
import com.example.demo.util.SceneManager;
import com.example.demo.util.SessionManager;
import com.example.demo.util.ToastUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Path;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

public class AddExpenseController {

    @FXML private TextField amountField;
    @FXML private TextField descriptionField;
    @FXML private ComboBox<Category> categoryCombo;
    @FXML private DatePicker datePicker;
    @FXML private Label errorLabel;
    @FXML private Label receiptLabel;
    @FXML private StackPane rootPane;

    private final ExpenseService expenseService = new ExpenseService();
    private final CategoryRepo categoryRepo = new CategoryRepo();

    private String selectedReceiptPath;

    @FXML
    private void handleAttachReceipt() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(amountField.getScene().getWindow());
        if (file == null) return;

        try {
            java.nio.file.Path receiptsDir = Paths.get(System.getProperty("user.home"), ".expensetracker", "receipts");
            Files.createDirectories(receiptsDir);
            String fileName = System.currentTimeMillis() + "_" + file.getName();
            java.nio.file.Path destination;
            destination = receiptsDir.resolve(fileName);
            Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

            selectedReceiptPath = destination.toString();
            receiptLabel.setText("Attached: " + file.getName());
        } catch (IOException e) {
            AlertUtil.showError("Upload Failed", "Could not attach receipt image.");
        }
    }

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
            ToastUtil.showSuccess(rootPane, "Expense added successfully");
            SceneManager.switchScene("dashboard.fxml");
        } else {
            ToastUtil.showError(rootPane, "Could not save expense");
        }
    }

    @FXML
    private void handleCancel() {
        SceneManager.switchScene("dashboard.fxml");
    }
}