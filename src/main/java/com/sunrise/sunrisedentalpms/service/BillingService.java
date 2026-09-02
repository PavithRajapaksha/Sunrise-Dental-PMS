package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.dao.BillDAOInterface;
import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.Appointment;
import com.sunrise.sunrisedentalpms.model.Bill;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class BillingService implements BillingServiceInterface {

    private final BillDAOInterface billDao;
    private final AppointmentServiceInterface appointmentService;

    public BillingService(BillDAOInterface billDao, AppointmentServiceInterface appointmentService) {
        this.billDao = Objects.requireNonNull(billDao, "BillDAOInterface cannot be null");
        this.appointmentService = Objects.requireNonNull(appointmentService, "AppointmentServiceInterface cannot be null");
    }

    // Generates a bill for an appointment
    @Override
    public Bill generateBill(String appointmentNumber, String generatedByUserId) throws RecordNotFoundException, ValidationException {
        Appointment appointment = appointmentService.findAppointment(appointmentNumber);

        Bill created = billDao.createBill(appointment, generatedByUserId);

        if (created == null) {
            throw new ValidationException("Could not generate bill. A bill may already exist for this appointment.");
        }

        return created;
    }

    @Override
    public Bill findBillByAppointmentNumber(String appointmentNumber) throws RecordNotFoundException {
        Optional<Bill> bill = billDao.findByAppointmentNumber(appointmentNumber);
        return bill.orElseThrow(() -> new RecordNotFoundException("No bill found for appointment " + appointmentNumber));
    }

    @Override
    public Bill findBillById(String billId) throws RecordNotFoundException {
        Optional<Bill> bill = billDao.findById(billId);
        return bill.orElseThrow(() -> new RecordNotFoundException("No bill found with id " + billId));
    }

    @Override
    public List<Bill> listAllBills() {
        return billDao.findAll();
    }
}