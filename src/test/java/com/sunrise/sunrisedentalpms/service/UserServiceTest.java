package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.dao.UserDAOInterface;
import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.User;
import com.sunrise.sunrisedentalpms.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDAOInterface userDao;

    private UserService userService;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        userService = new UserService(userDao);
        sampleUser = new User("1", "jdoe", "hashedvalue", UserRole.RECEPTIONIST, "Jane Doe", "0711234567");
    }

    @Test
    void Register_withValidData_shouldReturnUser() throws ValidationException {
        when(userDao.createStaff("jdoe", "pass1234", "Jane Doe", "0711234567")).thenReturn(sampleUser);

        User result = userService.registerUser("jdoe", "pass1234", "Jane Doe", "0711234567");

        assertEquals(sampleUser, result);
    }

    @Test
    void Register_whenDaoReturnsNull_shouldFail() {
        when(userDao.createStaff("jdoe", "pass1234", "Jane Doe", "0711234567")).thenReturn(null);

        assertThrows(ValidationException.class,
                () -> userService.registerUser("jdoe", "pass1234", "Jane Doe", "0711234567"));
    }

    @Test
    void Register_whenDaoThrowsInvalidData_shouldFail() {
        when(userDao.createStaff("bad", "pass1234", "Jane Doe", "0711234567"))
                .thenThrow(new IllegalArgumentException("Username must be at least 4 characters"));

        assertThrows(ValidationException.class,
                () -> userService.registerUser("bad", "pass1234", "Jane Doe", "0711234567"));
    }

    @Test
    void Find_withValidUsername_shouldReturnUser() throws RecordNotFoundException {
        when(userDao.findByUsername("jdoe")).thenReturn(Optional.of(sampleUser));

        User result = userService.findUserByUsername("jdoe");

        assertEquals(sampleUser, result);
    }

    @Test
    void Find_withInvalidUsername_shouldFail() {
        when(userDao.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> userService.findUserByUsername("unknown"));
    }

    @Test
    void ListAll_shouldReturnAllUsers() {
        when(userDao.findAllStaff()).thenReturn(List.of(sampleUser));

        List<User> result = userService.listAllUsers();

        assertEquals(1, result.size());
    }
}