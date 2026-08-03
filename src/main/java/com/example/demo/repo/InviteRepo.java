package com.example.demo.repo;

import com.example.demo.model.Invite;

import java.sql.*;
import java.time.LocalDateTime;

public class InviteRepo {
    private final Connection connection;

    public InviteRepo() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    public boolean createInvite(Invite invite) {
        String sql = "INSERT INTO invites (email, token, invited_by, organization_id, created_at, expires_at, used) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, invite.getEmail());
            ps.setString(2, invite.getToken());
            ps.setInt(3, invite.getInvitedBy());
            ps.setInt(4, invite.getOrganizationId());
            ps.setTimestamp(5, Timestamp.valueOf(invite.getCreatedAt()));
            ps.setTimestamp(6, Timestamp.valueOf(invite.getExpiresAt()));
            ps.setBoolean(7, invite.isUsed());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) invite.setId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Invite findByToken(String token) {
        String sql = "SELECT id, email, token, invited_by, organization_id, created_at, expires_at, used FROM invites WHERE token = ? AND used = 0 AND expires_at > NOW()";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Invite i = new Invite();
                i.setId(rs.getInt("id"));
                i.setEmail(rs.getString("email"));
                i.setToken(rs.getString("token"));
                i.setInvitedBy(rs.getInt("invited_by"));
                i.setOrganizationId(rs.getInt("organization_id"));
                Timestamp created = rs.getTimestamp("created_at");
                if (created != null) i.setCreatedAt(created.toLocalDateTime());
                Timestamp expires = rs.getTimestamp("expires_at");
                if (expires != null) i.setExpiresAt(expires.toLocalDateTime());
                i.setUsed(rs.getBoolean("used"));
                return i;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean markUsed(int inviteId) {
        String sql = "UPDATE invites SET used = 1 WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, inviteId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
