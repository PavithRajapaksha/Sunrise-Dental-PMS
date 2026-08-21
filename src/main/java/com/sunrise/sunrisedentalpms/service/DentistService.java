package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.dao.DentistDAOInterface;
import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.Dentist;
import com.sunrise.sunrisedentalpms.model.DentistStatus;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DentistService implements DentistServiceInterface {

    private final DentistDAOInterface dentistDao;

    public DentistService(DentistDAOInterface dentistDao) {
        this.dentistDao = Objects.requireNonNull(dentistDao, "DentistDAOInterface cannot be null");
    }

    // Registers a new dentist
    @Override
    public Dentist registerDentist(String name, String contactNumber) throws ValidationException {
        Dentist created = dentistDao.createDentist(name, contactNumber);

        if (created == null) {
            throw new ValidationException("Could not register dentist. Check the name and contact number.");
        }

        return created;
    }
// find dentist
    @Override
    public Dentist findDentist(String dentistId) throws RecordNotFoundException {
        Optional<Dentist> dentist = dentistDao.findById(dentistId);
        return dentist.orElseThrow(() -> new RecordNotFoundException("No dentist found with id " + dentistId));
    }

    @Override
    public List<Dentist> listAllDentists() {
        return dentistDao.findAll();
    }

    @Override
    public List<Dentist> listAvailableDentists() {
        return dentistDao.findAllAvailable();
    }
// update dentist status
    @Override
    public void updateDentistStatus(String dentistId, DentistStatus newStatus) throws RecordNotFoundException {
        boolean updated = dentistDao.updateStatus(dentistId, newStatus);

        if (!updated) {
            throw new RecordNotFoundException("No dentist found with id " + dentistId);
        }
    }
}