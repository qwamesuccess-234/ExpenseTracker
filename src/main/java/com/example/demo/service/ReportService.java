package com.example.demo.service;

import com.example.demo.model.Expense;
import com.example.demo.repo.ExpenseRepo;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {

    private final ExpenseRepo expenseRepo = new ExpenseRepo();

    // Category name -> total spent, for PieChart
    public Map<String, Double> getSpendingByCategory(int userId) {
        List<Expense> expenses = expenseRepo.findByUserId(userId);
        Map<String, Double> totals = new LinkedHashMap<>();

        for (Expense e : expenses) {
            String category = e.getCategoryName() != null ? e.getCategoryName() : "Uncategorized";
            totals.merge(category, e.getAmount(), Double::sum);
        }
        return totals;
    }

    // Month label -> total spent, last 6 months, for BarChart
    public Map<String, Double> getMonthlyTrend(int userId) {
        List<Expense> expenses = expenseRepo.findByUserId(userId);
        Map<String, Double> totals = new LinkedHashMap<>();

        LocalDate now = LocalDate.now();
        // seed last 6 months with 0 so the chart always shows them in order
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = YearMonth.from(now.minusMonths(i));
            String label = ym.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            totals.put(label, 0.0);
        }

        for (Expense e : expenses) {
            YearMonth ym = YearMonth.from(e.getDate());
            if (ym.isBefore(YearMonth.from(now.minusMonths(5)))) continue; // outside 6-month window
            String label = ym.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            totals.merge(label, e.getAmount(), Double::sum);
        }
        return totals;
    }

    public List<Expense> getExpensesInRange(int userId, LocalDate from, LocalDate to) {
        return expenseRepo.findByUserId(userId).stream()
                .filter(e -> !e.getDate().isBefore(from) && !e.getDate().isAfter(to))
                .collect(Collectors.toList());
    }
}