package com.example.demo.repo;

import com.example.demo.model.Budget;
import java.sql.*;

public class BudgetRepo {

    private final Connection connection;

    public BudgetRepo() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    public Budget findByUserId(int userId) {
        String query = "SELECT id, user_id, period, limit_amount FROM budgets WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Budget b = new Budget();
                b.setId(rs.getInt("id"));
                b.setUserId(rs.getInt("user_id"));
                b.setPeriod(rs.getString("period"));
                b.setLimitAmount(rs.getDouble("limit_amount"));
                return b;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean save(Budget budget) {
        // upsert: update if exists, insert if not
        Budget existing = findByUserId(budget.getUserId());
        String query = existing == null
                ? "INSERT INTO budgets (user_id, period, limit_amount) VALUES (?, ?, ?)"
                : "UPDATE budgets SET period=?, limit_amount=? WHERE user_id=?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            if (existing == null) {
                ps.setInt(1, budget.getUserId());
                ps.setString(2, budget.getPeriod());
                ps.setDouble(3, budget.getLimitAmount());
            } else {
                ps.setString(1, budget.getPeriod());
                ps.setDouble(2, budget.getLimitAmount());
                ps.setInt(3, budget.getUserId());
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}