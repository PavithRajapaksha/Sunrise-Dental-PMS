package com.sunrise.sunrisedentalpms.dao;

import com.sunrise.sunrisedentalpms.model.Dentist;
import com.sunrise.sunrisedentalpms.model.DentistStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DentistDAOTest {

    private static final String TEST_CONTACT_NUMBER = "0770000222";
    private static final String TEST_NAME = "Test Dentist";

    private DentistDAOInterface dentistDao;

    @BeforeEach
    void setUp() {
        dentistDao = new DentistDAO();
        deleteTestDentist();
    }

    @AfterEach
    void tearDown() {
        deleteTestDentist();
    }

    @Test
    void createDentist_ShouldInsertDentistRecord() {
        Dentist created = dentistDao.createDentist(TEST_NAME, TEST_CONTACT_NUMBER);

        assertNotNull(created);
        assertEquals(TEST_NAME, created.getName());
        assertEquals(TEST_CONTACT_NUMBER, created.getContactNumber());
        assertEquals(DentistStatus.AVAILABLE, created.getStatus());
    }

    @Test
    void findById_ShouldReturnCreatedDentist() {
        Dentist created = dentistDao.createDentist(TEST_NAME, TEST_CONTACT_NUMBER);

        Optional<Dentist> found = dentistDao.findById(created.getDentistId());

        assertTrue(found.isPresent());
        assertEquals(created.getDentistId(), found.get().getDentistId());
    }

    @Test
    void findAll_ShouldIncludeCreatedDentist() {
        dentistDao.createDentist(TEST_NAME, TEST_CONTACT_NUMBER);

        List<Dentist> allDentists = dentistDao.findAll();

        assertTrue(allDentists.stream()
                .anyMatch(d -> d.getContactNumber().equals(TEST_CONTACT_NUMBER)));
    }

    @Test
    void findAllAvailable_ShouldIncludeNewlyCreatedDentist() {
        dentistDao.createDentist(TEST_NAME, TEST_CONTACT_NUMBER);

        List<Dentist> available = dentistDao.findAllAvailable();

        assertTrue(available.stream()
                .anyMatch(d -> d.getContactNumber().equals(TEST_CONTACT_NUMBER)));
    }

    @Test
    void updateStatus_ShouldChangeDentistStatus() {
        Dentist created = dentistDao.createDentist(TEST_NAME, TEST_CONTACT_NUMBER);

        boolean updated = dentistDao.updateStatus(created.getDentistId(), DentistStatus.UNAVAILABLE);

        assertTrue(updated);

        Optional<Dentist> found = dentistDao.findById(created.getDentistId());
        assertTrue(found.isPresent());
        assertEquals(DentistStatus.UNAVAILABLE, found.get().getStatus());
    }

    @Test
    void updateStatus_ShouldRemoveDentistFromAvailableList() {
        Dentist created = dentistDao.createDentist(TEST_NAME, TEST_CONTACT_NUMBER);

        dentistDao.updateStatus(created.getDentistId(), DentistStatus.UNAVAILABLE);

        List<Dentist> available = dentistDao.findAllAvailable();
        assertTrue(available.stream()
                .noneMatch(d -> d.getDentistId().equals(created.getDentistId())));
    }

    @Test
    void updateStatus_WithInvalidId_ShouldReturnFalse() {
        boolean updated = dentistDao.updateStatus("not-a-number", DentistStatus.UNAVAILABLE);

        assertFalse(updated);
    }

    // Removes the test dentist directly via SQL, keeping tests repeatable
    private void deleteTestDentist() {
        String sql = "DELETE FROM dentist WHERE contact_number = ?";

        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, TEST_CONTACT_NUMBER);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up test data", e);
        }
    }
}