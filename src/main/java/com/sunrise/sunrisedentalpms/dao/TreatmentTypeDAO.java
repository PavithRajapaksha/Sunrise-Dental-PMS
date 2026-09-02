package com.sunrise.sunrisedentalpms.dao;

import com.sunrise.sunrisedentalpms.model.TreatmentType;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TreatmentTypeDAO implements TreatmentTypeDAOInterface {

    // Creates a new treatment type
    @Override
    public TreatmentType createTreatmentType(String name, BigDecimal consultationFee) {
        try {
            new TreatmentType("VALIDATION_ONLY", name, consultationFee);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid treatment type data: " + e.getMessage());
            return null;
        }

        String sql = "INSERT INTO treatment_type (name, consultation_fee) VALUES (?, ?)";

        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.setBigDecimal(2, consultationFee);

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    String generatedId = String.valueOf(keys.getInt(1));
                    return new TreatmentType(generatedId, name, consultationFee);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error creating treatment type record");
            e.printStackTrace();
        }

        return null;
    }

    // Finds a treatment type by ID
    @Override
    public Optional<TreatmentType> findById(String treatmentTypeId) {
        int id;
        try {
            id = Integer.parseInt(treatmentTypeId);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }

        String sql = "SELECT treatment_type_id, name, consultation_fee FROM treatment_type WHERE treatment_type_id = ?";

        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding treatment type by ID");
            e.printStackTrace();
        }

        return Optional.empty();
    }

    // Retrieves all treatment types
    @Override
    public List<TreatmentType> findAll() {
        String sql = "SELECT treatment_type_id, name, consultation_fee FROM treatment_type ORDER BY name";

        List<TreatmentType> treatmentTypes = new ArrayList<>();
        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    treatmentTypes.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving treatment type list");
            e.printStackTrace();
        }

        return treatmentTypes;
    }

    // Updates a treatment type's consultation fee
    @Override
    public boolean updateConsultationFee(String treatmentTypeId, BigDecimal newFee) {
        int id;
        try {
            id = Integer.parseInt(treatmentTypeId);
        } catch (NumberFormatException e) {
            return false;
        }

        if (newFee == null || newFee.signum() < 0) {
            return false;
        }

        String sql = "UPDATE treatment_type SET consultation_fee = ? WHERE treatment_type_id = ?";

        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, newFee);
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating treatment type fee");
            e.printStackTrace();
            return false;
        }
    }

    // Builds a TreatmentType object from a database row
    private TreatmentType mapRow(ResultSet rs) throws SQLException {
        return new TreatmentType(
                String.valueOf(rs.getInt("treatment_type_id")),
                rs.getString("name"),
                rs.getBigDecimal("consultation_fee")
        );
    }
}