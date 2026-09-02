package com.sunrise.sunrisedentalpms.controller;

import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.Appointment;
import com.sunrise.sunrisedentalpms.model.Patient;
import com.sunrise.sunrisedentalpms.service.AppointmentServiceInterface;
import com.sunrise.sunrisedentalpms.service.PatientServiceInterface;
import com.sunrise.sunrisedentalpms.service.ServiceFactory;
import com.sunrise.sunrisedentalpms.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/patient")
public class PatientController extends HttpServlet {

    private PatientServiceInterface patientService;
    private AppointmentServiceInterface appointmentService;

    public PatientController() {
    }

    // for testing
    PatientController(PatientServiceInterface patientService, AppointmentServiceInterface appointmentService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
    }

    @Override
    public void init() {
        if (patientService == null) {
            patientService = ServiceFactory.getPatientService();
        }
        if (appointmentService == null) {
            appointmentService = ServiceFactory.getAppointmentService();
        }
    }

    // Show patients
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessionUtil.getLoggedInUser(request) == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String patientId = request.getParameter("patientId");
        String contactNumber = request.getParameter("contactNumber");

        try {
            if (patientId != null && !patientId.isEmpty()) {
                showPatientWithHistory(request, response, patientService.findPatient(patientId));
                return;
            }

            if (contactNumber != null && !contactNumber.isEmpty()) {
                showPatientWithHistory(request, response, patientService.findPatientByContactNumber(contactNumber));
                return;
            }

            List<Patient> patients = patientService.listAllPatients();
            request.setAttribute("patients", patients);
            request.getRequestDispatcher("patientList.jsp").forward(request, response);
        } catch (RecordNotFoundException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("patientList.jsp").forward(request, response);
        }
    }

    // Register new patient
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessionUtil.getLoggedInUser(request) == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String name = request.getParameter("name");
        String address = request.getParameter("address");
        String contactNumber = request.getParameter("contactNumber");

        try {
            Patient created = patientService.registerPatient(name, address, contactNumber);

            request.setAttribute("patient", created);
            request.setAttribute("successMessage", "Patient registered successfully.");
            request.getRequestDispatcher("patientDetails.jsp").forward(request, response);
        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("registerPatient.jsp").forward(request, response);
        }
    }

    // Attach patient and appointment history
    private void showPatientWithHistory(HttpServletRequest request, HttpServletResponse response, Patient patient)
            throws ServletException, IOException {
        List<Appointment> appointments = appointmentService.listAppointmentsForPatient(patient.getPatientId());

        request.setAttribute("patient", patient);
        request.setAttribute("appointments", appointments);
        request.getRequestDispatcher("patientDetails.jsp").forward(request, response);
    }
}