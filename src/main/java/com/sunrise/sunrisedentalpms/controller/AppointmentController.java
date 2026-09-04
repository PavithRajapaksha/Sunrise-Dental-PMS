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

    private TreatmentTypeServiceInterface
            treatmentTypeService;

    private AppointmentServiceInterface
            appointmentService;

    public AppointmentController() {
    }

    // For testing
    AppointmentController(
            PatientServiceInterface patientService,
            DentistServiceInterface dentistService,
            TreatmentTypeServiceInterface treatmentTypeService,
            AppointmentServiceInterface appointmentService) {

        this.patientService =
                patientService;

        this.dentistService =
                dentistService;

        this.treatmentTypeService =
                treatmentTypeService;

        this.appointmentService =
                appointmentService;
    }

    @Override
    public void init() {

        if (patientService == null) {

            patientService =
                    ServiceFactory
                            .getPatientService();
        }

        if (dentistService == null) {

            dentistService =
                    ServiceFactory
                            .getDentistService();
        }

        if (treatmentTypeService == null) {

            treatmentTypeService =
                    ServiceFactory
                            .getTreatmentTypeService();
        }

        if (appointmentService == null) {

            appointmentService =
                    ServiceFactory
                            .getAppointmentService();
        }
    }

    // Show appointments
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

        String action =
                request.getParameter(
                        "action"
                );

        if ("book".equals(action)) {

            showBookingForm(
                    request,
                    response
            );

            return;
        }

        String appointmentNumber =
                request.getParameter(
                        "appointmentNumber"
                );

        String patientId =
                request.getParameter(
                        "patientId"
                );

        String contactNumber =
                request.getParameter(
                        "contactNumber"
                );

        try {

            /*
             * Find one appointment by number.
             */
            if (appointmentNumber != null
                    && !appointmentNumber
                    .trim()
                    .isEmpty()) {

                Appointment appointment =
                        appointmentService
                                .findAppointment(
                                        appointmentNumber
                                                .trim()
                                );

                request.setAttribute(
                        "appointment",
                        appointment
                );

                request.getRequestDispatcher(
                        "appointmentDetails.jsp"
                ).forward(
                        request,
                        response
                );

                return;
            }

            /*
             * Search appointments by patient ID.
             */
            if (patientId != null
                    && !patientId
                    .trim()
                    .isEmpty()) {

                Patient patient =
                        patientService
                                .findPatient(
                                        patientId
                                                .trim()
                                );

                showAppointmentsForPatient(
                        request,
                        response,
                        patient
                );

                return;
            }

            /*
             * Search appointments using
             * patient's contact number.
             */
            if (contactNumber != null
                    && !contactNumber
                    .trim()
                    .isEmpty()) {

                Patient patient =
                        patientService
                                .findPatientByContactNumber(
                                        contactNumber
                                                .trim()
                                );

                showAppointmentsForPatient(
                        request,
                        response,
                        patient
                );

                return;
            }

            /*
             * No search parameters:
             * show every appointment.
             */
            List<Appointment> appointments =
                    appointmentService
                            .listAllAppointments();

            request.setAttribute(
                    "appointments",
                    appointments
            );

            request.getRequestDispatcher(
                    "appointmentList.jsp"
            ).forward(
                    request,
                    response
            );

        } catch (RecordNotFoundException e) {

            request.setAttribute(
                    "errorMessage",
                    e.getMessage()
            );

            request.setAttribute(
                    "appointments",
                    appointmentService
                            .listAllAppointments()
            );

            request.getRequestDispatcher(
                    "appointmentList.jsp"
            ).forward(
                    request,
                    response
            );
        }
    }

    /*
     * Book an appointment or cancel
     * a scheduled appointment.
     */
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

        String action =
                request.getParameter(
                        "action"
                );

        if ("cancel".equals(action)) {

            handleCancel(
                    request,
                    response
            );

        } else {

            handleBook(
                    request,
                    response,
                    loggedInUser
            );
        }
    }

    private void showAppointmentsForPatient(
            HttpServletRequest request,
            HttpServletResponse response,
            Patient patient)
            throws ServletException,
            IOException {

        List<Appointment> appointments =
                appointmentService
                        .listAppointmentsForPatient(
                                patient.getPatientId()
                        );

        request.setAttribute(
                "searchedPatient",
                patient
        );

        request.setAttribute(
                "appointments",
                appointments
        );

        request.getRequestDispatcher(
                "appointmentList.jsp"
        ).forward(
                request,
                response
        );
    }

    private void showBookingForm(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException,
            IOException {

        prepareBookingOptions(
                request
        );

        String contactNumber =
                request.getParameter(
                        "contactNumber"
                );

        String patientId =
                request.getParameter(
                        "patientId"
                );

        try {

            if (contactNumber != null
                    && !contactNumber
                    .trim()
                    .isEmpty()) {

                Patient patient =
                        patientService
                                .findPatientByContactNumber(
                                        contactNumber
                                                .trim()
                                );

                request.setAttribute(
                        "selectedPatient",
                        patient
                );

            } else if (patientId != null
                    && !patientId
                    .trim()
                    .isEmpty()) {

                Patient patient =
                        patientService
                                .findPatient(
                                        patientId
                                                .trim()
                                );

                request.setAttribute(
                        "selectedPatient",
                        patient
                );
            }

        } catch (RecordNotFoundException e) {

            request.setAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        request.getRequestDispatcher(
                "bookAppointment.jsp"
        ).forward(
                request,
                response
        );
    }

    private void prepareBookingOptions(
            HttpServletRequest request) {

        List<Dentist> dentists =
                dentistService
                        .listAvailableDentists();

        List<TreatmentType> treatmentTypes =
                treatmentTypeService
                        .listAllTreatmentTypes();

        request.setAttribute(
                "dentists",
                dentists
        );

        request.setAttribute(
                "treatmentTypes",
                treatmentTypes
        );
    }

    // Book new appointment
    private void handleBook(
            HttpServletRequest request,
            HttpServletResponse response,
            User loggedInUser)
            throws ServletException,
            IOException {

        String patientId =
                request.getParameter(
                        "patientId"
                );

        String dentistId =
                request.getParameter(
                        "dentistId"
                );

        String treatmentTypeId =
                request.getParameter(
                        "treatmentTypeId"
                );

        String dateText =
                request.getParameter(
                        "appointmentDate"
                );

        String timeText =
                request.getParameter(
                        "appointmentTime"
                );

        Patient patient = null;

        try {

            patient =
                    patientService
                            .findPatient(
                                    patientId
                            );

            request.setAttribute(
                    "selectedPatient",
                    patient
            );

            Dentist dentist =
                    dentistService
                            .findDentist(
                                    dentistId
                            );

            TreatmentType treatmentType =
                    treatmentTypeService
                            .findTreatmentType(
                                    treatmentTypeId
                            );

            LocalDateTime appointmentDateTime =
                    LocalDateTime.of(
                            LocalDate.parse(
                                    dateText
                            ),
                            LocalTime.parse(
                                    timeText
                            )
                    );

            Appointment created =
                    appointmentService
                            .bookAppointment(
                                    patient,
                                    dentist,
                                    treatmentType,
                                    appointmentDateTime,
                                    loggedInUser
                                            .getUserId()
                            );

            request.setAttribute(
                    "appointment",
                    created
            );

            request.setAttribute(
                    "successMessage",
                    "Appointment booked successfully."
            );

            request.getRequestDispatcher(
                    "appointmentDetails.jsp"
            ).forward(
                    request,
                    response
            );

        } catch (DateTimeParseException e) {

            prepareBookingOptions(
                    request
            );

            request.setAttribute(
                    "selectedPatient",
                    patient
            );

            request.setAttribute(
                    "errorMessage",
                    "Invalid appointment date or time."
            );

            request.getRequestDispatcher(
                    "bookAppointment.jsp"
            ).forward(
                    request,
                    response
            );

        } catch (RecordNotFoundException
                 | ValidationException
                 | DoubleBookingException e) {

            prepareBookingOptions(
                    request
            );

            request.setAttribute(
                    "selectedPatient",
                    patient
            );

            request.setAttribute(
                    "errorMessage",
                    e.getMessage()
            );

            request.getRequestDispatcher(
                    "bookAppointment.jsp"
            ).forward(
                    request,
                    response
            );
        }
    }

    /*
     * Cancel appointment.
     *
     * Notice that no "status" parameter is read
     * from the browser. A cancel request always
     * means AppointmentStatus.CANCELLED.
     */
    private void handleCancel(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException,
            IOException {

        String appointmentNumber =
                request.getParameter(
                        "appointmentNumber"
                );

        if (appointmentNumber == null
                || appointmentNumber
                .trim()
                .isEmpty()) {

            showAppointmentListError(
                    request,
                    response,
                    "Invalid appointment number."
            );

            return;
        }

        String cleanedAppointmentNumber =
                appointmentNumber.trim();

        try {

            appointmentService
                    .updateAppointmentStatus(
                            cleanedAppointmentNumber,
                            AppointmentStatus.CANCELLED
                    );

            /*
             * Reload from the database so that
             * appointmentDetails.jsp immediately
             * receives status CANCELLED.
             */
            Appointment appointment =
                    appointmentService
                            .findAppointment(
                                    cleanedAppointmentNumber
                            );

            request.setAttribute(
                    "appointment",
                    appointment
            );

            request.setAttribute(
                    "successMessage",
                    "Appointment cancelled successfully."
            );

            request.getRequestDispatcher(
                    "appointmentDetails.jsp"
            ).forward(
                    request,
                    response
            );

        } catch (ValidationException e) {

            /*
             * Examples:
             *
             * - appointment already completed
             * - appointment already cancelled
             */
            request.setAttribute(
                    "errorMessage",
                    e.getMessage()
            );

            try {

                Appointment appointment =
                        appointmentService
                                .findAppointment(
                                        cleanedAppointmentNumber
                                );

                request.setAttribute(
                        "appointment",
                        appointment
                );

                request.getRequestDispatcher(
                        "appointmentDetails.jsp"
                ).forward(
                        request,
                        response
                );

            } catch (RecordNotFoundException ex) {

                showAppointmentListError(
                        request,
                        response,
                        ex.getMessage()
                );
            }

        } catch (RecordNotFoundException e) {

            showAppointmentListError(
                    request,
                    response,
                    e.getMessage()
            );
        }
    }

    /*
     * Common error helper used when an appointment
     * cannot be loaded.
     */
    private void showAppointmentListError(
            HttpServletRequest request,
            HttpServletResponse response,
            String message)
            throws ServletException,
            IOException {

        request.setAttribute(
                "errorMessage",
                message
        );

        request.setAttribute(
                "appointments",
                appointmentService
                        .listAllAppointments()
        );

        request.getRequestDispatcher(
                "appointmentList.jsp"
        ).forward(
                request,
                response
        );
    }
}