package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.dao.UserDAOInterface;
import com.sunrise.sunrisedentalpms.exception.AuthenticationException;
import com.sunrise.sunrisedentalpms.model.User;

import java.util.Objects;
import java.util.Optional;

public class AuthenticationService implements AuthenticationServiceInterface {

    private static final String LOGIN_FAILED_MESSAGE = "Invalid username or password.";

    private final UserDAOInterface userDao;

    public AuthenticationService(UserDAOInterface userDao) {
        this.userDao = Objects.requireNonNull(userDao, "UserDAOInterface cannot be null");
    }

    // Verifies the username and password against the users table
    @Override
    public User login(String username, String plainPassword) throws AuthenticationException {
        if (username == null || username.isEmpty()
                || plainPassword == null || plainPassword.isEmpty()) {
            throw new AuthenticationException(LOGIN_FAILED_MESSAGE);
        }

        Optional<User> authenticatedUser = userDao.authenticate(username, plainPassword);

        return authenticatedUser.orElseThrow(() -> new AuthenticationException(LOGIN_FAILED_MESSAGE));
    }
}