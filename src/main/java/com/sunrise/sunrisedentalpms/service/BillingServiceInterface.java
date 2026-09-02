package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.Bill;

import java.util.List;

public interface BillingServiceInterface {

    Bill generateBill(String appointmentNumber, String generatedByUserId) throws RecordNotFoundException, ValidationException;

    Bill findBillByAppointmentNumber(String appointmentNumber) throws RecordNotFoundException;

    Bill findBillById(String billId) throws RecordNotFoundException;

    List<Bill> listAllBills();
}