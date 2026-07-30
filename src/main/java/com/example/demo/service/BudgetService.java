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

    public String checkBudgetStatus(int userId) {
        Budget budget = budgetRepo.findByUserId(userId);
        if (budget == null) return null;

        double spent = expenseRepo.getTotalSpentThisMonth(userId);
        double limit = budget.getLimitAmount();
        if (limit <= 0) return null;

        double percentUsed = (spent / limit) * 100;

        if (spent > limit) {
            return String.format("You've exceeded your budget! Spent $%.2f of $%.2f limit.", spent, limit);
        } else if (percentUsed >= 90) {
            return String.format("Warning: you've used %.0f%% of your budget ($%.2f of $%.2f).", percentUsed, spent, limit);
        }

        return null;
    }
}