package com.sunrise.sunrisedentalpms.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
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

import com.sunrise.sunrisedentalpms.model.Patient;

class PatientDAOTest {

    private static final String TEST_CONTACT_NUMBER = "0770000111";
    private static final String TEST_NAME = "Test Patient";
    private static final String TEST_ADDRESS = "123 Test Lane, Colombo";

    private PatientDAOInterface patientDao;

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

        patientDao = new PatientDAO();
    }

    @AfterEach
    void tearDown() {
        dbConnectionMock.close();
    }

    @Test
    void createPatient_ShouldInsertPatientRecord() throws Exception {
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        Patient created = patientDao.createPatient(TEST_NAME, TEST_ADDRESS, TEST_CONTACT_NUMBER);

        assertNotNull(created);
        assertEquals(TEST_NAME, created.getName());
        assertEquals(TEST_ADDRESS, created.getAddress());
        assertEquals(TEST_CONTACT_NUMBER, created.getContactNumber());

        verify(preparedStatement).setString(1, TEST_NAME);
        verify(preparedStatement).setString(2, TEST_ADDRESS);
        verify(preparedStatement).setString(3, TEST_CONTACT_NUMBER);
    }

    @Test
    void findById_ShouldReturnCreatedPatient() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        stubPatientColumns(1);

        Optional<Patient> found = patientDao.findById("1");

        assertTrue(found.isPresent());
        assertEquals("1", found.get().getPatientId());
        verify(preparedStatement).setInt(1, 1);
    }

    @Test
    void findByContactNumber_ShouldReturnCreatedPatient() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        stubPatientColumns(1);

        Optional<Patient> found = patientDao.findByContactNumber(TEST_CONTACT_NUMBER);

        assertTrue(found.isPresent());
        assertEquals(TEST_NAME, found.get().getName());
        verify(preparedStatement).setString(1, TEST_CONTACT_NUMBER);
    }

    @Test
    void findAll_ShouldIncludeCreatedPatient() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        stubPatientColumns(1);

        List<Patient> allPatients = patientDao.findAll();

        assertTrue(allPatients.stream()
                .anyMatch(p -> p.getContactNumber().equals(TEST_CONTACT_NUMBER)));
    }

    @Test
    void findOrCreate_ExistingPatient_ReturnsSameRecord() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        stubPatientColumns(1);

        Patient result = patientDao.findOrCreate("Different Name", "Different Address", TEST_CONTACT_NUMBER);

        assertEquals("1", result.getPatientId());
        assertEquals(TEST_NAME, result.getName()); // unchanged, since existing record was reused
        verify(connection, never()).prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS));
    }

    @Test
    void findOrCreate_NewPatient_CreatesRecord() throws Exception {
        PreparedStatement selectStatement = mock(PreparedStatement.class);
        PreparedStatement insertStatement = mock(PreparedStatement.class);
        ResultSet selectResultSet = mock(ResultSet.class);
        ResultSet generatedKeys = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(selectStatement);
        when(selectStatement.executeQuery()).thenReturn(selectResultSet);
        when(selectResultSet.next()).thenReturn(false);

        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(insertStatement);
        when(insertStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(2);

        Patient result = patientDao.findOrCreate(TEST_NAME, TEST_ADDRESS, TEST_CONTACT_NUMBER);

        assertNotNull(result);
        assertEquals(TEST_CONTACT_NUMBER, result.getContactNumber());
    }

    private void stubPatientColumns(int patientId) throws Exception {
        when(resultSet.getInt("patient_id")).thenReturn(patientId);
        when(resultSet.getString("name")).thenReturn(TEST_NAME);
        when(resultSet.getString("address")).thenReturn(TEST_ADDRESS);
        when(resultSet.getString("contact_number")).thenReturn(TEST_CONTACT_NUMBER);
    }
}