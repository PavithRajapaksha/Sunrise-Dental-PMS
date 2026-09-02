package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.dao.PatientDAOInterface;
import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.Patient;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PatientService implements PatientServiceInterface {

    private final PatientDAOInterface patientDao;

    public PatientService(PatientDAOInterface patientDao) {
        this.patientDao = Objects.requireNonNull(patientDao, "PatientDAOInterface cannot be null");
    }

    // Registers a new patient
    @Override
    public Patient registerPatient(String name, String address, String contactNumber) throws ValidationException {
        try {
            Patient created = patientDao.createPatient(name, address, contactNumber);

            if (created == null) {
                throw new ValidationException("Could not register patient.");
            }

            return created;
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    // Finds a patient by id
    @Override
    public Patient findPatient(String patientId) throws RecordNotFoundException {
        Optional<Patient> patient = patientDao.findById(patientId);
        return patient.orElseThrow(() -> new RecordNotFoundException("No patient found with id " + patientId));
    }

    // Finds a patient by contact number
    @Override
    public Patient findPatientByContactNumber(String contactNumber) throws RecordNotFoundException {
        Optional<Patient> patient = patientDao.findByContactNumber(contactNumber);
        return patient.orElseThrow(() -> new RecordNotFoundException("No patient found with contact number " + contactNumber));
    }

    // Lists every registered patient
    @Override
    public List<Patient> listAllPatients() {
        return patientDao.findAll();
    }

    // Finds an existing patient by contact number, or registers a new one
    @Override
    public Patient findOrRegisterPatient(String name, String address, String contactNumber) throws ValidationException {
        try {
            return patientDao.findOrCreate(name, address, contactNumber);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        }
    }
}