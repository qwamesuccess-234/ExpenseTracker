package com.example.demo.controller;

import com.example.demo.model.Category;
import com.example.demo.model.Expense;
import com.example.demo.repo.CategoryRepo;
import com.example.demo.service.ExpenseService;
import com.example.demo.util.SceneManager;
import com.example.demo.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ExpenseListController {

    @FXML private TextField searchField;
    @FXML private ComboBox<Category> categoryFilter;
    @FXML private TableView<Expense> expenseTable;
    @FXML private TableColumn<Expense, String> dateColumn;
    @FXML private TableColumn<Expense, String> descriptionColumn;
    @FXML private TableColumn<Expense, String> categoryColumn;
    @FXML private TableColumn<Expense, Double> amountColumn;
    @FXML private TableColumn<Expense, Void> actionsColumn;

    private final ExpenseService expenseService = new ExpenseService();
    private final CategoryRepo categoryRepo = new CategoryRepo();
    private ObservableList<Expense> allExpenses;

    @FXML
    public void initialize() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        addActionButtons();

        categoryFilter.setItems(FXCollections.observableArrayList(categoryRepo.findAll()));

        loadExpenses();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        categoryFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void loadExpenses() {
        int userId = SessionManager.getCurrentUser().getId();
        List<Expense> expenses = expenseService.getExpensesForUser(userId);
        allExpenses = FXCollections.observableArrayList(expenses);
        expenseTable.setItems(allExpenses);
    }

    private void applyFilters() {
        String search = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        Category selectedCategory = categoryFilter.getValue();

        List<Expense> filtered = allExpenses.stream()
                .filter(e -> e.getDescription().toLowerCase().contains(search))
                .filter(e -> selectedCategory == null || selectedCategory.getName().equals(e.getCategoryName()))
                .collect(Collectors.toList());

        expenseTable.setItems(FXCollections.observableArrayList(filtered));
    }

    private void addActionButtons() {
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox box = new HBox(8, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: #3868F2; -fx-text-fill: white; -fx-background-radius: 6;");
                deleteBtn.setStyle("-fx-background-color: #E64D4D; -fx-text-fill: white; -fx-background-radius: 6;");

                editBtn.setOnAction(e -> {
                    Expense expense = getTableView().getItems().get(getIndex());
                    SessionManager.setSelectedExpense(expense);
                    SceneManager.switchScene("edit_expense.fxml");
                });

                deleteBtn.setOnAction(e -> {
                    Expense expense = getTableView().getItems().get(getIndex());
                    expenseService.deleteExpense(expense.getId());
                    loadExpenses();
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

    @FXML
    private void handleExport() {
        List<Expense> expenses = expenseTable.getItems();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("expenses.csv");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File file = fileChooser.showSaveDialog(expenseTable.getScene().getWindow());

        if (file == null) return;

        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Date,Description,Category,Amount\n");
            for (Expense e : expenses) {
                writer.write(String.format("%s,%s,%s,%.2f%n",
                        e.getDate(),
                        e.getDescription().replace(",", ";"),
                        e.getCategoryName(),
                        e.getAmount()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}