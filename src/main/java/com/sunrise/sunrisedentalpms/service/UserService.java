package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.dao.UserDAOInterface;
import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UserService implements UserServiceInterface {

    private final UserDAOInterface userDao;

    public UserService(UserDAOInterface userDao) {
        this.userDao = Objects.requireNonNull(userDao, "UserDAOInterface cannot be null");
    }

    // Creates a new user
    @Override
    public User registerUser(String username, String plainPassword, String fullName, String contactNumber) throws ValidationException {
        try {
            User created = userDao.createStaff(username, plainPassword, fullName, contactNumber);

            if (created == null) {
                throw new ValidationException("Could not create user account.");
            }

            return created;
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    @Override
    public User findUserByUsername(String username) throws RecordNotFoundException {
        Optional<User> user = userDao.findByUsername(username);
        return user.orElseThrow(() -> new RecordNotFoundException("No user account found with username " + username));
    }

    @Override
    public List<User> listAllUsers() {
        return userDao.findAllStaff();
    }
}