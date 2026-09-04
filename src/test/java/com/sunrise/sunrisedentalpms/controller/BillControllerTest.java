package com.sunrise.sunrisedentalpms.controller;

import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.Appointment;
import com.sunrise.sunrisedentalpms.model.AppointmentStatus;
import com.sunrise.sunrisedentalpms.model.Bill;
import com.sunrise.sunrisedentalpms.model.BillStatus;
import com.sunrise.sunrisedentalpms.model.Dentist;
import com.sunrise.sunrisedentalpms.model.Patient;
import com.sunrise.sunrisedentalpms.model.PaymentType;
import com.sunrise.sunrisedentalpms.model.TreatmentType;
import com.sunrise.sunrisedentalpms.model.User;
import com.sunrise.sunrisedentalpms.model.UserRole;
import com.sunrise.sunrisedentalpms.service.AppointmentServiceInterface;
import com.sunrise.sunrisedentalpms.service.BillingServiceInterface;
import com.sunrise.sunrisedentalpms.service.PatientServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillControllerTest {

    @Mock
    private BillingServiceInterface billingService;

    @Mock
    private PatientServiceInterface patientService;

    @Mock
    private AppointmentServiceInterface appointmentService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private HttpSession session;

    private BillController billController;

    @BeforeEach
    void setUp() {
        billController =
                new BillController(
                        billingService,
                        patientService,
                        appointmentService
                );
    }

    @Test
    void Get_whenNotLoggedIn_shouldRedirectToLogin()
            throws Exception {

        when(request.getSession(false))
                .thenReturn(null);

        billController.doGet(
                request,
                response
        );

        verify(response)
                .sendRedirect(
                        "login.jsp"
                );
    }

    @Test
    void Get_withBillId_shouldReturnBill()
            throws Exception {

        Bill bill =
                sampleBill();

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute("loggedInUser"))
                .thenReturn(sampleUser());

        when(request.getParameter("billId"))
                .thenReturn("1");

        when(billingService.findBillById("1"))
                .thenReturn(bill);

        when(request.getRequestDispatcher(
                "billDetails.jsp"
        )).thenReturn(dispatcher);

        billController.doGet(
                request,
                response
        );

        verify(request)
                .setAttribute(
                        "bill",
                        bill
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Get_withAppointmentNumber_shouldReturnBill()
            throws Exception {

        Bill bill =
                sampleBill();

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute("loggedInUser"))
                .thenReturn(sampleUser());

        when(request.getParameter("appointmentNumber"))
                .thenReturn("1");

        when(billingService
                .findBillByAppointmentNumber("1"))
                .thenReturn(bill);

        when(request.getRequestDispatcher(
                "billDetails.jsp"
        )).thenReturn(dispatcher);

        billController.doGet(
                request,
                response
        );

        verify(request)
                .setAttribute(
                        "bill",
                        bill
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Get_withContactNumber_shouldLoadOnlyScheduledAppointments()
            throws Exception {

        Patient patient =
                samplePatient();

        Appointment scheduledAppointment =
                sampleAppointment(
                        "1",
                        AppointmentStatus.SCHEDULED
                );

        Appointment completedAppointment =
                sampleAppointment(
                        "2",
                        AppointmentStatus.COMPLETED
                );

        Appointment cancelledAppointment =
                sampleAppointment(
                        "3",
                        AppointmentStatus.CANCELLED
                );

        List<Appointment> allAppointments =
                List.of(
                        scheduledAppointment,
                        completedAppointment,
                        cancelledAppointment
                );

        List<Bill> bills =
                List.of(
                        sampleBill()
                );

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute("loggedInUser"))
                .thenReturn(sampleUser());

        when(request.getParameter("contactNumber"))
                .thenReturn("0711234567");

        when(patientService
                .findPatientByContactNumber(
                        "0711234567"
                ))
                .thenReturn(patient);

        when(appointmentService
                .listAppointmentsForPatient(
                        "1"
                ))
                .thenReturn(allAppointments);

        when(billingService.listAllBills())
                .thenReturn(bills);

        when(request.getRequestDispatcher(
                "billList.jsp"
        )).thenReturn(dispatcher);

        billController.doGet(
                request,
                response
        );

        verify(request)
                .setAttribute(
                        "selectedPatient",
                        patient
                );

        verify(request)
                .setAttribute(
                        "patientAppointments",
                        List.of(scheduledAppointment)
                );

        verify(request)
                .setAttribute(
                        "bills",
                        bills
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Get_withUnknownContactNumber_shouldShowError()
            throws Exception {

        List<Bill> bills =
                List.of();

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute("loggedInUser"))
                .thenReturn(sampleUser());

        when(request.getParameter("contactNumber"))
                .thenReturn("0999999999");

        when(patientService
                .findPatientByContactNumber(
                        "0999999999"
                ))
                .thenThrow(
                        new RecordNotFoundException(
                                "No patient found with contact number 0999999999"
                        )
                );

        when(billingService.listAllBills())
                .thenReturn(bills);

        when(request.getRequestDispatcher(
                "billList.jsp"
        )).thenReturn(dispatcher);

        billController.doGet(
                request,
                response
        );

        verify(request)
                .setAttribute(
                        "errorMessage",
                        "No patient found with contact number 0999999999"
                );

        verify(request)
                .setAttribute(
                        "bills",
                        bills
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Get_withNoParams_shouldListAllBills()
            throws Exception {

        List<Bill> bills =
                List.of(
                        sampleBill()
                );

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute("loggedInUser"))
                .thenReturn(sampleUser());

        when(billingService.listAllBills())
                .thenReturn(bills);

        when(request.getRequestDispatcher(
                "billList.jsp"
        )).thenReturn(dispatcher);

        billController.doGet(
                request,
                response
        );

        verify(request)
                .setAttribute(
                        "bills",
                        bills
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Get_withInvalidBillId_shouldShowErrorOnList()
            throws Exception {

        List<Bill> bills =
                List.of();

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute("loggedInUser"))
                .thenReturn(sampleUser());

        when(request.getParameter("billId"))
                .thenReturn("99");

        when(billingService.findBillById("99"))
                .thenThrow(
                        new RecordNotFoundException(
                                "No bill found with id 99"
                        )
                );

        when(billingService.listAllBills())
                .thenReturn(bills);

        when(request.getRequestDispatcher(
                "billList.jsp"
        )).thenReturn(dispatcher);

        billController.doGet(
                request,
                response
        );

        verify(request)
                .setAttribute(
                        "errorMessage",
                        "No bill found with id 99"
                );

        verify(request)
                .setAttribute(
                        "bills",
                        bills
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Post_generate_withCashPayment_shouldGenerateBill()
            throws Exception {

        Bill bill =
                sampleBill(
                        PaymentType.CASH
                );

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute("loggedInUser"))
                .thenReturn(sampleUser());

        when(request.getParameter("appointmentNumber"))
                .thenReturn("1");

        when(request.getParameter("paymentType"))
                .thenReturn("CASH");

        when(billingService
                .generateBill(
                        "1",
                        PaymentType.CASH,
                        "1"
                ))
                .thenReturn(bill);

        when(request.getRequestDispatcher(
                "billDetails.jsp"
        )).thenReturn(dispatcher);

        billController.doPost(
                request,
                response
        );

        verify(billingService)
                .generateBill(
                        "1",
                        PaymentType.CASH,
                        "1"
                );

        verify(request)
                .setAttribute(
                        "bill",
                        bill
                );

        verify(request)
                .setAttribute(
                        "successMessage",
                        "Payment recorded and bill generated successfully."
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Post_generate_withCardPayment_shouldGenerateBill()
            throws Exception {

        Bill bill =
                sampleBill(
                        PaymentType.CARD
                );

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute("loggedInUser"))
                .thenReturn(sampleUser());

        when(request.getParameter("appointmentNumber"))
                .thenReturn("1");

        when(request.getParameter("paymentType"))
                .thenReturn("CARD");

        when(billingService
                .generateBill(
                        "1",
                        PaymentType.CARD,
                        "1"
                ))
                .thenReturn(bill);

        when(request.getRequestDispatcher(
                "billDetails.jsp"
        )).thenReturn(dispatcher);

        billController.doPost(
                request,
                response
        );

        verify(billingService)
                .generateBill(
                        "1",
                        PaymentType.CARD,
                        "1"
                );

        verify(request)
                .setAttribute(
                        "bill",
                        bill
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Post_generate_withoutPaymentType_shouldShowError()
            throws Exception {

        List<Bill> bills =
                List.of();

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute("loggedInUser"))
                .thenReturn(sampleUser());

        when(request.getParameter("appointmentNumber"))
                .thenReturn("1");

        when(request.getParameter("paymentType"))
                .thenReturn(null);

        when(billingService.listAllBills())
                .thenReturn(bills);

        when(request.getRequestDispatcher(
                "billList.jsp"
        )).thenReturn(dispatcher);

        billController.doPost(
                request,
                response
        );

        verify(request)
                .setAttribute(
                        "errorMessage",
                        "Please select a payment type."
                );

        verify(billingService, never())
                .generateBill(
                        anyString(),
                        any(PaymentType.class),
                        anyString()
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Post_generate_withoutAppointment_shouldShowError()
            throws Exception {

        List<Bill> bills =
                List.of();

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute("loggedInUser"))
                .thenReturn(sampleUser());

        when(request.getParameter("appointmentNumber"))
                .thenReturn("");

        when(request.getParameter("paymentType"))
                .thenReturn("CASH");

        when(billingService.listAllBills())
                .thenReturn(bills);

        when(request.getRequestDispatcher(
                "billList.jsp"
        )).thenReturn(dispatcher);

        billController.doPost(
                request,
                response
        );

        verify(request)
                .setAttribute(
                        "errorMessage",
                        "Please select an appointment."
                );

        verify(billingService, never())
                .generateBill(
                        anyString(),
                        any(PaymentType.class),
                        anyString()
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Post_generate_withInvalidPaymentType_shouldShowError()
            throws Exception {

        List<Bill> bills =
                List.of();

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute("loggedInUser"))
                .thenReturn(sampleUser());

        when(request.getParameter("appointmentNumber"))
                .thenReturn("1");

        when(request.getParameter("paymentType"))
                .thenReturn("CHEQUE");

        when(billingService.listAllBills())
                .thenReturn(bills);

        when(request.getRequestDispatcher(
                "billList.jsp"
        )).thenReturn(dispatcher);

        billController.doPost(
                request,
                response
        );

        verify(request)
                .setAttribute(
                        "errorMessage",
                        "Invalid payment type selected."
                );

        verify(billingService, never())
                .generateBill(
                        anyString(),
                        any(PaymentType.class),
                        anyString()
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Post_generate_whenAlreadyBilled_shouldShowError()
            throws Exception {

        List<Bill> bills =
                List.of();

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute("loggedInUser"))
                .thenReturn(sampleUser());

        when(request.getParameter("appointmentNumber"))
                .thenReturn("1");

        when(request.getParameter("paymentType"))
                .thenReturn("CASH");

        when(billingService
                .generateBill(
                        "1",
                        PaymentType.CASH,
                        "1"
                ))
                .thenThrow(
                        new ValidationException(
                                "Could not generate bill. A bill may already exist for this appointment."
                        )
                );

        when(billingService.listAllBills())
                .thenReturn(bills);

        when(request.getRequestDispatcher(
                "billList.jsp"
        )).thenReturn(dispatcher);

        billController.doPost(
                request,
                response
        );

        verify(request)
                .setAttribute(
                        "errorMessage",
                        "Could not generate bill. A bill may already exist for this appointment."
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Post_generate_withUnknownAppointment_shouldShowError()
            throws Exception {

        List<Bill> bills =
                List.of();

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute("loggedInUser"))
                .thenReturn(sampleUser());

        when(request.getParameter("appointmentNumber"))
                .thenReturn("99");

        when(request.getParameter("paymentType"))
                .thenReturn("CASH");

        when(billingService
                .generateBill(
                        "99",
                        PaymentType.CASH,
                        "1"
                ))
                .thenThrow(
                        new RecordNotFoundException(
                                "No appointment found with number 99"
                        )
                );

        when(billingService.listAllBills())
                .thenReturn(bills);

        when(request.getRequestDispatcher(
                "billList.jsp"
        )).thenReturn(dispatcher);

        billController.doPost(
                request,
                response
        );

        verify(request)
                .setAttribute(
                        "errorMessage",
                        "No appointment found with number 99"
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Post_whenNotLoggedIn_shouldRedirectToLogin()
            throws Exception {

        when(request.getSession(false))
                .thenReturn(null);

        billController.doPost(
                request,
                response
        );

        verify(response)
                .sendRedirect(
                        "login.jsp"
                );
    }

    private Patient samplePatient() {
        return new Patient(
                "1",
                "Kasun Silva",
                "12 Galle Road, Colombo",
                "0711234567"
        );
    }

    private Appointment sampleAppointment() {
        return sampleAppointment(
                "1",
                AppointmentStatus.SCHEDULED
        );
    }

    private Appointment sampleAppointment(
            String appointmentNumber,
            AppointmentStatus status) {

        Patient patient =
                samplePatient();

        Dentist dentist =
                new Dentist(
                        "1",
                        "Dr. Perera",
                        "0711234567"
                );

        TreatmentType treatmentType =
                new TreatmentType(
                        "1",
                        "Root Canal",
                        new BigDecimal("15000.00")
                );

        return new Appointment.Builder(
                appointmentNumber
        )
                .patient(patient)
                .dentist(dentist)
                .treatmentType(treatmentType)
                .appointmentDateTime(
                        LocalDateTime.now()
                                .plusDays(1)
                )
                .status(status)
                .bookedByUserId("1")
                .build();
    }

    private Bill sampleBill() {
        return sampleBill(
                PaymentType.CASH
        );
    }

    private Bill sampleBill(
            PaymentType paymentType) {

        Appointment appointment =
                sampleAppointment(
                        "1",
                        AppointmentStatus.COMPLETED
                );

        Bill bill =
                new Bill(
                        "1",
                        appointment,
                        new BigDecimal("15000.00"),
                        LocalDate.now(),
                        "1"
                );

        bill.setPaymentType(
                paymentType
        );

        bill.setStatus(
                BillStatus.PAID
        );

        return bill;
    }

    private User sampleUser() {
        return new User(
                "1",
                "jdoe",
                "hashedvalue",
                UserRole.RECEPTIONIST,
                "Jane Doe",
                "0711234567"
        );
    }
}