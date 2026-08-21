package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.TreatmentType;

import java.math.BigDecimal;
import java.util.List;

public interface TreatmentTypeServiceInterface {

    TreatmentType registerTreatmentType(String name, BigDecimal consultationFee) throws ValidationException;

    TreatmentType findTreatmentType(String treatmentTypeId) throws RecordNotFoundException;

    List<TreatmentType> listAllTreatmentTypes();

    void updateConsultationFee(String treatmentTypeId, BigDecimal newFee) throws RecordNotFoundException;
}