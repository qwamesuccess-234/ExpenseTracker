package com.example.demo.repo;

import com.example.demo.model.Expense;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseRepo {

    private final Connection connection;

    public ExpenseRepo() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    public boolean save(Expense expense) {
        String query = "INSERT INTO expenses (user_id, category_id, amount, description, expense_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, expense.getUserId());
            ps.setInt(2, expense.getCategoryId());
            ps.setDouble(3, expense.getAmount());
            ps.setString(4, expense.getDescription());
            ps.setDate(5, Date.valueOf(expense.getDate()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Expense expense) {
        String query = "UPDATE expenses SET category_id=?, amount=?, description=?, expense_date=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, expense.getCategoryId());
            ps.setDouble(2, expense.getAmount());
            ps.setString(3, expense.getDescription());
            ps.setDate(4, Date.valueOf(expense.getDate()));
            ps.setInt(5, expense.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int expenseId) {
        String query = "DELETE FROM expenses WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, expenseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Expense> findByUserId(int userId) {
        List<Expense> expenses = new ArrayList<>();
        String query = """
            SELECT e.id, e.user_id, e.category_id, c.name AS category_name,
                   e.amount, e.description, e.expense_date
            FROM expenses e
            LEFT JOIN categories c ON e.category_id = c.id
            WHERE e.user_id = ?
            ORDER BY e.expense_date DESC
            """;
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                expenses.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    public double getTotalSpentThisMonth(int userId) {
        String query = """
            SELECT COALESCE(SUM(amount), 0) AS total FROM expenses
            WHERE user_id = ? AND MONTH(expense_date) = MONTH(CURDATE())
            AND YEAR(expense_date) = YEAR(CURDATE())
            """;
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private Expense mapRow(ResultSet rs) throws SQLException {
        Expense e = new Expense();
        e.setId(rs.getInt("id"));
        e.setUserId(rs.getInt("user_id"));
        e.setCategoryId(rs.getInt("category_id"));
        e.setCategoryName(rs.getString("category_name"));
        e.setAmount(rs.getDouble("amount"));
        e.setDescription(rs.getString("description"));
        LocalDate date = rs.getDate("expense_date").toLocalDate();
        e.setDate(date);
        return e;
    }
}