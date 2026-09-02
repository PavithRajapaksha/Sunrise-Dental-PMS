package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.exception.AuthorizationException;
import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.User;
import com.sunrise.sunrisedentalpms.model.UserRole;

import java.util.List;

public interface UserServiceInterface {

    User registerUser(String username, String plainPassword, String fullName, String contactNumber, UserRole requestingUserRole)
            throws ValidationException, AuthorizationException;

    User findUserByUsername(String username) throws RecordNotFoundException;

    List<User> listAllUsers();
}