package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.exception.AuthorizationException;
import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.Dentist;
import com.sunrise.sunrisedentalpms.model.DentistStatus;
import com.sunrise.sunrisedentalpms.model.UserRole;

import java.util.List;

public interface DentistServiceInterface {

    Dentist registerDentist(String name, String contactNumber, String email, UserRole requestingUserRole)
            throws ValidationException, AuthorizationException;

    Dentist findDentist(String dentistId) throws RecordNotFoundException;

    List<Dentist> listAllDentists();

    List<Dentist> listAvailableDentists();

    void updateDentistStatus(String dentistId, DentistStatus newStatus) throws RecordNotFoundException;
}