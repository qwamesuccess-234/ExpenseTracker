package com.example.demo.repo;

import com.example.demo.model.User;
import com.example.demo.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public User findByEmail(String email) {
        String query = "SELECT id, name, email, phone, password, user_type, company_name, status, organization_id FROM users WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setPassword(rs.getString("password")); // this is the hash
                user.setUserType(rs.getString("user_type"));
                user.setCompanyName(rs.getString("company_name"));
                user.setStatus(rs.getBoolean("status"));
                user.setOrganizationId(rs.getObject("organization_id") != null ? rs.getInt("organization_id") : null);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updatePassword(int userId, String newHashedPassword) {
        String query = "UPDATE users SET password = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, newHashedPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<User> findTeamMembers(int organizationOwnerId) {
        List<User> members = new ArrayList<>();
        String query = "SELECT id, name, email, user_type FROM users WHERE organization_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, organizationOwnerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setName(rs.getString("name"));
                u.setEmail(rs.getString("email"));
                u.setUserType(rs.getString("user_type"));
                members.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    public boolean updateProfilePicture(int userId, String path) {
        String query = "UPDATE users SET profile_picture_path = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, path);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addTeamMember(int organizationOwnerId, String memberEmail) {
        String query = "UPDATE users SET organization_id = ? WHERE email = ? AND organization_id IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, organizationOwnerId);
            ps.setString(2, memberEmail);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean save(User user) {
        String query = "INSERT INTO users (name, email, phone, password, user_type, company_name, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, PasswordUtil.hash(user.getPassword())); // hash here, not in the controller
            ps.setString(5, user.getUserType());
            ps.setString(6, user.getCompanyName());
            ps.setBoolean(7, true);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // For a team member: get their organization owner's info (company name, etc.)
    public User findById(int id) {
        String query = "SELECT id, name, email, phone, password, user_type, company_name, status, organization_id FROM users WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setName(rs.getString("name"));
                u.setEmail(rs.getString("email"));
                u.setUserType(rs.getString("user_type"));
                u.setCompanyName(rs.getString("company_name"));
                u.setStatus(rs.getBoolean("status"));
                u.setOrganizationId(rs.getObject("organization_id") != null ? rs.getInt("organization_id") : null);
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
