package com.sunrise.sunrisedentalpms.dao;

import com.sunrise.sunrisedentalpms.model.Dentist;
import com.sunrise.sunrisedentalpms.model.DentistStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DentistDAO implements DentistDAOInterface {

    // Register new dentist
    @Override
    public Dentist createDentist(String name, String contactNumber, String email) {
        Dentist candidate;
        try {
            candidate = new Dentist("VALIDATION_ONLY", name, contactNumber);
            candidate.setEmail(email);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid dentist data: " + e.getMessage());
            return null;
        }

        String sql = "INSERT INTO dentist (name, contact_number, email, status) VALUES (?, ?, ?, ?)";

        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.setString(2, contactNumber);
            stmt.setString(3, email);
            stmt.setString(4, candidate.getStatus().name());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    String generatedId = String.valueOf(keys.getInt(1));
                    Dentist created = new Dentist(generatedId, name, contactNumber);
                    created.setEmail(email);
                    return created;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error creating dentist record");
            e.printStackTrace();
        }

        return null;
    }

    // Finds a dentist by ID
    @Override
    public Optional<Dentist> findById(String dentistId) {
        int id;
        try {
            id = Integer.parseInt(dentistId);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }

        String sql = "SELECT dentist_id, name, contact_number, email, status FROM dentist WHERE dentist_id = ?";

        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding dentist by ID");
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Dentist> findAll() {
        String sql = "SELECT dentist_id, name, contact_number, email, status FROM dentist ORDER BY name";

        List<Dentist> dentists = new ArrayList<>();
        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dentists.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving dentist list");
            e.printStackTrace();
        }

        return dentists;
    }

    // get all available dentists
    @Override
    public List<Dentist> findAllAvailable() {
        String sql = "SELECT dentist_id, name, contact_number, email, status FROM dentist WHERE status = ? ORDER BY name";

        List<Dentist> dentists = new ArrayList<>();
        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, DentistStatus.AVAILABLE.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dentists.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving available dentist list");
            e.printStackTrace();
        }

        return dentists;
    }

    // Update dentist status
    @Override
    public boolean updateStatus(String dentistId, DentistStatus newStatus) {
        int id;
        try {
            id = Integer.parseInt(dentistId);
        } catch (NumberFormatException e) {
            return false;
        }

        String sql = "UPDATE dentist SET status = ? WHERE dentist_id = ?";

        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus.name());
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating dentist status");
            e.printStackTrace();
            return false;
        }
    }

    private Dentist mapRow(ResultSet rs) throws SQLException {
        Dentist dentist = new Dentist(
                String.valueOf(rs.getInt("dentist_id")),
                rs.getString("name"),
                rs.getString("contact_number")
        );
        dentist.setEmail(rs.getString("email"));
        dentist.setStatus(DentistStatus.valueOf(rs.getString("status")));
        return dentist;
    }
}