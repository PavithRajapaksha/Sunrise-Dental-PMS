package com.sunrise.sunrisedentalpms.dao;

import com.sunrise.sunrisedentalpms.model.TreatmentType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TreatmentTypeDAOInterface {

    TreatmentType createTreatmentType(String name, BigDecimal consultationFee);

    Optional<TreatmentType> findById(String treatmentTypeId);

    List<TreatmentType> findAll();

    boolean updateConsultationFee(String treatmentTypeId, BigDecimal newFee);
}