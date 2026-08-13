package com.sunrise.sunrisedentalpms.model;

/** A registered dental patient. */
public class Patient extends Person {

    private final String patientId;
    private String address;

    public Patient(String patientId, String name, String address, String contactNumber) {
        super(name, contactNumber);
        if (patientId == null || patientId.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient ID cannot be empty");
        }
        this.patientId = patientId.trim();
        setAddress(address);
    }

    public String getPatientId() {
        return patientId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty");
        }
        this.address = address.trim();
    }

    @Override
    public String toString() {
        return "Patient{" + "patientId='" + patientId + '\'' + ", name='" + getName() + '\'' + '}';
    }
}