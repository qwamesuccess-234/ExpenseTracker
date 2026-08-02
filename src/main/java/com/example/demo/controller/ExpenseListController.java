package com.example.demo.controller;

import com.example.demo.model.Category;
import com.example.demo.model.Expense;
import com.example.demo.model.User;
import com.example.demo.repo.CategoryRepo;
import com.example.demo.service.ExpenseService;
import com.example.demo.util.AlertUtil;
import com.example.demo.util.SceneManager;
import com.example.demo.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.scene.control.ComboBox;

import java.io.*;
import java.time.LocalDate;
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
    @FXML private ComboBox<String> exportPeriodCombo;

    private final ExpenseService expenseService = new ExpenseService();
    private final CategoryRepo categoryRepo = new CategoryRepo();
    private ObservableList<Expense> allExpenses;


    @FXML
    public void initialize() {
        exportPeriodCombo.setItems(FXCollections.observableArrayList("Daily", "Weekly", "Monthly", "All Time"));
        exportPeriodCombo.setValue("Daily");
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

    private List<Expense> getFilteredExport(){
        LocalDate today = LocalDate.now();
        String period = exportPeriodCombo.getValue();
        return expenseTable.getItems().stream()
                .filter(e -> switch (period){
                    case "Daily" -> e.getDate().equals(today);
                    case "Weekly" -> !e.getDate().isBefore(today.minusDays(7));
                    case "Monthly" -> e.getDate().getMonth() == today.getMonth() && e.getDate().getYear() == today.getYear();
                    default -> true;
                })
                .collect(Collectors.toList());
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
                    boolean confirmed = AlertUtil.confirm("Delete Expense", "Are you sure you want to delete this expense?");
                    if (confirmed) {
                        expenseService.deleteExpense(expense.getId());
                        loadExpenses();
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

    @FXML
    private void handleExport() {
        LocalDate today = LocalDate.now();
        List<Expense> todaysExpenses = expenseTable.getItems().stream()
                .filter(e ->e.getDate().equals(today))
                .collect(Collectors.toList());

        if (todaysExpenses.isEmpty()){
            System.out.println("No expenses recorded today.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("expenses_" + today + ".csv");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File file = fileChooser.showSaveDialog(expenseTable.getScene().getWindow());

        if (file == null) return;

        User currentUser = SessionManager.getCurrentUser();

        try (FileWriter writer = new FileWriter(file)) {

            writer.write("ExpenseTracker\n");
            writer.write("EXPENSE FOR THE " + today + " - " + currentUser.getUserType().toUpperCase() + "\n");
            writer.write("Exported by: " + currentUser.getName() + "\n\n");
            writer.write("Date,Description,Category,Amount\n");
            for (Expense e : todaysExpenses) {
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
    @FXML private void goToBudget() {
        SceneManager.switchScene("budget.fxml");
    }
    @FXML private void goToReports() {
        SceneManager.switchScene("report.fxml");
    }
    @FXML private void goToCategories() {
        SceneManager.switchScene("categories.fxml");
    }
    @FXML
    private void handleImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(expenseTable.getScene().getWindow());
        if (file == null) return;

        int userId = SessionManager.getCurrentUser().getId();
        User currentUser = SessionManager.getCurrentUser();
        int imported = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean dataStarted = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (!dataStarted) {
                    if (line.equalsIgnoreCase("Date,Description,Category,Amount")) {
                        dataStarted = true;
                    }
                    continue;
                }

                String[] parts = line.split(",", -1);
                if (parts.length < 4) continue;

                try {
                    LocalDate date = LocalDate.parse(parts[0].trim());
                    String description = parts[1].trim();
                    String categoryName = parts[2].trim();
                    double amount = Double.parseDouble(parts[3].trim());

                    Category category = categoryRepo.findAll().stream()
                            .filter(c -> c.getName().equalsIgnoreCase(categoryName))
                            .findFirst()
                            .orElse(null);
                    int categoryId = category != null ? category.getId() : 0;

                    String department = currentUser.getDepartment(); // or "N/A" if User has no such field
                    // Same rule as AddExpenseController: team members' expenses need
                    // owner approval; the account owner's own expenses auto-approve.
                    boolean isTeamMember = currentUser.getOrganizationId() != null;
                    String status = isTeamMember ? "Pending" : "Approved";

                    expenseService.addExpense(userId, categoryId, amount, description, date, department, status);
                    imported++;
                } catch (Exception ex) {
                    // skip malformed row
                }
            }
            loadExpenses();
            System.out.println("Imported " + imported + " expenses.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}