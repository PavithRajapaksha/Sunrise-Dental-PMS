package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.dao.UserDAOInterface;
import com.sunrise.sunrisedentalpms.exception.AuthenticationException;
import com.sunrise.sunrisedentalpms.model.User;
import com.sunrise.sunrisedentalpms.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserDAOInterface userDao;

    private AuthenticationService authenticationService;
    private User validUser;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(userDao);
        validUser = new User("1", "jdoe", "hashedvalue", UserRole.RECEPTIONIST, "Jane Doe", "0711234567");
    }

    @Test
    void Login_withCorrectCredentials_shouldReturnUser() throws AuthenticationException {
        when(userDao.authenticate("jdoe", "correctPassword")).thenReturn(Optional.of(validUser));

        User result = authenticationService.login("jdoe", "correctPassword");

        assertEquals(validUser, result);
    }

    @Test
    void Login_withWrongCredentials_shouldFail() {
        when(userDao.authenticate("jdoe", "wrongPassword")).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class,
                () -> authenticationService.login("jdoe", "wrongPassword"));
    }

    @Test
    void Login_withMissingUsernameOrPassword_shouldFail() {
        assertThrows(AuthenticationException.class, () -> authenticationService.login(null, "pass"));
        assertThrows(AuthenticationException.class, () -> authenticationService.login("", "pass"));
        assertThrows(AuthenticationException.class, () -> authenticationService.login("jdoe", null));
        assertThrows(AuthenticationException.class, () -> authenticationService.login("jdoe", ""));
    }
}