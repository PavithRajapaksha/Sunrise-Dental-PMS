package com.sunrise.sunrisedentalpms.dao;

import com.sunrise.sunrisedentalpms.model.Appointment;
import com.sunrise.sunrisedentalpms.model.AppointmentStatus;
import com.sunrise.sunrisedentalpms.model.Dentist;
import com.sunrise.sunrisedentalpms.model.DentistStatus;
import com.sunrise.sunrisedentalpms.model.Patient;
import com.sunrise.sunrisedentalpms.model.TreatmentType;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppointmentDAO implements AppointmentDAOInterface {

    private static final String BASE_SELECT =
            "SELECT a.appointment_no, a.appointment_date, a.appointment_time, a.status, a.user_id, "
                    + "p.patient_id, p.name AS patient_name, p.address AS patient_address, p.contact_number AS patient_contact, p.email AS patient_email, "
                    + "d.dentist_id, d.name AS dentist_name, d.contact_number AS dentist_contact, d.email AS dentist_email, d.status AS dentist_status, "
                    + "t.treatment_type_id, t.name AS treatment_name, t.consultation_fee AS treatment_fee "
                    + "FROM appointment a "
                    + "JOIN patient p ON a.patient_id = p.patient_id "
                    + "JOIN dentist d ON a.dentist_id = d.dentist_id "
                    + "JOIN treatment_type t ON a.treatment_type_id = t.treatment_type_id ";

    // Creates a new appointment
    @Override
    public Appointment createAppointment(Patient patient, Dentist dentist, TreatmentType treatmentType,
                                         LocalDateTime appointmentDateTime, String bookedByUserId) {
        try {
            new Appointment.Builder("VALIDATION_ONLY")
                    .patient(patient)
                    .dentist(dentist)
                    .treatmentType(treatmentType)
                    .appointmentDateTime(appointmentDateTime)
                    .bookedByUserId(bookedByUserId)
                    .build();
        } catch (NullPointerException e) {
            System.err.println("Invalid appointment data: " + e.getMessage());
            return null;
        }

        Integer patientId = parseId(patient.getPatientId());
        Integer dentistId = parseId(dentist.getDentistId());
        Integer treatmentTypeId = parseId(treatmentType.getTreatmentTypeId());
        Integer userId = parseId(bookedByUserId);

        if (patientId == null || dentistId == null || treatmentTypeId == null || userId == null) {
            System.err.println("Invalid ID supplied while creating appointment");
            return null;
        }

        String sql = "INSERT INTO appointment (patient_id, dentist_id, treatment_type_id, user_id, "
                + "appointment_date, appointment_time, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, patientId);
            stmt.setInt(2, dentistId);
            stmt.setInt(3, treatmentTypeId);
            stmt.setInt(4, userId);
            stmt.setDate(5, Date.valueOf(appointmentDateTime.toLocalDate()));
            stmt.setTime(6, Time.valueOf(appointmentDateTime.toLocalTime()));
            stmt.setString(7, AppointmentStatus.SCHEDULED.name());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    String generatedNumber = String.valueOf(keys.getInt(1));
                    return new Appointment.Builder(generatedNumber)
                            .patient(patient)
                            .dentist(dentist)
                            .treatmentType(treatmentType)
                            .appointmentDateTime(appointmentDateTime)
                            .status(AppointmentStatus.SCHEDULED)
                            .bookedByUserId(bookedByUserId)
                            .build();
                }
            }

        } catch (SQLException e) {
            System.err.println("Error creating appointment record");
            e.printStackTrace();
        }

        return null;
    }

    // Find appointment by number
    @Override
    public Optional<Appointment> findByAppointmentNumber(String appointmentNumber) {
        Integer id = parseId(appointmentNumber);
        if (id == null) {
            return Optional.empty();
        }

        String sql = BASE_SELECT + "WHERE a.appointment_no = ?";

        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding appointment by number");
            e.printStackTrace();
        }

        return Optional.empty();
    }

    // Finds appointments for a patient
    @Override
    public List<Appointment> findByPatientId(String patientId) {
        List<Appointment> appointments = new ArrayList<>();

        Integer id = parseId(patientId);
        if (id == null) {
            return appointments;
        }

        String sql = BASE_SELECT + "WHERE a.patient_id = ? ORDER BY a.appointment_date, a.appointment_time";

        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving appointments for patient");
            e.printStackTrace();
        }

        return appointments;
    }

    // get all appointments
    @Override
    public List<Appointment> findAll() {
        String sql = BASE_SELECT + "ORDER BY a.appointment_date, a.appointment_time";

        List<Appointment> appointments = new ArrayList<>();
        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving all appointments");
            e.printStackTrace();
        }

        return appointments;
    }

    // Update appointment status
    @Override
    public boolean updateStatus(String appointmentNumber, AppointmentStatus newStatus) {
        Integer id = parseId(appointmentNumber);
        if (id == null || newStatus == null) {
            return false;
        }

        String sql = "UPDATE appointment SET status = ? WHERE appointment_no = ?";

        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus.name());
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating appointment status");
            e.printStackTrace();
            return false;
        }
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        Patient patient = new Patient(
                String.valueOf(rs.getInt("patient_id")),
                rs.getString("patient_name"),
                rs.getString("patient_address"),
                rs.getString("patient_contact")
        );
        patient.setEmail(rs.getString("patient_email"));

        Dentist dentist = new Dentist(
                String.valueOf(rs.getInt("dentist_id")),
                rs.getString("dentist_name"),
                rs.getString("dentist_contact")
        );
        dentist.setEmail(rs.getString("dentist_email"));
        dentist.setStatus(DentistStatus.valueOf(rs.getString("dentist_status")));

        TreatmentType treatmentType = new TreatmentType(
                String.valueOf(rs.getInt("treatment_type_id")),
                rs.getString("treatment_name"),
                rs.getBigDecimal("treatment_fee")
        );

        LocalDateTime appointmentDateTime = LocalDateTime.of(
                rs.getDate("appointment_date").toLocalDate(),
                rs.getTime("appointment_time").toLocalTime()
        );

        return new Appointment.Builder(String.valueOf(rs.getInt("appointment_no")))
                .patient(patient)
                .dentist(dentist)
                .treatmentType(treatmentType)
                .appointmentDateTime(appointmentDateTime)
                .status(AppointmentStatus.valueOf(rs.getString("status")))
                .bookedByUserId(String.valueOf(rs.getInt("user_id")))
                .build();
    }


    private Integer parseId(String id) {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }
}