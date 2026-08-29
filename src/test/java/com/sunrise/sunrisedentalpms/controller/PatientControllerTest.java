package com.sunrise.sunrisedentalpms.controller;

import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.Appointment;
import com.sunrise.sunrisedentalpms.model.Dentist;
import com.sunrise.sunrisedentalpms.model.Patient;
import com.sunrise.sunrisedentalpms.model.TreatmentType;
import com.sunrise.sunrisedentalpms.model.User;
import com.sunrise.sunrisedentalpms.model.UserRole;
import com.sunrise.sunrisedentalpms.service.AppointmentServiceInterface;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

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

    private PatientController patientController;

    @BeforeEach
    void setUp() {
        patientController = new PatientController(patientService, appointmentService);
    }

    @Test
    void Get_whenNotLoggedIn_shouldRedirectToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        patientController.doGet(request, response);

        verify(response).sendRedirect("login.jsp");
    }

    @Test
    void Get_withPatientId_shouldReturnPatientWithHistory() throws Exception {
        Patient patient = new Patient("1", "Kasun Silva", "12 Galle Road, Colombo", "0711234567");
        List<Appointment> appointments = List.of(sampleAppointment(patient));

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(sampleUser());
        when(request.getParameter("patientId")).thenReturn("1");
        when(patientService.findPatient("1")).thenReturn(patient);
        when(appointmentService.listAppointmentsForPatient("1")).thenReturn(appointments);
        when(request.getRequestDispatcher("patientDetails.jsp")).thenReturn(dispatcher);

        patientController.doGet(request, response);

        verify(request).setAttribute("patient", patient);
        verify(request).setAttribute("appointments", appointments);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Get_withContactNumber_shouldReturnPatientWithHistory() throws Exception {
        Patient patient = new Patient("1", "Kasun Silva", "12 Galle Road, Colombo", "0711234567");
        List<Appointment> appointments = List.of(sampleAppointment(patient));

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(sampleUser());
        when(request.getParameter("patientId")).thenReturn(null);
        when(request.getParameter("contactNumber")).thenReturn("0711234567");
        when(patientService.findPatientByContactNumber("0711234567")).thenReturn(patient);
        when(appointmentService.listAppointmentsForPatient("1")).thenReturn(appointments);
        when(request.getRequestDispatcher("patientDetails.jsp")).thenReturn(dispatcher);

        patientController.doGet(request, response);

        verify(request).setAttribute("patient", patient);
        verify(request).setAttribute("appointments", appointments);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Get_withNoParams_shouldListAllPatients() throws Exception {
        List<Patient> patients = List.of(new Patient("1", "Kasun Silva", "12 Galle Road, Colombo", "0711234567"));

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(sampleUser());
        when(request.getParameter("patientId")).thenReturn(null);
        when(request.getParameter("contactNumber")).thenReturn(null);
        when(patientService.listAllPatients()).thenReturn(patients);
        when(request.getRequestDispatcher("patientList.jsp")).thenReturn(dispatcher);

        patientController.doGet(request, response);

        verify(request).setAttribute("patients", patients);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Get_withInvalidPatientId_shouldShowErrorOnList() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(sampleUser());
        when(request.getParameter("patientId")).thenReturn("99");
        when(patientService.findPatient("99"))
                .thenThrow(new RecordNotFoundException("No patient found with id 99"));
        when(request.getRequestDispatcher("patientList.jsp")).thenReturn(dispatcher);

        patientController.doGet(request, response);

        verify(request).setAttribute("errorMessage", "No patient found with id 99");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Post_register_withValidData_shouldRegisterPatient() throws Exception {
        Patient created = new Patient("1", "Kasun Silva", "12 Galle Road, Colombo", "0711234567");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(sampleUser());
        when(request.getParameter("name")).thenReturn("Kasun Silva");
        when(request.getParameter("address")).thenReturn("12 Galle Road, Colombo");
        when(request.getParameter("contactNumber")).thenReturn("0711234567");
        when(patientService.registerPatient("Kasun Silva", "12 Galle Road, Colombo", "0711234567"))
                .thenReturn(created);
        when(request.getRequestDispatcher("patientDetails.jsp")).thenReturn(dispatcher);

        patientController.doPost(request, response);

        verify(request).setAttribute("patient", created);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Post_register_withInvalidData_shouldShowError() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(sampleUser());
        when(request.getParameter("name")).thenReturn("");
        when(request.getParameter("address")).thenReturn("12 Galle Road, Colombo");
        when(request.getParameter("contactNumber")).thenReturn("0711234567");
        when(patientService.registerPatient("", "12 Galle Road, Colombo", "0711234567"))
                .thenThrow(new ValidationException("Could not register patient."));
        when(request.getRequestDispatcher("registerPatient.jsp")).thenReturn(dispatcher);

        patientController.doPost(request, response);

        verify(request).setAttribute("errorMessage", "Could not register patient.");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Post_whenNotLoggedIn_shouldRedirectToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        patientController.doPost(request, response);

        verify(response).sendRedirect("login.jsp");
    }

    private Appointment sampleAppointment(Patient patient) {
        Dentist dentist = new Dentist("1", "Dr. Perera", "0711234567");
        TreatmentType treatmentType = new TreatmentType("1", "Root Canal", new BigDecimal("15000.00"));

        return new Appointment.Builder("1")
                .patient(patient).dentist(dentist).treatmentType(treatmentType)
                .appointmentDateTime(LocalDateTime.now().plusDays(1)).bookedByUserId("1").build();
    }

    private User sampleUser() {
        return new User("1", "jdoe", "hashedvalue", UserRole.RECEPTIONIST, "Jane Doe", "0711234567");
    }
}