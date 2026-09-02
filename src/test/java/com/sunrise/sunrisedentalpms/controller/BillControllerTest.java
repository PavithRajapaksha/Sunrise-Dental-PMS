package com.sunrise.sunrisedentalpms.controller;

import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.Appointment;
import com.sunrise.sunrisedentalpms.model.Bill;
import com.sunrise.sunrisedentalpms.model.Dentist;
import com.sunrise.sunrisedentalpms.model.Patient;
import com.sunrise.sunrisedentalpms.model.TreatmentType;
import com.sunrise.sunrisedentalpms.model.User;
import com.sunrise.sunrisedentalpms.model.UserRole;
import com.sunrise.sunrisedentalpms.service.BillingServiceInterface;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillControllerTest {

    @Mock
    private BillingServiceInterface billingService;

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
        billController = new BillController(billingService);
    }

    @Test
    void Get_whenNotLoggedIn_shouldRedirectToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        billController.doGet(request, response);

        verify(response).sendRedirect("login.jsp");
    }

    @Test
    void Get_withBillId_shouldReturnBill() throws Exception {
        Bill bill = sampleBill();

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(sampleUser());
        when(request.getParameter("billId")).thenReturn("1");
        when(billingService.findBillById("1")).thenReturn(bill);
        when(request.getRequestDispatcher("billDetails.jsp")).thenReturn(dispatcher);

        billController.doGet(request, response);

        verify(request).setAttribute("bill", bill);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Get_withAppointmentNumber_shouldReturnBill() throws Exception {
        Bill bill = sampleBill();

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(sampleUser());
        when(request.getParameter("billId")).thenReturn(null);
        when(request.getParameter("appointmentNumber")).thenReturn("1");
        when(billingService.findBillByAppointmentNumber("1")).thenReturn(bill);
        when(request.getRequestDispatcher("billDetails.jsp")).thenReturn(dispatcher);

        billController.doGet(request, response);

        verify(request).setAttribute("bill", bill);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Get_withNoParams_shouldListAllBills() throws Exception {
        List<Bill> bills = List.of(sampleBill());

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(sampleUser());
        when(request.getParameter("billId")).thenReturn(null);
        when(request.getParameter("appointmentNumber")).thenReturn(null);
        when(billingService.listAllBills()).thenReturn(bills);
        when(request.getRequestDispatcher("billList.jsp")).thenReturn(dispatcher);

        billController.doGet(request, response);

        verify(request).setAttribute("bills", bills);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Get_withInvalidBillId_shouldShowErrorOnList() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(sampleUser());
        when(request.getParameter("billId")).thenReturn("99");
        when(billingService.findBillById("99"))
                .thenThrow(new RecordNotFoundException("No bill found with id 99"));
        when(request.getRequestDispatcher("billList.jsp")).thenReturn(dispatcher);

        billController.doGet(request, response);

        verify(request).setAttribute("errorMessage", "No bill found with id 99");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Post_generate_withValidAppointment_shouldGenerateBill() throws Exception {
        Bill bill = sampleBill();

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(sampleUser());
        when(request.getParameter("appointmentNumber")).thenReturn("1");
        when(billingService.generateBill("1", "1")).thenReturn(bill);
        when(request.getRequestDispatcher("billDetails.jsp")).thenReturn(dispatcher);

        billController.doPost(request, response);

        verify(request).setAttribute("bill", bill);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Post_generate_withUnknownAppointment_shouldShowError() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(sampleUser());
        when(request.getParameter("appointmentNumber")).thenReturn("99");
        when(billingService.generateBill("99", "1"))
                .thenThrow(new RecordNotFoundException("No appointment found with number 99"));
        when(request.getRequestDispatcher("appointmentDetails.jsp")).thenReturn(dispatcher);

        billController.doPost(request, response);

        verify(request).setAttribute("errorMessage", "No appointment found with number 99");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Post_generate_whenAlreadyBilled_shouldShowError() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(sampleUser());
        when(request.getParameter("appointmentNumber")).thenReturn("1");
        when(billingService.generateBill("1", "1"))
                .thenThrow(new ValidationException("Could not generate bill. A bill may already exist for this appointment."));
        when(request.getRequestDispatcher("appointmentDetails.jsp")).thenReturn(dispatcher);

        billController.doPost(request, response);

        verify(request).setAttribute("errorMessage", "Could not generate bill. A bill may already exist for this appointment.");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Post_whenNotLoggedIn_shouldRedirectToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        billController.doPost(request, response);

        verify(response).sendRedirect("login.jsp");
    }

    private Bill sampleBill() {
        Patient patient = new Patient("1", "Kasun Silva", "12 Galle Road, Colombo", "0711234567");
        Dentist dentist = new Dentist("1", "Dr. Perera", "0711234567");
        TreatmentType treatmentType = new TreatmentType("1", "Root Canal", new BigDecimal("15000.00"));

        Appointment appointment = new Appointment.Builder("1")
                .patient(patient).dentist(dentist).treatmentType(treatmentType)
                .appointmentDateTime(LocalDateTime.now().plusDays(1)).bookedByUserId("1").build();

        return new Bill("1", appointment, new BigDecimal("15000.00"), LocalDate.now(), "1");
    }

    private User sampleUser() {
        return new User("1", "jdoe", "hashedvalue", UserRole.RECEPTIONIST, "Jane Doe", "0711234567");
    }
}