package com.sunrise.sunrisedentalpms.dao;

import com.sunrise.sunrisedentalpms.model.Appointment;
import com.sunrise.sunrisedentalpms.model.AppointmentStatus;
import com.sunrise.sunrisedentalpms.model.Dentist;
import com.sunrise.sunrisedentalpms.model.Patient;
import com.sunrise.sunrisedentalpms.model.TreatmentType;
import com.sunrise.sunrisedentalpms.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentDAOTest {

    private static final String TEST_PATIENT_CONTACT = "0770000333";
    private static final String TEST_DENTIST_CONTACT = "0770000444";
    private static final String TEST_STAFF_CONTACT = "0770000555";
    private static final String TEST_TREATMENT_NAME = "Test Appointment Treatment";
    private static final String TEST_STAFF_USERNAME = "test_apt_staff";
    private static final String TEST_STAFF_PASSWORD = "TestPass123";

    private AppointmentDAOInterface appointmentDao;

    private Patient testPatient;
    private Dentist testDentist;
    private TreatmentType testTreatmentType;
    private User testStaff;

    @BeforeEach
    void setUp() {
        appointmentDao = new AppointmentDAO();

        deleteTestData();

        testPatient = new PatientDAO().createPatient("Test Patient", "123 Test Lane", TEST_PATIENT_CONTACT);
        testDentist = new DentistDAO().createDentist("Test Dentist", TEST_DENTIST_CONTACT);
        testTreatmentType = new TreatmentTypeDAO().createTreatmentType(TEST_TREATMENT_NAME, new BigDecimal("2000.00"));
        testStaff = new UserDAO().createStaff(TEST_STAFF_USERNAME, TEST_STAFF_PASSWORD, "Test Staff", TEST_STAFF_CONTACT);
    }

    @AfterEach
    void tearDown() {
        deleteTestData();
    }

    @Test
    void createAppointment_ShouldInsertAppointmentWithScheduledStatus() {
        LocalDateTime dateTime = testDateTime(1);

        Appointment created = appointmentDao.createAppointment(
                testPatient, testDentist, testTreatmentType, dateTime, testStaff.getUserId());

        assertNotNull(created);
        assertEquals(AppointmentStatus.SCHEDULED, created.getStatus());
        assertEquals(testStaff.getUserId(), created.getBookedByUserId());
    }

    @Test
    void findByAppointmentNumber_ShouldReturnCreatedAppointment() {
        Appointment created = appointmentDao.createAppointment(
                testPatient, testDentist, testTreatmentType, testDateTime(2), testStaff.getUserId());

        Optional<Appointment> found = appointmentDao.findByAppointmentNumber(created.getAppointmentNumber());

        assertTrue(found.isPresent());
        assertEquals(created.getAppointmentNumber(), found.get().getAppointmentNumber());
        assertEquals(testPatient.getPatientId(), found.get().getPatient().getPatientId());
        assertEquals(testDentist.getDentistId(), found.get().getDentist().getDentistId());
        assertEquals(testTreatmentType.getTreatmentTypeId(), found.get().getTreatmentType().getTreatmentTypeId());
    }

    @Test
    void findByPatientId_ShouldReturnAppointmentsForThatPatient() {
        appointmentDao.createAppointment(
                testPatient, testDentist, testTreatmentType, testDateTime(3), testStaff.getUserId());

        List<Appointment> results = appointmentDao.findByPatientId(testPatient.getPatientId());

        assertFalse(results.isEmpty());
        assertTrue(results.stream()
                .allMatch(a -> a.getPatient().getPatientId().equals(testPatient.getPatientId())));
    }

    @Test
    void findAll_ShouldIncludeCreatedAppointment() {
        Appointment created = appointmentDao.createAppointment(
                testPatient, testDentist, testTreatmentType, testDateTime(4), testStaff.getUserId());

        List<Appointment> all = appointmentDao.findAll();

        assertTrue(all.stream()
                .anyMatch(a -> a.getAppointmentNumber().equals(created.getAppointmentNumber())));
    }

    @Test
    void updateStatus_ShouldChangeAppointmentStatus() {
        Appointment created = appointmentDao.createAppointment(
                testPatient, testDentist, testTreatmentType, testDateTime(5), testStaff.getUserId());

        boolean updated = appointmentDao.updateStatus(created.getAppointmentNumber(), AppointmentStatus.COMPLETED);

        assertTrue(updated);

        Optional<Appointment> found = appointmentDao.findByAppointmentNumber(created.getAppointmentNumber());
        assertTrue(found.isPresent());
        assertEquals(AppointmentStatus.COMPLETED, found.get().getStatus());
    }

    @Test
    void updateStatus_WithInvalidAppointmentNumber_ShouldReturnFalse() {
        boolean updated = appointmentDao.updateStatus("not-a-number", AppointmentStatus.CANCELLED);

        assertFalse(updated);
    }

    // appointment_time has no fractional-second precision in the DB, so nanos
    // are dropped before insert to keep round-tripped values comparable
    private LocalDateTime testDateTime(int daysFromNow) {
        return LocalDateTime.now().plusDays(daysFromNow).withNano(0);
    }

    // Deletes appointment rows first (FK constraint), then the patient/dentist/
    // treatment type/staff rows they depend on, so tests stay repeatable
    private void deleteTestData() {
        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement deleteAppointments = conn.prepareStatement(
                "DELETE a FROM appointment a JOIN patient p ON a.patient_id = p.patient_id WHERE p.contact_number = ?")) {
            deleteAppointments.setString(1, TEST_PATIENT_CONTACT);
            deleteAppointments.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up test appointments", e);
        }

        try (PreparedStatement deletePatient = conn.prepareStatement("DELETE FROM patient WHERE contact_number = ?")) {
            deletePatient.setString(1, TEST_PATIENT_CONTACT);
            deletePatient.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up test patient", e);
        }

        try (PreparedStatement deleteDentist = conn.prepareStatement("DELETE FROM dentist WHERE contact_number = ?")) {
            deleteDentist.setString(1, TEST_DENTIST_CONTACT);
            deleteDentist.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up test dentist", e);
        }

        try (PreparedStatement deleteTreatmentType = conn.prepareStatement("DELETE FROM treatment_type WHERE name = ?")) {
            deleteTreatmentType.setString(1, TEST_TREATMENT_NAME);
            deleteTreatmentType.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up test treatment type", e);
        }

        try (PreparedStatement deleteStaff = conn.prepareStatement("DELETE FROM users WHERE username = ?")) {
            deleteStaff.setString(1, TEST_STAFF_USERNAME);
            deleteStaff.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up test staff", e);
        }
    }
}