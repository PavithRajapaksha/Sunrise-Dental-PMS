package com.sunrise.sunrisedentalpms.dao;

import com.sunrise.sunrisedentalpms.model.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientDAOInterface {

    Patient createPatient(String name, String address, String contactNumber);

    Optional<Patient> findById(String patientId);

    Optional<Patient> findByContactNumber(String contactNumber);

    List<Patient> findAll();

    Patient findOrCreate(String name, String address, String contactNumber);
}