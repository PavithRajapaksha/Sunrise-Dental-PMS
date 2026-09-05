package com.sunrise.sunrisedentalpms.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
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

import com.sunrise.sunrisedentalpms.model.User;
import com.sunrise.sunrisedentalpms.model.UserRole;
import com.sunrise.sunrisedentalpms.util.PasswordUtil;

class UserDAOTest {

    private static final String TEST_USERNAME = "test_receptionist";
    private static final String TEST_PASSWORD = "TestPass123";

    private UserDAOInterface userDao;

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

        userDao = new UserDAO();
    }

    @AfterEach
    void tearDown() {
        dbConnectionMock.close();
    }

    @Test
    void createStaff_ShouldInsertUserWithReceptionistRole() throws Exception {
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        User created = userDao.createStaff(TEST_USERNAME, TEST_PASSWORD, "Test Receptionist", "0771112222");

        assertNotNull(created);
        assertEquals(TEST_USERNAME, created.getUsername());
        assertEquals(UserRole.RECEPTIONIST, created.getRole());
    }

    @Test
    void findByUsername_ShouldReturnCreatedUser() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        stubUserColumns(1, PasswordUtil.hash(TEST_PASSWORD));

        Optional<User> found = userDao.findByUsername(TEST_USERNAME);

        assertTrue(found.isPresent());
        assertEquals(TEST_USERNAME, found.get().getUsername());
    }

    @Test
    void authenticate_WithCorrectPassword_ShouldReturnUser() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        stubUserColumns(1, PasswordUtil.hash(TEST_PASSWORD));

        Optional<User> result = userDao.authenticate(TEST_USERNAME, TEST_PASSWORD);

        assertTrue(result.isPresent());
    }

    @Test
    void authenticate_WithWrongPassword_ShouldReturnEmpty() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        stubUserColumns(1, PasswordUtil.hash(TEST_PASSWORD));

        Optional<User> result = userDao.authenticate(TEST_USERNAME, "WrongPassword");

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllStaff_ShouldOnlyReturnReceptionists() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        stubUserColumns(1, PasswordUtil.hash(TEST_PASSWORD));

        List<User> staff = userDao.findAllStaff();

        assertTrue(staff.stream().allMatch(u -> u.getRole() == UserRole.RECEPTIONIST));
    }

    private void stubUserColumns(int userId, String hashedPassword) throws Exception {
        when(resultSet.getInt("user_id")).thenReturn(userId);
        when(resultSet.getString("username")).thenReturn(TEST_USERNAME);
        when(resultSet.getString("password_hash")).thenReturn(hashedPassword);
        when(resultSet.getString("full_name")).thenReturn("Test Receptionist");
        when(resultSet.getString("contact_number")).thenReturn("0771112222");
        when(resultSet.getString("role")).thenReturn("RECEPTIONIST");
    }
}