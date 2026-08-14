package com.sunrise.sunrisedentalpms.dao;

import com.sunrise.sunrisedentalpms.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDAOInterface {

    // Looks up a user by username only, no password check
    Optional<User> findByUsername(String username);

    // Looks up a user and verifies the password; empty if either check fails
    Optional<User> authenticate(String username, String plainPassword);

    // Creates a new staff account; role is always forced to RECEPTIONIST
    User createStaff(String username, String plainPassword, String fullName, String contactNumber);

    // Lists every receptionist account, for the admin's staff management screen
    List<User> findAllStaff();
}