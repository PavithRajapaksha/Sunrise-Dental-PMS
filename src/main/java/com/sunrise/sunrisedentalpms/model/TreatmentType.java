package com.sunrise.sunrisedentalpms.model;

import java.math.BigDecimal;

/** A treatment type offered by the clinic, with its consultation fee. */
public class TreatmentType {

    private final String treatmentTypeId;
    private String name;
    private BigDecimal consultationFee;

    public TreatmentType(String treatmentTypeId, String name, BigDecimal consultationFee) {
        if (treatmentTypeId == null || treatmentTypeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Treatment type ID cannot be empty");
        }
        this.treatmentTypeId = treatmentTypeId.trim();
        setName(name);
        setConsultationFee(consultationFee);
    }

    public String getTreatmentTypeId() {
        return treatmentTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name.trim();
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        if (consultationFee == null || consultationFee.signum() < 0) {
            throw new IllegalArgumentException("Consultation fee cannot be negative");
        }
        this.consultationFee = consultationFee;
    }

    @Override
    public String toString() {
        return "TreatmentType{" + "treatmentTypeId='" + treatmentTypeId + '\'' + ", name='" + name + '\'' + '}';
    }
}