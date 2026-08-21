package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.dao.TreatmentTypeDAOInterface;
import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.TreatmentType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TreatmentTypeService implements TreatmentTypeServiceInterface {

    private final TreatmentTypeDAOInterface treatmentTypeDao;

    public TreatmentTypeService(TreatmentTypeDAOInterface treatmentTypeDao) {
        this.treatmentTypeDao = Objects.requireNonNull(treatmentTypeDao, "TreatmentTypeDAOInterface cannot be null");
    }

    // Add a new treatment type
    @Override
    public TreatmentType registerTreatmentType(String name, BigDecimal consultationFee) throws ValidationException {
        TreatmentType created = treatmentTypeDao.createTreatmentType(name, consultationFee);

        if (created == null) {
            throw new ValidationException("Could not register treatment type. Check the name and fee.");
        }

        return created;
    }

    // Finds a treatment type by id
    @Override
    public TreatmentType findTreatmentType(String treatmentTypeId) throws RecordNotFoundException {
        Optional<TreatmentType> treatmentType = treatmentTypeDao.findById(treatmentTypeId);
        return treatmentType.orElseThrow(() -> new RecordNotFoundException("No treatment type found with id " + treatmentTypeId));
    }

    // Lists every treatment type
    @Override
    public List<TreatmentType> listAllTreatmentTypes() {
        return treatmentTypeDao.findAll();
    }

    // Updates the consultation fee for a treatment type
    @Override
    public void updateConsultationFee(String treatmentTypeId, BigDecimal newFee) throws RecordNotFoundException {
        boolean updated = treatmentTypeDao.updateConsultationFee(treatmentTypeId, newFee);

        if (!updated) {
            throw new RecordNotFoundException("Could not update treatment type " + treatmentTypeId + ". Check the id and fee.");
        }
    }
}