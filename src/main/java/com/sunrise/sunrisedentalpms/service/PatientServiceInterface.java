package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.Patient;

import java.util.List;

public interface PatientServiceInterface {

    Patient registerPatient(String name, String address, String contactNumber, String email) throws ValidationException;

    Patient findPatient(String patientId) throws RecordNotFoundException;

    Patient findPatientByContactNumber(String contactNumber) throws RecordNotFoundException;

    List<Patient> listAllPatients();

    Patient findOrRegisterPatient(String name, String address, String contactNumber) throws ValidationException;
}