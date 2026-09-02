package com.sunrise.sunrisedentalpms.dao;

import com.sunrise.sunrisedentalpms.model.Patient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientDAO implements PatientDAOInterface {

    // Creates a new patient record
    @Override
    public Patient createPatient(String name, String address, String contactNumber) {
        String sql = "INSERT INTO patient (name, address, contact_number) VALUES (?, ?, ?)";

        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setString(3, contactNumber);

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    String generatedId = String.valueOf(keys.getInt(1));
                    return new Patient(generatedId, name, address, contactNumber);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error creating patient record");
            e.printStackTrace();
        }

        return null;
    }

    // Finds a patient by ID
    @Override
    public Optional<Patient> findById(String patientId) {
        int id;
        try {
            id = Integer.parseInt(patientId);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }

        String sql = "SELECT patient_id, name, address, contact_number FROM patient WHERE patient_id = ?";

        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding patient by ID");
            e.printStackTrace();
        }

        return Optional.empty();
    }

    // Finds a patient by contact number
    @Override
    public Optional<Patient> findByContactNumber(String contactNumber) {
        String sql = "SELECT patient_id, name, address, contact_number FROM patient WHERE contact_number = ?";

        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, contactNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding patient by contact number");
            e.printStackTrace();
        }

        return Optional.empty();
    }

    // Retrieves all patients
    @Override
    public List<Patient> findAll() {
        String sql = "SELECT patient_id, name, address, contact_number FROM patient ORDER BY name";

        List<Patient> patients = new ArrayList<>();
        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    patients.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving patient list");
            e.printStackTrace();
        }

        return patients;
    }

    // Finds an existing patient or creates one
    @Override
    public Patient findOrCreate(String name, String address, String contactNumber) {
        return findByContactNumber(contactNumber)
                .orElseGet(() -> createPatient(name, address, contactNumber));
    }

    // Builds a Patient object from the current row of a ResultSet
    private Patient mapRow(ResultSet rs) throws SQLException {
        return new Patient(
                String.valueOf(rs.getInt("patient_id")),
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("contact_number")
        );
    }
}