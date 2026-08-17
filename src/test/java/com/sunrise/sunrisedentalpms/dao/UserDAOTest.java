 package com.sunrise.sunrisedentalpms.dao;

import com.sunrise.sunrisedentalpms.model.User;
import com.sunrise.sunrisedentalpms.model.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    private static final String TEST_USERNAME = "test_receptionist";
    private static final String TEST_PASSWORD = "TestPass123";

    private UserDAOInterface userDao;

    @BeforeEach
    void setUp() {
        userDao = new UserDAO();
        deleteTestUser();
    }

    @AfterEach
    void tearDown() {
        deleteTestUser();
    }

    @Test
    void createStaff_ShouldInsertUserWithReceptionistRole() {
        User created = userDao.createStaff(TEST_USERNAME, TEST_PASSWORD, "Test Receptionist", "0771112222");

        assertNotNull(created);
        assertEquals(TEST_USERNAME, created.getUsername());
        assertEquals(UserRole.RECEPTIONIST, created.getRole());
    }

    @Test
    void findByUsername_ShouldReturnCreatedUser() {
        userDao.createStaff(TEST_USERNAME, TEST_PASSWORD, "Test Receptionist", "0771112222");

        Optional<User> found = userDao.findByUsername(TEST_USERNAME);

        assertTrue(found.isPresent());
        assertEquals(TEST_USERNAME, found.get().getUsername());
    }

    @Test
    void authenticate_WithCorrectPassword_ShouldReturnUser() {
        userDao.createStaff(TEST_USERNAME, TEST_PASSWORD, "Test Receptionist", "0771112222");

        Optional<User> result = userDao.authenticate(TEST_USERNAME, TEST_PASSWORD);

        assertTrue(result.isPresent());
    }

    @Test
    void authenticate_WithWrongPassword_ShouldReturnEmpty() {
        userDao.createStaff(TEST_USERNAME, TEST_PASSWORD, "Test Receptionist", "0771112222");

        Optional<User> result = userDao.authenticate(TEST_USERNAME, "WrongPassword");

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllStaff_ShouldOnlyReturnReceptionists() {
        userDao.createStaff(TEST_USERNAME, TEST_PASSWORD, "Test Receptionist", "0771112222");

        List<User> staff = userDao.findAllStaff();

        assertTrue(staff.stream().allMatch(u -> u.getRole() == UserRole.RECEPTIONIST));
    }


    private void deleteTestUser() {
        String sql = "DELETE FROM users WHERE username = ?";

        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, TEST_USERNAME);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up test data", e);
        }
    }
}