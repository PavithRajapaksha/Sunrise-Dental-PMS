package com.sunrise.sunrisedentalpms.model;

import java.time.LocalDateTime;
import java.util.Objects;


public class Appointment {

    private final String appointmentNumber;
    private Patient patient;
    private Dentist dentist;
    private TreatmentType treatmentType;
    private LocalDateTime appointmentDateTime;
    private AppointmentStatus status;
    private String bookedByUserId;

    private Appointment(Builder builder) {
        this.appointmentNumber = Objects.requireNonNull(builder.appointmentNumber, "Appointment number cannot be null");
        this.patient = Objects.requireNonNull(builder.patient, "Patient cannot be null");
        this.dentist = Objects.requireNonNull(builder.dentist, "Dentist cannot be null");
        this.treatmentType = Objects.requireNonNull(builder.treatmentType, "Treatment type cannot be null");
        this.appointmentDateTime = Objects.requireNonNull(builder.appointmentDateTime, "Appointment date/time cannot be null");
        this.status = Objects.requireNonNull(builder.status, "Status cannot be null");
        this.bookedByUserId = Objects.requireNonNull(builder.bookedByUserId, "Booked-by user ID cannot be null");
    }

    // getters
    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public Patient getPatient() {
        return patient;
    }

    public Dentist getDentist() {
        return dentist;
    }

    public TreatmentType getTreatmentType() {
        return treatmentType;
    }

    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public String getBookedByUserId() {
        return bookedByUserId;
    }

    // setters
    public void setPatient(Patient patient) {
        this.patient = Objects.requireNonNull(patient, "Patient cannot be null");
    }

    public void setDentist(Dentist dentist) {
        this.dentist = Objects.requireNonNull(dentist, "Dentist cannot be null");
    }

    public void setTreatmentType(TreatmentType treatmentType) {
        this.treatmentType = Objects.requireNonNull(treatmentType, "Treatment type cannot be null");
    }

    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
        this.appointmentDateTime = Objects.requireNonNull(appointmentDateTime, "Appointment date/time cannot be null");
    }

    public void setStatus(AppointmentStatus status) {
        this.status = Objects.requireNonNull(status, "Status cannot be null");
    }

    // toString
    @Override
    public String toString() {
        return "Appointment{" + "appointmentNumber='" + appointmentNumber + '\'' + ", patient=" + patient.getName()
                + ", dentist=" + dentist.getName() + ", treatmentType=" + treatmentType
                + ", appointmentDateTime=" + appointmentDateTime + ", status=" + status + '}';
    }


    public static class Builder {
        private final String appointmentNumber;
        private Patient patient;
        private Dentist dentist;
        private TreatmentType treatmentType;
        private LocalDateTime appointmentDateTime;
        private AppointmentStatus status = AppointmentStatus.SCHEDULED;
        private String bookedByUserId;

        public Builder(String appointmentNumber) {
            this.appointmentNumber = appointmentNumber;
        }

        public Builder patient(Patient patient) {
            this.patient = patient;
            return this;
        }

        public Builder dentist(Dentist dentist) {
            this.dentist = dentist;
            return this;
        }

        public Builder treatmentType(TreatmentType treatmentType) {
            this.treatmentType = treatmentType;
            return this;
        }

        public Builder appointmentDateTime(LocalDateTime appointmentDateTime) {
            this.appointmentDateTime = appointmentDateTime;
            return this;
        }

        public Builder status(AppointmentStatus status) {
            this.status = status;
            return this;
        }

        public Builder bookedByUserId(String bookedByUserId) {
            this.bookedByUserId = bookedByUserId;
            return this;
        }

        public Appointment build() {
            return new Appointment(this);
        }
    }
}