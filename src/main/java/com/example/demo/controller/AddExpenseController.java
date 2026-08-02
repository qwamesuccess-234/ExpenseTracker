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
    @FXML private Label departmentLabel;
    @FXML private TextField departmentField;

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
        datePicker.setValue(LocalDate.now());

        boolean isBusiness = SessionManager.isBusinessAccount();
        departmentLabel.setVisible(isBusiness);
        departmentLabel.setManaged(isBusiness);
        departmentField.setVisible(isBusiness);
        departmentField.setManaged(isBusiness);//
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
        String department = departmentField.getText();

        // Team members' expenses need owner approval; owner's own expenses auto-approve
        boolean isTeamMember = SessionManager.getCurrentUser().getOrganizationId() != null;
        String status = isTeamMember ? "Pending" : "Approved";

        boolean saved = expenseService.addExpense(userId, selectedCategory.getId(), amount, description, date, department, status, selectedReceiptPath);

        if (saved) {
            String msg = isTeamMember ? "Expense submitted for approval" : "Expense added successfully";
            ToastUtil.showSuccess(rootPane, msg);
            SceneManager.switchScene("dashboard.fxml");
        }
    }

    @FXML
    private void handleCancel() {
        SceneManager.switchScene("dashboard.fxml");
    }
}