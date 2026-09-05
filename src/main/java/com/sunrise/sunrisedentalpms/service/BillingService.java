package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.dao.BillDAOInterface;
import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.Appointment;
import com.sunrise.sunrisedentalpms.model.AppointmentStatus;
import com.sunrise.sunrisedentalpms.model.Bill;
import com.sunrise.sunrisedentalpms.model.PaymentType;
import com.sunrise.sunrisedentalpms.util.BillGenerator;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class BillingService
        implements BillingServiceInterface {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern(
                    "dd MMM yyyy"
            );

    private final BillDAOInterface billDao;

    private final AppointmentServiceInterface
            appointmentService;

    private final Notifier notifier;

    public BillingService(
            BillDAOInterface billDao,
            AppointmentServiceInterface appointmentService,
            Notifier notifier) {

        this.billDao =
                Objects.requireNonNull(
                        billDao,
                        "BillDAOInterface cannot be null"
                );

        this.appointmentService =
                Objects.requireNonNull(
                        appointmentService,
                        "AppointmentServiceInterface cannot be null"
                );

        this.notifier =
                Objects.requireNonNull(
                        notifier,
                        "Notifier cannot be null"
                );
    }

    // Generate and pay a bill for an appointment
    @Override
    public Bill generateBill(
            String appointmentNumber,
            PaymentType paymentType,
            String generatedByUserId)
            throws RecordNotFoundException,
            ValidationException {

        if (appointmentNumber == null ||
                appointmentNumber.trim().isEmpty()) {

            throw new ValidationException(
                    "Please select an appointment."
            );
        }

        if (paymentType == null) {

            throw new ValidationException(
                    "Please select a payment type."
            );
        }

        Appointment appointment =
                appointmentService.findAppointment(
                        appointmentNumber
                );

        /*
         * Only active scheduled appointments
         * may proceed to billing.
         */
        if (appointment.getStatus()
                != AppointmentStatus.SCHEDULED) {

            throw new ValidationException(
                    "Only scheduled appointments can be billed."
            );
        }

        /*
         * Create the bill first.
         *
         * This prevents the appointment becoming COMPLETED
         * when bill creation itself fails.
         */
        Bill created =
                billDao.createBill(
                        appointment,
                        paymentType,
                        generatedByUserId
                );

        if (created == null) {

            throw new ValidationException(
                    "Could not generate bill. " +
                            "A bill may already exist for this appointment."
            );
        }

        /*
         * Bill creation succeeded.
         * The treatment/payment workflow is now complete.
         */
        appointmentService.updateAppointmentStatus(
                appointmentNumber,
                AppointmentStatus.COMPLETED
        );

        String message =
                "Your bill for appointment #"
                        + appointmentNumber
                        + " is ready.\n"
                        + "Treatment: "
                        + appointment
                        .getTreatmentType()
                        .getName()
                        + "\n"
                        + "Amount: Rs. "
                        + created.getTotalAmount()
                        + "\n"
                        + "Payment Type: "
                        + paymentType.name()
                        + "\n"
                        + "Status: PAID"
                        + "\n"
                        + "Date: "
                        + created
                        .getGeneratedDate()
                        .format(DATE_FORMAT)
                        + "\n\n"
                        + "Thank you for choosing "
                        + "Sunrise Dental Clinic.";

        byte[] pdfBytes = null;

        try {

            pdfBytes =
                    BillGenerator.generate(
                            created
                    );

        } catch (IOException e) {

            System.err.println(
                    "Could not build PDF for bill "
                            + created.getBillId()
            );

            e.printStackTrace();
        }

        notifier.publish(
                appointment.getPatient().getEmail(),
                message,
                appointmentNumber,
                pdfBytes,
                "bill-"
                        + created.getBillId()
                        + ".pdf"
        );

        return created;
    }

    @Override
    public Bill findBillByAppointmentNumber(
            String appointmentNumber)
            throws RecordNotFoundException {

        Optional<Bill> bill =
                billDao.findByAppointmentNumber(
                        appointmentNumber
                );

        return bill.orElseThrow(
                () -> new RecordNotFoundException(
                        "No bill found for appointment "
                                + appointmentNumber
                )
        );
    }

    @Override
    public Bill findBillById(
            String billId)
            throws RecordNotFoundException {

        Optional<Bill> bill =
                billDao.findById(
                        billId
                );

        return bill.orElseThrow(
                () -> new RecordNotFoundException(
                        "No bill found with id "
                                + billId
                )
        );
    }

    @Override
    public List<Bill> listAllBills() {
        return billDao.findAll();
    }
}