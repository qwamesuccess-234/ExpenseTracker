package com.example.demo.service;

import com.example.demo.model.Budget;
import com.example.demo.repo.BudgetRepo;
import com.example.demo.repo.ExpenseRepo;

public class BudgetService {

    private final BudgetRepo budgetRepo = new BudgetRepo();
    private final ExpenseRepo expenseRepo = new ExpenseRepo();

    public boolean setBudget(int userId, String period, double limitAmount) {
        if (limitAmount <= 0) return false;
        Budget budget = new Budget();
        budget.setUserId(userId);
        budget.setPeriod(period);
        budget.setLimitAmount(limitAmount);
        return budgetRepo.save(budget);
    }

    public Budget getBudget(int userId) {
        return budgetRepo.findByUserId(userId);
    }

    public double getSpentThisMonth(int userId) {
        return expenseRepo.getTotalSpentThisMonth(userId);
    }
}