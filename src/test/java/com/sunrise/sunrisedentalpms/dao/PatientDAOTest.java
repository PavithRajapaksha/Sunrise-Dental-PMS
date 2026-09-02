package com.sunrise.sunrisedentalpms.dao;

import com.sunrise.sunrisedentalpms.model.Patient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PatientDAOTest {

    private static final String TEST_CONTACT_NUMBER = "0770000111";
    private static final String TEST_NAME = "Test Patient";
    private static final String TEST_ADDRESS = "123 Test Lane, Colombo";

    private PatientDAOInterface patientDao;

    @BeforeEach
    void setUp() {
        patientDao = new PatientDAO();
        deleteTestPatient();
    }

    @AfterEach
    void tearDown() {
        deleteTestPatient();
    }

    @Test
    void createPatient_ShouldInsertPatientRecord() {
        Patient created = patientDao.createPatient(TEST_NAME, TEST_ADDRESS, TEST_CONTACT_NUMBER);

        assertNotNull(created);
        assertEquals(TEST_NAME, created.getName());
        assertEquals(TEST_ADDRESS, created.getAddress());
        assertEquals(TEST_CONTACT_NUMBER, created.getContactNumber());
    }

    @Test
    void findById_ShouldReturnCreatedPatient() {
        Patient created = patientDao.createPatient(TEST_NAME, TEST_ADDRESS, TEST_CONTACT_NUMBER);

        Optional<Patient> found = patientDao.findById(created.getPatientId());

        assertTrue(found.isPresent());
        assertEquals(created.getPatientId(), found.get().getPatientId());
    }

    @Test
    void findByContactNumber_ShouldReturnCreatedPatient() {
        patientDao.createPatient(TEST_NAME, TEST_ADDRESS, TEST_CONTACT_NUMBER);

        Optional<Patient> found = patientDao.findByContactNumber(TEST_CONTACT_NUMBER);

        assertTrue(found.isPresent());
        assertEquals(TEST_NAME, found.get().getName());
    }

    @Test
    void findAll_ShouldIncludeCreatedPatient() {
        patientDao.createPatient(TEST_NAME, TEST_ADDRESS, TEST_CONTACT_NUMBER);

        List<Patient> allPatients = patientDao.findAll();

        assertTrue(allPatients.stream()
                .anyMatch(p -> p.getContactNumber().equals(TEST_CONTACT_NUMBER)));
    }

    @Test
    void findOrCreate_ExistingPatient_ReturnsSameRecord() {
        Patient original = patientDao.createPatient(TEST_NAME, TEST_ADDRESS, TEST_CONTACT_NUMBER);

        Patient result = patientDao.findOrCreate("Different Name", "Different Address", TEST_CONTACT_NUMBER);

        assertEquals(original.getPatientId(), result.getPatientId());
        assertEquals(TEST_NAME, result.getName()); // unchanged, since existing record was reused
    }

    @Test
    void findOrCreate_NewPatient_CreatesRecord() {
        Patient result = patientDao.findOrCreate(TEST_NAME, TEST_ADDRESS, TEST_CONTACT_NUMBER);

        assertNotNull(result);
        assertEquals(TEST_CONTACT_NUMBER, result.getContactNumber());
    }

    // Removes the test patient directly via SQL, keeping tests repeatable
    private void deleteTestPatient() {
        String sql = "DELETE FROM patient WHERE contact_number = ?";

        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, TEST_CONTACT_NUMBER);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up test data", e);
        }
    }
}