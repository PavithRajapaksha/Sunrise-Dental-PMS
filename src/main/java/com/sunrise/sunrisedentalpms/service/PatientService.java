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
    private final Notifier notifier;

    public PatientService(PatientDAOInterface patientDao, Notifier notifier) {
        this.patientDao = Objects.requireNonNull(patientDao, "PatientDAOInterface cannot be null");
        this.notifier = Objects.requireNonNull(notifier, "Notifier cannot be null");
    }

    // Register new patient
    @Override
    public Patient registerPatient(String name, String address, String contactNumber, String email) throws ValidationException {
        try {
            Patient created = patientDao.createPatient(name, address, contactNumber, email);

            if (created == null) {
                throw new ValidationException("Could not register patient.");
            }

            notifier.publish(created.getEmail(),
                    "Welcome to Sunrise Dental Clinic, " + created.getName() + ". Your patient ID is " + created.getPatientId() + ".",
                    null);

            return created;
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    // Find patient by id
    @Override
    public Patient findPatient(String patientId) throws RecordNotFoundException {
        Optional<Patient> patient = patientDao.findById(patientId);
        return patient.orElseThrow(() -> new RecordNotFoundException("No patient found with id " + patientId));
    }

    // Find patient by contact number
    @Override
    public Patient findPatientByContactNumber(String contactNumber) throws RecordNotFoundException {
        Optional<Patient> patient = patientDao.findByContactNumber(contactNumber);
        return patient.orElseThrow(() -> new RecordNotFoundException("No patient found with contact number " + contactNumber));
    }

    // Lists all patients
    @Override
    public List<Patient> listAllPatients() {
        return patientDao.findAll();
    }

    // Find patient or register
    @Override
    public Patient findOrRegisterPatient(String name, String address, String contactNumber) throws ValidationException {
        try {
            return patientDao.findOrCreate(name, address, contactNumber);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        }
    }
}