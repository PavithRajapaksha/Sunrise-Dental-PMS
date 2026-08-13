package com.sunrise.sunrisedentalpms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/** Invoice generated for an appointment: treatment cost plus the dentist's consultation fee. */
public class Bill {

    private final String billId;
    private Appointment appointment;
    private final LocalDateTime billDate;

    public Bill(String billId, Appointment appointment, LocalDateTime billDate) {
        if (billId == null || billId.trim().isEmpty()) {
            throw new IllegalArgumentException("Bill ID cannot be empty");
        }
        this.billId = billId.trim();
        setAppointment(appointment);
        this.billDate = billDate == null ? LocalDateTime.now() : billDate;
    }

    public String getBillId() {
        return billId;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = Objects.requireNonNull(appointment, "Appointment cannot be null");
    }

    public LocalDateTime getBillDate() {
        return billDate;
    }

    public BigDecimal getTreatmentCost() {
        return appointment.getTreatmentType().getBaseCost();
    }

    public BigDecimal getConsultationFee() {
        return appointment.getDentist().getConsultationFee();
    }

    public BigDecimal getTotalAmount() {
        return getTreatmentCost().add(getConsultationFee());
    }

    @Override
    public String toString() {
        return "Bill{" + "billId='" + billId + '\'' + ", total=" + getTotalAmount() + '}';
    }
}