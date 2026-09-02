package com.sunrise.sunrisedentalpms.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.sunrise.sunrisedentalpms.model.TreatmentType;

class TreatmentTypeDAOTest {

    private static final String TEST_NAME = "Test Treatment";
    private static final BigDecimal TEST_FEE = new BigDecimal("2500.00");

    private TreatmentTypeDAOInterface treatmentTypeDao;

    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    private MockedStatic<DBConnection> dbConnectionMock;

    @BeforeEach
    void setUp() {
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);

        DBConnection mockDbConnection = mock(DBConnection.class);
        dbConnectionMock = mockStatic(DBConnection.class);
        dbConnectionMock.when(DBConnection::getInstance).thenReturn(mockDbConnection);
        when(mockDbConnection.getConnection()).thenReturn(connection);

        treatmentTypeDao = new TreatmentTypeDAO();
    }

    @AfterEach
    void tearDown() {
        dbConnectionMock.close();
    }

    @Test
    void createTreatmentType_ShouldInsertTreatmentTypeRecord() throws Exception {
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        TreatmentType created = treatmentTypeDao.createTreatmentType(TEST_NAME, TEST_FEE);

        assertNotNull(created);
        assertEquals(TEST_NAME, created.getName());
        assertEquals(0, TEST_FEE.compareTo(created.getConsultationFee()));
    }

    @Test
    void findById_ShouldReturnCreatedTreatmentType() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        stubTreatmentTypeColumns(1, TEST_FEE);

        Optional<TreatmentType> found = treatmentTypeDao.findById("1");

        assertTrue(found.isPresent());
        assertEquals("1", found.get().getTreatmentTypeId());
    }

    @Test
    void findAll_ShouldIncludeCreatedTreatmentType() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        stubTreatmentTypeColumns(1, TEST_FEE);

        List<TreatmentType> allTreatmentTypes = treatmentTypeDao.findAll();

        assertTrue(allTreatmentTypes.stream()
                .anyMatch(t -> t.getName().equals(TEST_NAME)));
    }

    @Test
    void updateConsultationFee_ShouldChangeFee() throws Exception {
        BigDecimal newFee = new BigDecimal("3000.00");

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        stubTreatmentTypeColumns(1, newFee);

        boolean updated = treatmentTypeDao.updateConsultationFee("1", newFee);

        assertTrue(updated);

        Optional<TreatmentType> found = treatmentTypeDao.findById("1");
        assertTrue(found.isPresent());
        assertEquals(0, newFee.compareTo(found.get().getConsultationFee()));
    }

    @Test
    void updateConsultationFee_WithNegativeFee_ShouldReturnFalse() {
        // validated before any database call is made, so no stubbing is needed
        boolean updated = treatmentTypeDao.updateConsultationFee("1", new BigDecimal("-100"));
        assertFalse(updated);
    }

    @Test
    void updateConsultationFee_WithInvalidId_ShouldReturnFalse() {
        boolean updated = treatmentTypeDao.updateConsultationFee("not-a-number", TEST_FEE);
        assertFalse(updated);
    }

    private void stubTreatmentTypeColumns(int treatmentTypeId, BigDecimal fee) throws Exception {
        when(resultSet.getInt("treatment_type_id")).thenReturn(treatmentTypeId);
        when(resultSet.getString("name")).thenReturn(TEST_NAME);
        when(resultSet.getBigDecimal("consultation_fee")).thenReturn(fee);
    }
}