package com.sunrise.sunrisedentalpms.dao;

import com.sunrise.sunrisedentalpms.model.TreatmentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TreatmentTypeDAOTest {

    private static final String TEST_NAME = "Test Treatment";
    private static final BigDecimal TEST_FEE = new BigDecimal("2500.00");

    private TreatmentTypeDAOInterface treatmentTypeDao;

    @BeforeEach
    void setUp() {
        treatmentTypeDao = new TreatmentTypeDAO();
        deleteTestTreatmentType();
    }

    @AfterEach
    void tearDown() {
        deleteTestTreatmentType();
    }

    @Test
    void createTreatmentType_ShouldInsertTreatmentTypeRecord() {
        TreatmentType created = treatmentTypeDao.createTreatmentType(TEST_NAME, TEST_FEE);

        assertNotNull(created);
        assertEquals(TEST_NAME, created.getName());
        assertEquals(0, TEST_FEE.compareTo(created.getConsultationFee()));
    }

    @Test
    void findById_ShouldReturnCreatedTreatmentType() {
        TreatmentType created = treatmentTypeDao.createTreatmentType(TEST_NAME, TEST_FEE);

        Optional<TreatmentType> found = treatmentTypeDao.findById(created.getTreatmentTypeId());

        assertTrue(found.isPresent());
        assertEquals(created.getTreatmentTypeId(), found.get().getTreatmentTypeId());
    }

    @Test
    void findAll_ShouldIncludeCreatedTreatmentType() {
        treatmentTypeDao.createTreatmentType(TEST_NAME, TEST_FEE);

        List<TreatmentType> allTreatmentTypes = treatmentTypeDao.findAll();

        assertTrue(allTreatmentTypes.stream()
                .anyMatch(t -> t.getName().equals(TEST_NAME)));
    }

    @Test
    void updateConsultationFee_ShouldChangeFee() {
        TreatmentType created = treatmentTypeDao.createTreatmentType(TEST_NAME, TEST_FEE);
        BigDecimal newFee = new BigDecimal("3000.00");

        boolean updated = treatmentTypeDao.updateConsultationFee(created.getTreatmentTypeId(), newFee);

        assertTrue(updated);

        Optional<TreatmentType> found = treatmentTypeDao.findById(created.getTreatmentTypeId());
        assertTrue(found.isPresent());
        assertEquals(0, newFee.compareTo(found.get().getConsultationFee()));
    }

    @Test
    void updateConsultationFee_WithNegativeFee_ShouldReturnFalse() {
        TreatmentType created = treatmentTypeDao.createTreatmentType(TEST_NAME, TEST_FEE);

        boolean updated = treatmentTypeDao.updateConsultationFee(created.getTreatmentTypeId(), new BigDecimal("-100"));

        assertFalse(updated);
    }

    @Test
    void updateConsultationFee_WithInvalidId_ShouldReturnFalse() {
        boolean updated = treatmentTypeDao.updateConsultationFee("not-a-number", TEST_FEE);

        assertFalse(updated);
    }

    // Removes the test treatment type directly via SQL, keeping tests repeatable
    private void deleteTestTreatmentType() {
        String sql = "DELETE FROM treatment_type WHERE name = ?";

        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, TEST_NAME);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up test data", e);
        }
    }
}