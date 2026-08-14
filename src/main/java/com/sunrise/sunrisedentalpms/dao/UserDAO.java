package com.sunrise.sunrisedentalpms.dao;

import com.sunrise.sunrisedentalpms.model.User;
import com.sunrise.sunrisedentalpms.model.UserRole;
import com.sunrise.sunrisedentalpms.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO implements UserDAOInterface {

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT user_id, username, password_hash, full_name, contact_number, role "
                + "FROM users WHERE username = ?";

        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding user by username");
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> authenticate(String username, String plainPassword) {
        return findByUsername(username)
                .filter(user -> PasswordUtil.matches(plainPassword, user.getHashedPassword()));
    }

    @Override
    public User createStaff(String username, String plainPassword, String fullName, String contactNumber) {
        String sql = "INSERT INTO users (username, password_hash, full_name, contact_number, role) "
                + "VALUES (?, ?, ?, ?, ?)";

        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String hashedPassword = PasswordUtil.hash(plainPassword);

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, fullName);
            stmt.setString(4, contactNumber);
            stmt.setString(5, UserRole.RECEPTIONIST.name());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    String generatedId = String.valueOf(keys.getInt(1));
                    return new User(generatedId, username, hashedPassword,
                            UserRole.RECEPTIONIST, fullName, contactNumber);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error creating staff account");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<User> findAllStaff() {
        String sql = "SELECT user_id, username, password_hash, full_name, contact_number, role "
                + "FROM users WHERE role = ? ORDER BY full_name";

        List<User> staffList = new ArrayList<>();
        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, UserRole.RECEPTIONIST.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    staffList.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving staff list");
            e.printStackTrace();
        }

        return staffList;
    }

    // Builds a User object from the current row of a ResultSet
    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                String.valueOf(rs.getInt("user_id")),
                rs.getString("username"),
                rs.getString("password_hash"),
                UserRole.valueOf(rs.getString("role")),
                rs.getString("full_name"),
                rs.getString("contact_number")
        );
    }
}