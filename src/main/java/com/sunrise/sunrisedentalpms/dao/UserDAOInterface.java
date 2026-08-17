package com.sunrise.sunrisedentalpms.dao;

import com.sunrise.sunrisedentalpms.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDAOInterface {

    Optional<User> findByUsername(String username);

    Optional<User> authenticate(String username, String plainPassword);

    User createStaff(String username, String plainPassword, String fullName, String contactNumber);

    List<User> findAllStaff();
}