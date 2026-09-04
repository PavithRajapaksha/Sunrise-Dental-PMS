package com.sunrise.sunrisedentalpms.model;

public class DentistWorkloadReport {

    private final String dentistId;
    private final String dentistName;
    private final int appointmentCount;

    public DentistWorkloadReport(String dentistId, String dentistName, int appointmentCount) {
        this.dentistId = dentistId;
        this.dentistName = dentistName;
        this.appointmentCount = appointmentCount;
    }

    public String getDentistId() {
        return dentistId;
    }

    public String getDentistName() {
        return dentistName;
    }

    public int getAppointmentCount() {
        return appointmentCount;
    }
}