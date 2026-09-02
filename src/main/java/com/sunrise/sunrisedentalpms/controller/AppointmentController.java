package com.sunrise.sunrisedentalpms.controller;

import com.sunrise.sunrisedentalpms.exception.DoubleBookingException;
import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.Appointment;
import com.sunrise.sunrisedentalpms.model.AppointmentStatus;
import com.sunrise.sunrisedentalpms.model.Dentist;
import com.sunrise.sunrisedentalpms.model.Patient;
import com.sunrise.sunrisedentalpms.model.TreatmentType;
import com.sunrise.sunrisedentalpms.model.User;
import com.sunrise.sunrisedentalpms.service.AppointmentServiceInterface;
import com.sunrise.sunrisedentalpms.service.DentistServiceInterface;
import com.sunrise.sunrisedentalpms.service.PatientServiceInterface;
import com.sunrise.sunrisedentalpms.service.ServiceFactory;
import com.sunrise.sunrisedentalpms.service.TreatmentTypeServiceInterface;
import com.sunrise.sunrisedentalpms.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@WebServlet("/appointment")
public class AppointmentController extends HttpServlet {

    private PatientServiceInterface patientService;
    private DentistServiceInterface dentistService;
    private TreatmentTypeServiceInterface treatmentTypeService;
    private AppointmentServiceInterface appointmentService;

    public AppointmentController() {
    }

    // for testing
    AppointmentController(PatientServiceInterface patientService, DentistServiceInterface dentistService,
                          TreatmentTypeServiceInterface treatmentTypeService, AppointmentServiceInterface appointmentService) {
        this.patientService = patientService;
        this.dentistService = dentistService;
        this.treatmentTypeService = treatmentTypeService;
        this.appointmentService = appointmentService;
    }

    @Override
    public void init() {
        if (patientService == null) {
            patientService = ServiceFactory.getPatientService();
        }
        if (dentistService == null) {
            dentistService = ServiceFactory.getDentistService();
        }
        if (treatmentTypeService == null) {
            treatmentTypeService = ServiceFactory.getTreatmentTypeService();
        }
        if (appointmentService == null) {
            appointmentService = ServiceFactory.getAppointmentService();
        }
    }

    // Show appointments
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessionUtil.getLoggedInUser(request) == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String appointmentNumber = request.getParameter("appointmentNumber");
        String patientId = request.getParameter("patientId");

        try {
            if (appointmentNumber != null && !appointmentNumber.isEmpty()) {
                Appointment appointment = appointmentService.findAppointment(appointmentNumber);
                request.setAttribute("appointment", appointment);
                request.getRequestDispatcher("appointmentDetails.jsp").forward(request, response);
                return;
            }

            if (patientId != null && !patientId.isEmpty()) {
                List<Appointment> appointments = appointmentService.listAppointmentsForPatient(patientId);
                request.setAttribute("appointments", appointments);
                request.getRequestDispatcher("appointmentList.jsp").forward(request, response);
                return;
            }

            List<Appointment> appointments = appointmentService.listAllAppointments();
            request.setAttribute("appointments", appointments);
            request.getRequestDispatcher("appointmentList.jsp").forward(request, response);
        } catch (RecordNotFoundException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("appointmentList.jsp").forward(request, response);
        }
    }

    // Book appointment or update status
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loggedInUser = SessionUtil.getLoggedInUser(request);

        if (loggedInUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if ("updateStatus".equals(action)) {
            handleUpdateStatus(request, response);
        } else {
            handleBook(request, response, loggedInUser);
        }
    }

    // Book new appointment
    private void handleBook(HttpServletRequest request, HttpServletResponse response, User loggedInUser)
            throws ServletException, IOException {
        String patientId = request.getParameter("patientId");
        String dentistId = request.getParameter("dentistId");
        String treatmentTypeId = request.getParameter("treatmentTypeId");
        String dateText = request.getParameter("appointmentDate");
        String timeText = request.getParameter("appointmentTime");

        try {
            Patient patient = patientService.findPatient(patientId);
            Dentist dentist = dentistService.findDentist(dentistId);
            TreatmentType treatmentType = treatmentTypeService.findTreatmentType(treatmentTypeId);

            LocalDateTime appointmentDateTime = LocalDateTime.of(LocalDate.parse(dateText), LocalTime.parse(timeText));

            Appointment created = appointmentService.bookAppointment(
                    patient, dentist, treatmentType, appointmentDateTime, loggedInUser.getUserId());

            request.setAttribute("appointment", created);
            request.setAttribute("successMessage", "Appointment booked successfully.");
            request.getRequestDispatcher("appointmentDetails.jsp").forward(request, response);
        } catch (DateTimeParseException e) {
            request.setAttribute("errorMessage", "Invalid appointment date or time.");
            request.getRequestDispatcher("registerAppointment.jsp").forward(request, response);
        } catch (RecordNotFoundException | ValidationException | DoubleBookingException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("registerAppointment.jsp").forward(request, response);
        }
    }

    // Update appointment status
    private void handleUpdateStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String appointmentNumber = request.getParameter("appointmentNumber");
        String statusText = request.getParameter("status");

        if (statusText == null || statusText.isEmpty()) {
            request.setAttribute("errorMessage", "Invalid appointment status.");
            request.getRequestDispatcher("appointmentList.jsp").forward(request, response);
            return;
        }

        try {
            AppointmentStatus newStatus = AppointmentStatus.valueOf(statusText);
            appointmentService.updateAppointmentStatus(appointmentNumber, newStatus);

            request.setAttribute("successMessage", "Appointment status updated successfully.");
            request.getRequestDispatcher("appointmentList.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Invalid appointment status.");
            request.getRequestDispatcher("appointmentList.jsp").forward(request, response);
        } catch (RecordNotFoundException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("appointmentList.jsp").forward(request, response);
        }
    }
}