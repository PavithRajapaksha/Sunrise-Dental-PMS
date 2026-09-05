package com.sunrise.sunrisedentalpms.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.sunrise.sunrisedentalpms.model.Dentist;
import com.sunrise.sunrisedentalpms.model.DentistStatus;

class DentistDAOTest {

    private static final String TEST_CONTACT_NUMBER = "0770000222";
    private static final String TEST_NAME = "Test Dentist";
    private static final String TEST_EMAIL = "test.dentist@example.com";

    private DentistDAOInterface dentistDao;

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

        dentistDao = new DentistDAO();
    }

    @AfterEach
    void tearDown() {
        dbConnectionMock.close();
    }

    @Test
    void createDentist_ShouldInsertDentistRecord() throws Exception {
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        Dentist created = dentistDao.createDentist(TEST_NAME, TEST_CONTACT_NUMBER, TEST_EMAIL);

        assertNotNull(created);
        assertEquals(TEST_NAME, created.getName());
        assertEquals(TEST_CONTACT_NUMBER, created.getContactNumber());
        assertEquals(DentistStatus.AVAILABLE, created.getStatus());

        verify(preparedStatement).setString(1, TEST_NAME);
        verify(preparedStatement).setString(2, TEST_CONTACT_NUMBER);
        verify(preparedStatement).setString(3, TEST_EMAIL);
        verify(preparedStatement).setString(4, "AVAILABLE");
    }

    @Test
    void findById_ShouldReturnCreatedDentist() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        stubDentistColumns(1, DentistStatus.AVAILABLE);

        Optional<Dentist> found = dentistDao.findById("1");

        assertTrue(found.isPresent());
        assertEquals("1", found.get().getDentistId());
        verify(preparedStatement).setInt(1, 1);
    }

    @Test
    void findAll_ShouldIncludeCreatedDentist() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        stubDentistColumns(1, DentistStatus.AVAILABLE);

        List<Dentist> allDentists = dentistDao.findAll();

        assertTrue(allDentists.stream()
                .anyMatch(d -> d.getContactNumber().equals(TEST_CONTACT_NUMBER)));
    }

    @Test
    void findAllAvailable_ShouldIncludeNewlyCreatedDentist() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        stubDentistColumns(1, DentistStatus.AVAILABLE);

        List<Dentist> available = dentistDao.findAllAvailable();

        assertTrue(available.stream()
                .anyMatch(d -> d.getContactNumber().equals(TEST_CONTACT_NUMBER)));
        verify(preparedStatement).setString(1, "AVAILABLE");
    }

    @Test
    void updateStatus_ShouldChangeDentistStatus() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        stubDentistColumns(1, DentistStatus.UNAVAILABLE);

        boolean updated = dentistDao.updateStatus("1", DentistStatus.UNAVAILABLE);

        assertTrue(updated);

        Optional<Dentist> found = dentistDao.findById("1");
        assertTrue(found.isPresent());
        assertEquals(DentistStatus.UNAVAILABLE, found.get().getStatus());
    }

    @Test
    void updateStatus_ShouldRemoveDentistFromAvailableList() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        boolean updated = dentistDao.updateStatus("1", DentistStatus.UNAVAILABLE);
        assertTrue(updated);

        List<Dentist> available = dentistDao.findAllAvailable();
        assertTrue(available.stream().noneMatch(d -> d.getDentistId().equals("1")));
    }

    @Test
    void updateStatus_WithInvalidId_ShouldReturnFalse() {
        boolean updated = dentistDao.updateStatus("not-a-number", DentistStatus.UNAVAILABLE);
        assertFalse(updated);
    }

    private void stubDentistColumns(int dentistId, DentistStatus status) throws Exception {
        when(resultSet.getInt("dentist_id")).thenReturn(dentistId);
        when(resultSet.getString("name")).thenReturn(TEST_NAME);
        when(resultSet.getString("contact_number")).thenReturn(TEST_CONTACT_NUMBER);
        when(resultSet.getString("email")).thenReturn(TEST_EMAIL);
        when(resultSet.getString("status")).thenReturn(status.name());
    }
}