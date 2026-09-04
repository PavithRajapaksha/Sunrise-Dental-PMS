package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.dao.AppointmentDAOInterface;
import com.sunrise.sunrisedentalpms.exception.DoubleBookingException;
import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.Appointment;
import com.sunrise.sunrisedentalpms.model.AppointmentStatus;
import com.sunrise.sunrisedentalpms.model.Dentist;
import com.sunrise.sunrisedentalpms.model.Patient;
import com.sunrise.sunrisedentalpms.model.TreatmentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentDAOInterface appointmentDao;

    @Mock
    private Notifier notifier;

    private AppointmentService appointmentService;
    private Patient samplePatient;
    private Dentist sampleDentist;
    private TreatmentType sampleTreatmentType;
    private LocalDateTime futureDateTime;

    @BeforeEach
    void setUp() {
        appointmentService = new AppointmentService(appointmentDao, notifier);
        samplePatient = new Patient("1", "Kasun Silva", "12 Galle Road, Colombo", "0711234567");
        sampleDentist = new Dentist("1", "Dr. Perera", "0711234567");
        sampleTreatmentType = new TreatmentType("1", "Root Canal", new BigDecimal("15000.00"));
        futureDateTime = LocalDateTime.now().plusDays(1);
    }

    @Test
    void Book_withValidData_shouldReturnAppointment() throws ValidationException, DoubleBookingException {
        Appointment created = new Appointment.Builder("1")
                .patient(samplePatient).dentist(sampleDentist).treatmentType(sampleTreatmentType)
                .appointmentDateTime(futureDateTime).bookedByUserId("1").build();

        when(appointmentDao.findAll()).thenReturn(List.of());
        when(appointmentDao.createAppointment(samplePatient, sampleDentist, sampleTreatmentType, futureDateTime, "1"))
                .thenReturn(created);

        Appointment result = appointmentService.bookAppointment(samplePatient, sampleDentist, sampleTreatmentType, futureDateTime, "1");

        assertEquals(created, result);
    }

    @Test
    void Book_withPastDateTime_shouldFail() {
        LocalDateTime pastDateTime = LocalDateTime.now().minusDays(1);

        assertThrows(ValidationException.class,
                () -> appointmentService.bookAppointment(samplePatient, sampleDentist, sampleTreatmentType, pastDateTime, "1"));

        verify(appointmentDao, never()).createAppointment(any(), any(), any(), any(), anyString());
    }

    @Test
    void Book_withDoubleBookedDentist_shouldFail() {
        Appointment existing = new Appointment.Builder("1")
                .patient(samplePatient).dentist(sampleDentist).treatmentType(sampleTreatmentType)
                .appointmentDateTime(futureDateTime).status(AppointmentStatus.SCHEDULED).bookedByUserId("1").build();

        when(appointmentDao.findAll()).thenReturn(List.of(existing));

        assertThrows(DoubleBookingException.class,
                () -> appointmentService.bookAppointment(samplePatient, sampleDentist, sampleTreatmentType, futureDateTime, "1"));

        verify(appointmentDao, never()).createAppointment(any(), any(), any(), any(), anyString());
    }

    @Test
    void Book_whenDaoReturnsNull_shouldFail() {
        when(appointmentDao.findAll()).thenReturn(List.of());
        when(appointmentDao.createAppointment(samplePatient, sampleDentist, sampleTreatmentType, futureDateTime, "1"))
                .thenReturn(null);

        assertThrows(ValidationException.class,
                () -> appointmentService.bookAppointment(samplePatient, sampleDentist, sampleTreatmentType, futureDateTime, "1"));
    }

    @Test
    void Find_withValidNumber_shouldReturnAppointment() throws RecordNotFoundException {
        Appointment appointment = new Appointment.Builder("1")
                .patient(samplePatient).dentist(sampleDentist).treatmentType(sampleTreatmentType)
                .appointmentDateTime(futureDateTime).bookedByUserId("1").build();

        when(appointmentDao.findByAppointmentNumber("1")).thenReturn(Optional.of(appointment));

        Appointment result = appointmentService.findAppointment("1");

        assertEquals(appointment, result);
    }

    @Test
    void Find_withInvalidNumber_shouldFail() {
        when(appointmentDao.findByAppointmentNumber("99")).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> appointmentService.findAppointment("99"));
    }

    @Test
    void ListForPatient_shouldReturnAppointments() {
        Appointment appointment = new Appointment.Builder("1")
                .patient(samplePatient).dentist(sampleDentist).treatmentType(sampleTreatmentType)
                .appointmentDateTime(futureDateTime).bookedByUserId("1").build();

        when(appointmentDao.findByPatientId("1")).thenReturn(List.of(appointment));

        List<Appointment> result = appointmentService.listAppointmentsForPatient("1");

        assertEquals(1, result.size());
    }

    @Test
    void ListAll_shouldReturnAllAppointments() {
        Appointment appointment = new Appointment.Builder("1")
                .patient(samplePatient).dentist(sampleDentist).treatmentType(sampleTreatmentType)
                .appointmentDateTime(futureDateTime).bookedByUserId("1").build();

        when(appointmentDao.findAll()).thenReturn(List.of(appointment));

        List<Appointment> result = appointmentService.listAllAppointments();

        assertEquals(1, result.size());
    }

    @Test
    void UpdateStatus_withValidNumber_shouldSucceed() {
        when(appointmentDao.updateStatus("1", AppointmentStatus.COMPLETED)).thenReturn(true);

        assertDoesNotThrow(() -> appointmentService.updateAppointmentStatus("1", AppointmentStatus.COMPLETED));
    }

    @Test
    void UpdateStatus_withInvalidNumber_shouldFail() {
        when(appointmentDao.updateStatus("99", AppointmentStatus.COMPLETED)).thenReturn(false);

        assertThrows(RecordNotFoundException.class,
                () -> appointmentService.updateAppointmentStatus("99", AppointmentStatus.COMPLETED));
    }
}