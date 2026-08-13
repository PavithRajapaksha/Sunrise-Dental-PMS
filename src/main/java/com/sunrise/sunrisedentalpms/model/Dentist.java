package com.sunrise.sunrisedentalpms.model;

import java.math.BigDecimal;

/** A dentist who treats patients and has a per-visit consultation fee. */
public class Dentist extends Person {

    private final String dentistId;
    private BigDecimal consultationFee;

    public Dentist(String dentistId, String name, String contactNumber, BigDecimal consultationFee) {
        super(name, contactNumber);
        if (dentistId == null || dentistId.trim().isEmpty()) {
            throw new IllegalArgumentException("Dentist ID cannot be empty");
        }
        this.dentistId = dentistId.trim();
        setConsultationFee(consultationFee);
    }

    public String getDentistId() {
        return dentistId;
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
        return "Dentist{" + "dentistId='" + dentistId + '\'' + ", name='" + getName() + '\'' + '}';
    }
}