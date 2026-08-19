package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.exception.AuthenticationException;
import com.sunrise.sunrisedentalpms.model.User;

public interface AuthenticationServiceInterface {

    User login(String username, String plainPassword) throws AuthenticationException;
}