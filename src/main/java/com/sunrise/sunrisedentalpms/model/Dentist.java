package com.sunrise.sunrisedentalpms.model;

import java.util.Objects;

/** A dentist who treats patients. */
public class Dentist extends Person {

    private final String dentistId;
    private DentistStatus status;

    public Dentist(String dentistId, String name, String contactNumber) {
        super(name, contactNumber);
        if (dentistId == null || dentistId.trim().isEmpty()) {
            throw new IllegalArgumentException("Dentist ID cannot be empty");
        }
        this.dentistId = dentistId.trim();
        setStatus(DentistStatus.AVAILABLE);
    }

    public String getDentistId() {
        return dentistId;
    }

    public DentistStatus getStatus() {
        return status;
    }

    public void setStatus(DentistStatus status) {
        this.status = Objects.requireNonNull(status, "Status cannot be null");
    }

    @Override
    public String toString() {
        return "Dentist{" + "dentistId='" + dentistId + '\'' + ", name='" + getName() + '\'' + ", status=" + status + '}';
    }
}