package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.User;

import java.util.List;

public interface UserServiceInterface {

    User registerUser(String username, String plainPassword, String fullName, String contactNumber) throws ValidationException;

    User findUserByUsername(String username) throws RecordNotFoundException;

    List<User> listAllUsers();
}