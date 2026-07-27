package com.example.demo.repo;

import com.example.demo.model.User;
import java.sql.*;

public class UserRepo {

    private final Connection connection;

    public UserRepo() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    public boolean existByEmail(String email) {
        String query = "SELECT id FROM users WHERE email = ?";
        try (
                PreparedStatement ps = connection.prepareStatement(query))
        {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User findByEmailAndPassword(String email, String password) {
        String query = "SELECT id, name, email, phone, user_type, status FROM users WHERE email = ? AND password = ?";
        try (
                PreparedStatement ps = connection.prepareStatement(query))
        {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setUserType(rs.getString("user_type"));
                user.setStatus(rs.getBoolean("status"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean save(User user) {
        String query = "INSERT INTO users (name, email, phone, password, user_type, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (
                PreparedStatement ps = connection.prepareStatement(query))
        {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getUserType());
            ps.setBoolean(6, true); // active by default on registration
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}