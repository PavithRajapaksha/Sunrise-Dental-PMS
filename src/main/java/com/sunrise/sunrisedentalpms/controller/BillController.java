package com.sunrise.sunrisedentalpms.controller;

import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.Appointment;
import com.sunrise.sunrisedentalpms.model.AppointmentStatus;
import com.sunrise.sunrisedentalpms.model.Bill;
import com.sunrise.sunrisedentalpms.model.Patient;
import com.sunrise.sunrisedentalpms.model.PaymentType;
import com.sunrise.sunrisedentalpms.model.User;
import com.sunrise.sunrisedentalpms.service.AppointmentServiceInterface;
import com.sunrise.sunrisedentalpms.service.BillingServiceInterface;
import com.sunrise.sunrisedentalpms.service.PatientServiceInterface;
import com.sunrise.sunrisedentalpms.service.ServiceFactory;
import com.sunrise.sunrisedentalpms.util.BillGenerator;
import com.sunrise.sunrisedentalpms.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/bill")
public class BillController extends HttpServlet {

    private BillingServiceInterface billingService;

    private PatientServiceInterface patientService;

    private AppointmentServiceInterface appointmentService;

    public BillController() {
    }

    // Existing constructor retained for tests
    BillController(
            BillingServiceInterface billingService) {

        this.billingService =
                billingService;
    }

    // Additional constructor useful for testing
    BillController(
            BillingServiceInterface billingService,
            PatientServiceInterface patientService,
            AppointmentServiceInterface appointmentService) {

        this.billingService =
                billingService;

        this.patientService =
                patientService;

        this.appointmentService =
                appointmentService;
    }

    @Override
    public void init() {

        if (billingService == null) {
            billingService =
                    ServiceFactory
                            .getBillingService();
        }

        if (patientService == null) {
            patientService =
                    ServiceFactory
                            .getPatientService();
        }

        if (appointmentService == null) {
            appointmentService =
                    ServiceFactory
                            .getAppointmentService();
        }
    }

    // Show billing page / bill details
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException,
            IOException {

        if (SessionUtil.getLoggedInUser(request)
                == null) {

            response.sendRedirect(
                    "login.jsp"
            );

            return;
        }

        String billId =
                request.getParameter(
                        "billId"
                );

        String appointmentNumber =
                request.getParameter(
                        "appointmentNumber"
                );

        String contactNumber =
                request.getParameter(
                        "contactNumber"
                );

        String format =
                request.getParameter(
                        "format"
                );

        try {

            /*
             * Find bill by bill ID.
             */
            if (billId != null &&
                    !billId.trim().isEmpty()) {

                Bill bill =
                        billingService
                                .findBillById(
                                        billId
                                );

                if ("pdf".equalsIgnoreCase(
                        format)) {

                    streamBillPdf(
                            bill,
                            response
                    );

                    return;
                }

                request.setAttribute(
                        "bill",
                        bill
                );

                request.getRequestDispatcher(
                        "billDetails.jsp"
                ).forward(
                        request,
                        response
                );

                return;
            }

            /*
             * Find bill by appointment number.
             */
            if (appointmentNumber != null &&
                    !appointmentNumber
                            .trim()
                            .isEmpty()) {

                Bill bill =
                        billingService
                                .findBillByAppointmentNumber(
                                        appointmentNumber
                                );

                if ("pdf".equalsIgnoreCase(
                        format)) {

                    streamBillPdf(
                            bill,
                            response
                    );

                    return;
                }

                request.setAttribute(
                        "bill",
                        bill
                );

                request.getRequestDispatcher(
                        "billDetails.jsp"
                ).forward(
                        request,
                        response
                );

                return;
            }

            /*
             * Search for patient by contact number.
             */
            if (contactNumber != null &&
                    !contactNumber
                            .trim()
                            .isEmpty()) {

                loadPatientForBilling(
                        request,
                        contactNumber
                );
            }

            loadAllBills(request);

            request.getRequestDispatcher(
                    "billList.jsp"
            ).forward(
                    request,
                    response
            );

        } catch (RecordNotFoundException e) {

            request.setAttribute(
                    "errorMessage",
                    e.getMessage()
            );

            loadAllBills(request);

            request.getRequestDispatcher(
                    "billList.jsp"
            ).forward(
                    request,
                    response
            );
        }
    }

    // Generate bill
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException,
            IOException {

        User loggedInUser =
                SessionUtil.getLoggedInUser(
                        request
                );

        if (loggedInUser == null) {

            response.sendRedirect(
                    "login.jsp"
            );

            return;
        }

        String appointmentNumber =
                request.getParameter(
                        "appointmentNumber"
                );

        String paymentTypeText =
                request.getParameter(
                        "paymentType"
                );

        String contactNumber =
                request.getParameter(
                        "contactNumber"
                );

        if (appointmentNumber == null ||
                appointmentNumber
                        .trim()
                        .isEmpty()) {

            request.setAttribute(
                    "errorMessage",
                    "Please select an appointment."
            );

            forwardBackToBilling(
                    request,
                    response,
                    contactNumber
            );

            return;
        }

        if (paymentTypeText == null ||
                paymentTypeText
                        .trim()
                        .isEmpty()) {

            request.setAttribute(
                    "errorMessage",
                    "Please select a payment type."
            );

            forwardBackToBilling(
                    request,
                    response,
                    contactNumber
            );

            return;
        }

        try {

            PaymentType paymentType =
                    PaymentType.valueOf(
                            paymentTypeText
                                    .trim()
                                    .toUpperCase()
                    );

            Bill created =
                    billingService.generateBill(
                            appointmentNumber,
                            paymentType,
                            loggedInUser.getUserId()
                    );

            request.setAttribute(
                    "bill",
                    created
            );

            request.setAttribute(
                    "successMessage",
                    "Payment recorded and bill generated successfully."
            );

            request.getRequestDispatcher(
                    "billDetails.jsp"
            ).forward(
                    request,
                    response
            );

        } catch (IllegalArgumentException e) {

            request.setAttribute(
                    "errorMessage",
                    "Invalid payment type selected."
            );

            forwardBackToBilling(
                    request,
                    response,
                    contactNumber
            );

        } catch (RecordNotFoundException |
                 ValidationException e) {

            request.setAttribute(
                    "errorMessage",
                    e.getMessage()
            );

            forwardBackToBilling(
                    request,
                    response,
                    contactNumber
            );
        }
    }

    /*
     * Loads the patient and only appointments
     * that are actually allowed to be billed.
     */
    private void loadPatientForBilling(
            HttpServletRequest request,
            String contactNumber)
            throws RecordNotFoundException {

        Patient patient =
                patientService
                        .findPatientByContactNumber(
                                contactNumber.trim()
                        );

        List<Appointment> allAppointments =
                appointmentService
                        .listAppointmentsForPatient(
                                patient.getPatientId()
                        );

        List<Appointment> billableAppointments =
                new ArrayList<>();

        for (Appointment appointment :
                allAppointments) {

            if (appointment.getStatus()
                    == AppointmentStatus.SCHEDULED) {

                billableAppointments.add(
                        appointment
                );
            }
        }

        request.setAttribute(
                "selectedPatient",
                patient
        );

        request.setAttribute(
                "patientAppointments",
                billableAppointments
        );
    }

    private void loadAllBills(
            HttpServletRequest request) {

        request.setAttribute(
                "bills",
                billingService.listAllBills()
        );
    }

    /*
     * Used if generating a bill fails.
     * Keeps the patient and their appointments visible.
     */
    private void forwardBackToBilling(
            HttpServletRequest request,
            HttpServletResponse response,
            String contactNumber)
            throws ServletException,
            IOException {

        loadAllBills(request);

        if (contactNumber != null &&
                !contactNumber.trim().isEmpty()) {

            try {

                loadPatientForBilling(
                        request,
                        contactNumber
                );

            } catch (RecordNotFoundException ignored) {
                // Main error message is already displayed.
            }
        }

        request.getRequestDispatcher(
                "billList.jsp"
        ).forward(
                request,
                response
        );
    }

    // Streams bill as PDF
    private void streamBillPdf(
            Bill bill,
            HttpServletResponse response)
            throws IOException {

        byte[] pdfBytes =
                BillGenerator.generate(
                        bill
                );

        response.setContentType(
                "application/pdf"
        );

        response.setHeader(
                "Content-Disposition",
                "inline; filename=bill-"
                        + bill.getBillId()
                        + ".pdf"
        );

        response.setContentLength(
                pdfBytes.length
        );

        response
                .getOutputStream()
                .write(
                        pdfBytes
                );

        response
                .getOutputStream()
                .flush();
    }
}