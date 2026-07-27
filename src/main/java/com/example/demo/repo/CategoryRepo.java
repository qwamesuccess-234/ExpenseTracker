package com.example.demo.repo;

import com.example.demo.model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepo {

    private final Connection connection;

    public CategoryRepo() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT id, name, type FROM categories";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.add(new Category(rs.getInt("id"), rs.getString("name"), rs.getString("type")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public boolean save(Category category) {
        String query = "INSERT INTO categories (name, type) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getType());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String query = "DELETE FROM categories WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}