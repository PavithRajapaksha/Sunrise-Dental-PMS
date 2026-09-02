package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.exception.AuthorizationException;
import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.TreatmentType;
import com.sunrise.sunrisedentalpms.model.UserRole;

import java.math.BigDecimal;
import java.util.List;

public interface TreatmentTypeServiceInterface {

    TreatmentType addTreatmentType(String name, BigDecimal consultationFee, UserRole requestingUserRole)
            throws ValidationException, AuthorizationException;

    TreatmentType findTreatmentType(String treatmentTypeId) throws RecordNotFoundException;

    List<TreatmentType> listAllTreatmentTypes();

    void updateConsultationFee(String treatmentTypeId, BigDecimal newFee, UserRole requestingUserRole)
            throws RecordNotFoundException, AuthorizationException;
}