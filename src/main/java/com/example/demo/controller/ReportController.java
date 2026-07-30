package com.example.demo.controller;

import com.example.demo.model.Expense;
import com.example.demo.service.ReportService;
import com.example.demo.util.AlertUtil;
import com.example.demo.util.SceneManager;
import com.example.demo.util.SessionManager;
import com.example.demo.util.ToastUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ReportController {

    @FXML private PieChart categoryPieChart;
    @FXML private BarChart<String, Number> trendBarChart;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;

    private StackPane rootPane;

    private final ReportService reportService = new ReportService();

    @FXML
    public void initialize() {
        toDatePicker.setValue(LocalDate.now());
        fromDatePicker.setValue(LocalDate.now().minusMonths(1));

        loadCharts();
    }

    private void loadCharts() {
        int userId = SessionManager.getCurrentUser().getId();

        // Pie chart: spending by category
        Map<String, Double> byCategory = reportService.getSpendingByCategory(userId);
        categoryPieChart.setData(FXCollections.observableArrayList(
                byCategory.entrySet().stream()
                        .map(e -> new PieChart.Data(e.getKey(), e.getValue()))
                        .toList()
        ));

        // Bar chart: monthly trend
        Map<String, Double> byMonth = reportService.getMonthlyTrend(userId);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Spent");
        byMonth.forEach((month, total) -> series.getData().add(new XYChart.Data<>(month, total)));

        trendBarChart.getData().clear();
        trendBarChart.getData().add(series);
    }

    @FXML
    private void handleApplyFilter() {
        // Currently pie/bar charts show all-time / last-6-months data.
        // This filters the underlying expense list for CSV export.
        loadCharts();
    }

    @FXML
    private void handleExportCsv() {
        int userId = SessionManager.getCurrentUser().getId();
        LocalDate from = fromDatePicker.getValue();
        LocalDate to = toDatePicker.getValue();

        List<Expense> expenses = reportService.getExpensesInRange(userId, from, to);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("expense_report.csv");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File file = fileChooser.showSaveDialog(categoryPieChart.getScene().getWindow());

        if (file == null) return; // user cancelled

        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Date,Description,Category,Amount\n");
            for (Expense e : expenses) {
                writer.write(String.format("%s,%s,%s,%.2f%n",
                        e.getDate(),
                        e.getDescription().replace(",", ";"), // avoid breaking CSV columns
                        e.getCategoryName(),
                        e.getAmount()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleExportPdf() {
        int userId = SessionManager.getCurrentUser().getId();
        LocalDate from = fromDatePicker.getValue();
        LocalDate to = toDatePicker.getValue();
        List<Expense> expenses = reportService.getExpensesInRange(userId, from, to);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("expense_report.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(categoryPieChart.getScene().getWindow());
        if (file == null) return;

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(doc, page)) {
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
                content.beginText();
                content.newLineAtOffset(50, 750);
                content.showText("ExpenseTracker Report");
                content.endText();

                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
                float y = 710;
                content.beginText();
                content.newLineAtOffset(50, y);
                content.showText("Date         Description                 Category         Amount");
                content.endText();

                for (Expense e : expenses) {
                    y -= 18;
                    if (y < 50) break; // simple single-page cutoff; paginate later if needed
                    content.beginText();
                    content.newLineAtOffset(50, y);
                    content.showText(String.format("%s   %-25s %-15s $%.2f",
                            e.getDate(), truncate(e.getDescription(), 24), e.getCategoryName(), e.getAmount()));
                    content.endText();
                }
            }
            doc.save(file);
            ToastUtil.showSuccess(rootPane, "PDF exported successfully");
        } catch (IOException e) {
            AlertUtil.showError("Export Failed", "Could not create PDF file.");
        }
    }

    private String truncate(String text, int max) {
        return text.length() > max ? text.substring(0, max - 1) + "…" : text;
    }

    @FXML
    private void goToDashboard() {

        SceneManager.switchScene("dashboard.fxml");
    }

    @FXML
    private void goToExpenseList() {

        SceneManager.switchScene("expense_list.fxml");
    }

    @FXML
    private void goToBudget() {

        SceneManager.switchScene("budget.fxml");
    }

    @FXML
    private void goToCategories() {
        SceneManager.switchScene("categories.fxml");
    }

}