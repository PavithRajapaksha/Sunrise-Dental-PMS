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
import com.sunrise.sunrisedentalpms.model.UserRole;
import com.sunrise.sunrisedentalpms.service.AppointmentServiceInterface;
import com.sunrise.sunrisedentalpms.service.DentistServiceInterface;
import com.sunrise.sunrisedentalpms.service.PatientServiceInterface;
import com.sunrise.sunrisedentalpms.service.TreatmentTypeServiceInterface;
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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    @Mock
    private PatientServiceInterface patientService;

    @Mock
    private DentistServiceInterface dentistService;

    @Mock
    private TreatmentTypeServiceInterface
            treatmentTypeService;

    @Mock
    private AppointmentServiceInterface
            appointmentService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private HttpSession session;

    private AppointmentController
            appointmentController;

    private Patient samplePatient;

    private Dentist sampleDentist;

    private TreatmentType sampleTreatmentType;

    @BeforeEach
    void setUp() {

        appointmentController =
                new AppointmentController(
                        patientService,
                        dentistService,
                        treatmentTypeService,
                        appointmentService
                );

        samplePatient =
                new Patient(
                        "1",
                        "Kasun Silva",
                        "12 Galle Road, Colombo",
                        "0711234567"
                );

        sampleDentist =
                new Dentist(
                        "1",
                        "Dr. Perera",
                        "0711234567"
                );

        sampleTreatmentType =
                new TreatmentType(
                        "1",
                        "Root Canal",
                        new BigDecimal(
                                "15000.00"
                        )
                );
    }

    @Test
    void Get_whenNotLoggedIn_shouldRedirectToLogin()
            throws Exception {

        when(request.getSession(false))
                .thenReturn(null);

        appointmentController.doGet(
                request,
                response
        );

        verify(response)
                .sendRedirect(
                        "login.jsp"
                );
    }

    @Test
    void Get_book_shouldShowBookingForm()
            throws Exception {

        List<Dentist> dentists =
                List.of(
                        sampleDentist
                );

        List<TreatmentType> treatmentTypes =
                List.of(
                        sampleTreatmentType
                );

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute(
                "loggedInUser"
        )).thenReturn(
                sampleUser()
        );

        when(request.getParameter("action"))
                .thenReturn("book");

        when(dentistService
                .listAvailableDentists())
                .thenReturn(
                        dentists
                );

        when(treatmentTypeService
                .listAllTreatmentTypes())
                .thenReturn(
                        treatmentTypes
                );

        when(request.getRequestDispatcher(
                "bookAppointment.jsp"
        )).thenReturn(
                dispatcher
        );

        appointmentController.doGet(
                request,
                response
        );

        verify(request)
                .setAttribute(
                        "dentists",
                        dentists
                );

        verify(request)
                .setAttribute(
                        "treatmentTypes",
                        treatmentTypes
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Get_book_withContactNumber_shouldFindPatient()
            throws Exception {

        List<Dentist> dentists =
                List.of(
                        sampleDentist
                );

        List<TreatmentType> treatmentTypes =
                List.of(
                        sampleTreatmentType
                );

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute(
                "loggedInUser"
        )).thenReturn(
                sampleUser()
        );

        when(request.getParameter(
                "action"
        )).thenReturn(
                "book"
        );

        when(request.getParameter(
                "contactNumber"
        )).thenReturn(
                "0711234567"
        );

        when(patientService
                .findPatientByContactNumber(
                        "0711234567"
                ))
                .thenReturn(
                        samplePatient
                );

        when(dentistService
                .listAvailableDentists())
                .thenReturn(
                        dentists
                );

        when(treatmentTypeService
                .listAllTreatmentTypes())
                .thenReturn(
                        treatmentTypes
                );

        when(request.getRequestDispatcher(
                "bookAppointment.jsp"
        )).thenReturn(
                dispatcher
        );

        appointmentController.doGet(
                request,
                response
        );

        verify(request)
                .setAttribute(
                        "selectedPatient",
                        samplePatient
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Get_book_withUnknownContact_shouldShowError()
            throws Exception {

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute(
                "loggedInUser"
        )).thenReturn(
                sampleUser()
        );

        when(request.getParameter(
                "action"
        )).thenReturn(
                "book"
        );

        when(request.getParameter(
                "contactNumber"
        )).thenReturn(
                "0999999999"
        );

        when(patientService
                .findPatientByContactNumber(
                        "0999999999"
                ))
                .thenThrow(
                        new RecordNotFoundException(
                                "No patient found with contact number 0999999999"
                        )
                );

        when(request.getRequestDispatcher(
                "bookAppointment.jsp"
        )).thenReturn(
                dispatcher
        );

        appointmentController.doGet(
                request,
                response
        );

        verify(request)
                .setAttribute(
                        "errorMessage",
                        "No patient found with contact number 0999999999"
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Get_withAppointmentNumber_shouldReturnAppointment()
            throws Exception {

        Appointment appointment =
                sampleAppointment();

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute(
                "loggedInUser"
        )).thenReturn(
                sampleUser()
        );

        stubAppointmentListGetParameters(
                "1",
                null,
                null
        );

        when(appointmentService
                .findAppointment("1"))
                .thenReturn(
                        appointment
                );

        when(request.getRequestDispatcher(
                "appointmentDetails.jsp"
        )).thenReturn(
                dispatcher
        );

        appointmentController.doGet(
                request,
                response
        );

        verify(request)
                .setAttribute(
                        "appointment",
                        appointment
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Get_withInvalidAppointmentNumber_shouldShowErrorOnList()
            throws Exception {

        List<Appointment> appointments =
                List.of();

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute(
                "loggedInUser"
        )).thenReturn(
                sampleUser()
        );

        stubAppointmentListGetParameters(
                "99",
                null,
                null
        );

        when(appointmentService
                .findAppointment("99"))
                .thenThrow(
                        new RecordNotFoundException(
                                "No appointment found with number 99"
                        )
                );

        when(appointmentService
                .listAllAppointments())
                .thenReturn(
                        appointments
                );

        when(request.getRequestDispatcher(
                "appointmentList.jsp"
        )).thenReturn(
                dispatcher
        );

        appointmentController.doGet(
                request,
                response
        );

        verify(request)
                .setAttribute(
                        "errorMessage",
                        "No appointment found with number 99"
                );

        verify(request)
                .setAttribute(
                        "appointments",
                        appointments
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Get_withPatientId_shouldListAppointmentsForPatient()
            throws Exception {

        List<Appointment> appointments =
                List.of(
                        sampleAppointment()
                );

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute(
                "loggedInUser"
        )).thenReturn(
                sampleUser()
        );

        stubAppointmentListGetParameters(
                null,
                "1",
                null
        );

        when(patientService
                .findPatient("1"))
                .thenReturn(
                        samplePatient
                );

        when(appointmentService
                .listAppointmentsForPatient(
                        "1"
                ))
                .thenReturn(
                        appointments
                );

        when(request.getRequestDispatcher(
                "appointmentList.jsp"
        )).thenReturn(
                dispatcher
        );

        appointmentController.doGet(
                request,
                response
        );

        verify(request)
                .setAttribute(
                        "searchedPatient",
                        samplePatient
                );

        verify(request)
                .setAttribute(
                        "appointments",
                        appointments
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Get_withContactNumber_shouldListAppointmentsForPatient()
            throws Exception {

        List<Appointment> appointments =
                List.of(
                        sampleAppointment()
                );

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute(
                "loggedInUser"
        )).thenReturn(
                sampleUser()
        );

        stubAppointmentListGetParameters(
                null,
                null,
                "0711234567"
        );

        when(patientService
                .findPatientByContactNumber(
                        "0711234567"
                ))
                .thenReturn(
                        samplePatient
                );

        when(appointmentService
                .listAppointmentsForPatient(
                        "1"
                ))
                .thenReturn(
                        appointments
                );

        when(request.getRequestDispatcher(
                "appointmentList.jsp"
        )).thenReturn(
                dispatcher
        );

        appointmentController.doGet(
                request,
                response
        );

        verify(request)
                .setAttribute(
                        "searchedPatient",
                        samplePatient
                );

        verify(request)
                .setAttribute(
                        "appointments",
                        appointments
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Get_withUnknownContactNumber_shouldShowErrorOnList()
            throws Exception {

        List<Appointment> appointments =
                List.of();

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute(
                "loggedInUser"
        )).thenReturn(
                sampleUser()
        );

        stubAppointmentListGetParameters(
                null,
                null,
                "0999999999"
        );

        when(patientService
                .findPatientByContactNumber(
                        "0999999999"
                ))
                .thenThrow(
                        new RecordNotFoundException(
                                "No patient found with contact number 0999999999"
                        )
                );

        when(appointmentService
                .listAllAppointments())
                .thenReturn(
                        appointments
                );

        when(request.getRequestDispatcher(
                "appointmentList.jsp"
        )).thenReturn(
                dispatcher
        );

        appointmentController.doGet(
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
                        "appointments",
                        appointments
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Get_withNoParams_shouldListAllAppointments()
            throws Exception {

        List<Appointment> appointments =
                List.of(
                        sampleAppointment()
                );

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute(
                "loggedInUser"
        )).thenReturn(
                sampleUser()
        );

        when(appointmentService
                .listAllAppointments())
                .thenReturn(
                        appointments
                );

        when(request.getRequestDispatcher(
                "appointmentList.jsp"
        )).thenReturn(
                dispatcher
        );

        appointmentController.doGet(
                request,
                response
        );

        verify(request)
                .setAttribute(
                        "appointments",
                        appointments
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Post_book_withValidData_shouldBookAppointment()
            throws Exception {

        LocalDateTime appointmentDateTime =
                LocalDateTime.now()
                        .plusDays(1)
                        .withHour(10)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0);

        Appointment created =
                new Appointment.Builder("1")
                        .patient(
                                samplePatient
                        )
                        .dentist(
                                sampleDentist
                        )
                        .treatmentType(
                                sampleTreatmentType
                        )
                        .appointmentDateTime(
                                appointmentDateTime
                        )
                        .bookedByUserId(
                                "1"
                        )
                        .build();

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute(
                "loggedInUser"
        )).thenReturn(
                sampleUser()
        );

        when(request.getParameter(
                "action"
        )).thenReturn(
                "book"
        );

        when(request.getParameter(
                "patientId"
        )).thenReturn(
                "1"
        );

        when(request.getParameter(
                "dentistId"
        )).thenReturn(
                "1"
        );

        when(request.getParameter(
                "treatmentTypeId"
        )).thenReturn(
                "1"
        );

        when(request.getParameter(
                "appointmentDate"
        )).thenReturn(
                appointmentDateTime
                        .toLocalDate()
                        .toString()
        );

        when(request.getParameter(
                "appointmentTime"
        )).thenReturn(
                appointmentDateTime
                        .toLocalTime()
                        .toString()
        );

        when(patientService
                .findPatient("1"))
                .thenReturn(
                        samplePatient
                );

        when(dentistService
                .findDentist("1"))
                .thenReturn(
                        sampleDentist
                );

        when(treatmentTypeService
                .findTreatmentType("1"))
                .thenReturn(
                        sampleTreatmentType
                );

        when(appointmentService
                .bookAppointment(
                        samplePatient,
                        sampleDentist,
                        sampleTreatmentType,
                        appointmentDateTime,
                        "1"
                ))
                .thenReturn(
                        created
                );

        when(request.getRequestDispatcher(
                "appointmentDetails.jsp"
        )).thenReturn(
                dispatcher
        );

        appointmentController.doPost(
                request,
                response
        );

        verify(request)
                .setAttribute(
                        "appointment",
                        created
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Post_book_withInvalidDate_shouldShowError()
            throws Exception {

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute(
                "loggedInUser"
        )).thenReturn(
                sampleUser()
        );

        when(request.getParameter(
                "action"
        )).thenReturn(
                "book"
        );

        when(request.getParameter(
                "patientId"
        )).thenReturn(
                "1"
        );

        when(request.getParameter(
                "dentistId"
        )).thenReturn(
                "1"
        );

        when(request.getParameter(
                "treatmentTypeId"
        )).thenReturn(
                "1"
        );

        when(request.getParameter(
                "appointmentDate"
        )).thenReturn(
                "not-a-date"
        );

        when(request.getParameter(
                "appointmentTime"
        )).thenReturn(
                "10:00"
        );

        when(patientService
                .findPatient("1"))
                .thenReturn(
                        samplePatient
                );

        when(dentistService
                .findDentist("1"))
                .thenReturn(
                        sampleDentist
                );

        when(treatmentTypeService
                .findTreatmentType("1"))
                .thenReturn(
                        sampleTreatmentType
                );

        when(request.getRequestDispatcher(
                "bookAppointment.jsp"
        )).thenReturn(
                dispatcher
        );

        appointmentController.doPost(
                request,
                response
        );

        verify(request)
                .setAttribute(
                        "errorMessage",
                        "Invalid appointment date or time."
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Post_book_withDoubleBooking_shouldShowError()
            throws Exception {

        LocalDateTime appointmentDateTime =
                LocalDateTime.now()
                        .plusDays(1)
                        .withHour(10)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0);

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute(
                "loggedInUser"
        )).thenReturn(
                sampleUser()
        );

        when(request.getParameter(
                "action"
        )).thenReturn(
                "book"
        );

        when(request.getParameter(
                "patientId"
        )).thenReturn(
                "1"
        );

        when(request.getParameter(
                "dentistId"
        )).thenReturn(
                "1"
        );

        when(request.getParameter(
                "treatmentTypeId"
        )).thenReturn(
                "1"
        );

        when(request.getParameter(
                "appointmentDate"
        )).thenReturn(
                appointmentDateTime
                        .toLocalDate()
                        .toString()
        );

        when(request.getParameter(
                "appointmentTime"
        )).thenReturn(
                appointmentDateTime
                        .toLocalTime()
                        .toString()
        );

        when(patientService
                .findPatient("1"))
                .thenReturn(
                        samplePatient
                );

        when(dentistService
                .findDentist("1"))
                .thenReturn(
                        sampleDentist
                );

        when(treatmentTypeService
                .findTreatmentType("1"))
                .thenReturn(
                        sampleTreatmentType
                );

        when(appointmentService
                .bookAppointment(
                        samplePatient,
                        sampleDentist,
                        sampleTreatmentType,
                        appointmentDateTime,
                        "1"
                ))
                .thenThrow(
                        new DoubleBookingException(
                                "Dentist Dr. Perera already has an appointment at "
                                        + appointmentDateTime
                        )
                );

        when(request.getRequestDispatcher(
                "bookAppointment.jsp"
        )).thenReturn(
                dispatcher
        );

        appointmentController.doPost(
                request,
                response
        );

        verify(request)
                .setAttribute(
                        "errorMessage",
                        "Dentist Dr. Perera already has an appointment at "
                                + appointmentDateTime
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Post_cancelScheduledAppointment_shouldCancel()
            throws Exception {

        Appointment cancelledAppointment =
                sampleAppointment(
                        AppointmentStatus.CANCELLED
                );

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute(
                "loggedInUser"
        )).thenReturn(
                sampleUser()
        );

        when(request.getParameter(
                "action"
        )).thenReturn(
                "cancel"
        );

        when(request.getParameter(
                "appointmentNumber"
        )).thenReturn(
                "1"
        );

        when(appointmentService
                .findAppointment("1"))
                .thenReturn(
                        cancelledAppointment
                );

        when(request.getRequestDispatcher(
                "appointmentDetails.jsp"
        )).thenReturn(
                dispatcher
        );

        appointmentController.doPost(
                request,
                response
        );

        verify(appointmentService)
                .updateAppointmentStatus(
                        "1",
                        AppointmentStatus.CANCELLED
                );

        verify(request)
                .setAttribute(
                        "appointment",
                        cancelledAppointment
                );

        verify(request)
                .setAttribute(
                        "successMessage",
                        "Appointment cancelled successfully."
                );

        verify(dispatcher)
                .forward(
                        request,
                        response
                );
    }

    @Test
    void Post_cancelCompletedAppointment_shouldShowError()
            throws Exception {

        Appointment completedAppointment =
                sampleAppointment(
                        AppointmentStatus.COMPLETED
                );

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute(
                "loggedInUser"
        )).thenReturn(
                sampleUser()
        );

        when(request.getParameter(
                "action"
        )).thenReturn(
                "cancel"
        );

        when(request.getParameter(
                "appointmentNumber"
        )).thenReturn(
                "1"
        );

        doThrow(
                new ValidationException(
                        "Completed appointments cannot be changed."
                )
        ).when(
                appointmentService
        ).updateAppointmentStatus(
                "1",
                AppointmentStatus.CANCELLED
        );

        when(appointmentService
                .findAppointment("1"))
                .thenReturn(
                        completedAppointment
                );

        when(request.getRequestDispatcher(
                "appointmentDetails.jsp"
        )).thenReturn(
                dispatcher
        );

        appointmentController.doPost(
                request,
                response
        );

        verify(request)
                .setAttribute(
                        "errorMessage",
                        "Completed appointments cannot be changed."
                );

        verify(request)
                .setAttribute(
                        "appointment",
                        completedAppointment
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

        appointmentController.doPost(
                request,
                response
        );

        verify(response)
                .sendRedirect(
                        "login.jsp"
                );
    }

    private void stubAppointmentListGetParameters(
            String appointmentNumber,
            String patientId,
            String contactNumber) {

        doReturn(null)
                .when(request)
                .getParameter("action");

        doReturn(appointmentNumber)
                .when(request)
                .getParameter("appointmentNumber");

        doReturn(patientId)
                .when(request)
                .getParameter("patientId");

        doReturn(contactNumber)
                .when(request)
                .getParameter("contactNumber");
    }

    private Appointment sampleAppointment() {

        return sampleAppointment(
                AppointmentStatus.SCHEDULED
        );
    }

    private Appointment sampleAppointment(
            AppointmentStatus status) {

        return new Appointment.Builder("1")
                .patient(
                        samplePatient
                )
                .dentist(
                        sampleDentist
                )
                .treatmentType(
                        sampleTreatmentType
                )
                .appointmentDateTime(
                        LocalDateTime.now()
                                .plusDays(1)
                )
                .status(
                        status
                )
                .bookedByUserId(
                        "1"
                )
                .build();
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