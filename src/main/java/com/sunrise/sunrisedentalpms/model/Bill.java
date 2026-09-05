package com.sunrise.sunrisedentalpms.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Bill {

    private final String billId;
    private final Appointment appointment;
    private final BigDecimal totalAmount;
    private final LocalDate generatedDate;
    private final String generatedByUserId;
    private BillStatus status;
    private PaymentType paymentType;

    public Bill(String billId, Appointment appointment, BigDecimal totalAmount,
                LocalDate generatedDate, String generatedByUserId) {
        if (billId == null || billId.trim().isEmpty()) {
            throw new IllegalArgumentException("Bill ID cannot be empty");
        }
        if (totalAmount == null || totalAmount.signum() < 0) {
            throw new IllegalArgumentException("Total amount cannot be negative");
        }
        if (generatedByUserId == null || generatedByUserId.trim().isEmpty()) {
            throw new IllegalArgumentException("Generated-by user ID cannot be empty");
        }

        this.billId = billId.trim();
        this.appointment = Objects.requireNonNull(appointment, "Appointment cannot be null");
        this.totalAmount = totalAmount;
        this.generatedDate = generatedDate == null ? LocalDate.now() : generatedDate;
        this.generatedByUserId = generatedByUserId.trim();
        this.status = BillStatus.PENDING;
    }

    public String getBillId() {
        return billId;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public LocalDate getGeneratedDate() {
        return generatedDate;
    }

    public String getGeneratedByUserId() {
        return generatedByUserId;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = Objects.requireNonNull(status, "Status cannot be null");
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    @Override
    public String toString() {
        return "Bill{" + "billId='" + billId + '\'' + ", total=" + totalAmount + '}';
    }
}