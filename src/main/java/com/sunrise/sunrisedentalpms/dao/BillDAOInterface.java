package com.sunrise.sunrisedentalpms.dao;

import com.sunrise.sunrisedentalpms.model.Appointment;
import com.sunrise.sunrisedentalpms.model.Bill;
import com.sunrise.sunrisedentalpms.model.PaymentType;

import java.util.List;
import java.util.Optional;

public interface BillDAOInterface {

    Bill createBill(
            Appointment appointment,
            PaymentType paymentType,
            String generatedByUserId
    );

    Optional<Bill> findByAppointmentNumber(String appointmentNumber);

    Optional<Bill> findById(String billId);

    List<Bill> findAll();
}