package com.example.demo.service;

import com.example.demo.model.Expense;
import com.example.demo.repo.ExpenseRepo;

import java.time.LocalDate;
import java.util.List;

public class ExpenseService {

    private final ExpenseRepo expenseRepo = new ExpenseRepo();

    public boolean addExpense(int userId, int categoryId, double amount, String description, LocalDate date, String department, String status) {
        return addExpense(userId, categoryId, amount, description, date, department, status, null, null);
    }

    public boolean addExpense(int userId, int categoryId, double amount, String description, LocalDate date, String department, String status, String receiptPath) {
        return addExpense(userId, categoryId, amount, description, date, department, status, receiptPath, null);
    }

    // New overload that accepts optional organizationId for company expenses
    public boolean addExpense(int userId, int categoryId, double amount, String description, LocalDate date, String department, String status, String receiptPath, Integer organizationId) {
        if (amount <= 0) return false; // basic validation belongs here, not in controller

        Expense expense = new Expense();
        expense.setUserId(userId);
        expense.setCategoryId(categoryId);
        expense.setAmount(amount);
        expense.setDescription(description);
        expense.setDate(date);
        expense.setDepartment(department);
        expense.setApprovalStatus(status);
        expense.setReceiptPath(receiptPath);
        expense.setOrganizationId(organizationId);

        return expenseRepo.save(expense);
    }

    public boolean updateExpense(Expense expense) {
        if (expense.getAmount() <= 0) return false;
        return expenseRepo.update(expense);
    }

    public boolean deleteExpense(int expenseId) {
        return expenseRepo.delete(expenseId);
    }

    public List<Expense> getExpensesForUser(int userId) {
        return expenseRepo.findByUserId(userId);
    }

    public double getTotalSpentThisMonth(int userId) {
        return expenseRepo.getTotalSpentThisMonth(userId);
    }
}
