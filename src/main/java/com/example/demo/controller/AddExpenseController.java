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
import javafx.scene.control.CheckBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import com.example.demo.model.Category;

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
    @FXML private CheckBox companyCheckbox;

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
        departmentField.setManaged(isBusiness); //

        // Show company checkbox only if the app supports business/team flows
        companyCheckbox.setVisible(true);
        companyCheckbox.setManaged(true);
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

        boolean companyExpense = companyCheckbox != null && companyCheckbox.isSelected();
        Integer organizationId = null;
        String status;

        if (companyExpense) {
            // If user is the business owner (business account) allow company expense and set org to owner's id
            if (SessionManager.isBusinessAccount()) {
                organizationId = SessionManager.getCurrentUser().getId();
                status = "Approved"; // owner's own company expenses auto-approved
            } else if (SessionManager.getCurrentUser().getOrganizationId() != null) {
                // regular team member creating a company expense -> needs approval
                organizationId = SessionManager.getCurrentUser().getOrganizationId();
                status = "Pending";
            } else {
                errorLabel.setText("You are not part of an organization to add a company expense.");
                return;
            }
        } else {
            // Personal expense: always allowed and auto-approved
            organizationId = null;
            status = "Approved";
        }

        boolean saved = expenseService.addExpense(userId, selectedCategory.getId(), amount, description, date, department, status, selectedReceiptPath, organizationId);

        if (saved) {
            ToastUtil.show("Expense saved");
            SceneManager.goBack();
        } else {
            errorLabel.setText("Could not save expense. Try again.");
        }
    }

    @FXML
    private void handleCancel() {
        SceneManager.goBack();
    }
}
