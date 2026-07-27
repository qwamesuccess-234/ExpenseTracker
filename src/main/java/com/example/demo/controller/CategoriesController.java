package com.example.demo.controller;

import com.example.demo.model.Category;
import com.example.demo.repo.CategoryRepo;
import com.example.demo.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class CategoriesController {

    @FXML private TextField nameField;
    @FXML private TextField typeField;
    @FXML private Label errorLabel;
    @FXML private TableView<Category> categoryTable;
    @FXML private TableColumn<Category, String> nameColumn;
    @FXML private TableColumn<Category, String> typeColumn;
    @FXML private TableColumn<Category, Void> actionColumn;

    private final CategoryRepo categoryRepo = new CategoryRepo();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        addDeleteButton();
        loadCategories();
    }

    private void loadCategories() {
        categoryTable.setItems(FXCollections.observableArrayList(categoryRepo.findAll()));
    }

    @FXML
    private void handleAdd() {
        String name = nameField.getText();
        String type = typeField.getText();

        if (name.isEmpty()) {
            errorLabel.setText("Category name is required");
            return;
        }

        Category category = new Category();
        category.setName(name);
        category.setType(type.isEmpty() ? "Expense" : type);

        boolean saved = categoryRepo.save(category);
        if (saved) {
            nameField.clear();
            typeField.clear();
            errorLabel.setText("");
            loadCategories();
        } else {
            errorLabel.setText("Could not save category");
        }
    }

    private void addDeleteButton() {
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");
            private final HBox box = new HBox(deleteBtn);

            {
                deleteBtn.setStyle("-fx-background-color: #E64D4D; -fx-text-fill: white; -fx-background-radius: 6;");
                deleteBtn.setOnAction(e -> {
                    Category category = getTableView().getItems().get(getIndex());
                    categoryRepo.delete(category.getId());
                    loadCategories();
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
    private void goToExpenseList() {
        SceneManager.switchScene("expense_list.fxml");
    }
}