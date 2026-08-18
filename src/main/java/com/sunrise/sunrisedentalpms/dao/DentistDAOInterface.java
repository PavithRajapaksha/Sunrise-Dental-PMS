package com.sunrise.sunrisedentalpms.dao;

import com.sunrise.sunrisedentalpms.model.Dentist;
import com.sunrise.sunrisedentalpms.model.DentistStatus;

import java.util.List;
import java.util.Optional;

public interface DentistDAOInterface {

    Dentist createDentist(String name, String contactNumber);

    Optional<Dentist> findById(String dentistId);

    List<Dentist> findAll();

    List<Dentist> findAllAvailable();

    boolean updateStatus(String dentistId, DentistStatus newStatus);
}