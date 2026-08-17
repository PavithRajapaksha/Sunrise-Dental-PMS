package com.sunrise.sunrisedentalpms.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentTest {

    private Patient patient;
    private Dentist dentist;
    private TreatmentType treatmentType;

    @BeforeEach
    void setUp() {
        patient = Mockito.mock(Patient.class);
        dentist = Mockito.mock(Dentist.class);
        Mockito.when(patient.getName()).thenReturn("Kasun Perera");
        Mockito.when(dentist.getName()).thenReturn("Dr. Silva");

        treatmentType = new TreatmentType("1", "Filling", new BigDecimal("5000"));
    }

    @Test
    void buildsValidAppointment() {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(1);

        Appointment appointment = new Appointment.Builder("APT001")
                .patient(patient)
                .dentist(dentist)
                .treatmentType(treatmentType)
                .appointmentDateTime(dateTime)
                .bookedByUserId("1")
                .build();

        assertEquals("APT001", appointment.getAppointmentNumber());
        assertEquals(patient, appointment.getPatient());
        assertEquals(dentist, appointment.getDentist());
        assertEquals(treatmentType, appointment.getTreatmentType());
        assertEquals(dateTime, appointment.getAppointmentDateTime());
        assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());
        assertEquals("1", appointment.getBookedByUserId());
    }

    @Test
    void rejectsNullAppointmentNumber() {
        assertThrows(NullPointerException.class, () ->
                new Appointment.Builder(null)
                        .patient(patient)
                        .dentist(dentist)
                        .treatmentType(treatmentType)
                        .appointmentDateTime(LocalDateTime.now().plusDays(1))
                        .bookedByUserId("1")
                        .build());
    }

    @Test
    void rejectsNullPatient() {
        assertThrows(NullPointerException.class, () ->
                new Appointment.Builder("APT002")
                        .dentist(dentist)
                        .treatmentType(treatmentType)
                        .appointmentDateTime(LocalDateTime.now().plusDays(1))
                        .bookedByUserId("1")
                        .build());
    }

    @Test
    void rejectsNullDentist() {
        assertThrows(NullPointerException.class, () ->
                new Appointment.Builder("APT003")
                        .patient(patient)
                        .treatmentType(treatmentType)
                        .appointmentDateTime(LocalDateTime.now().plusDays(1))
                        .bookedByUserId("1")
                        .build());
    }

    @Test
    void rejectsNullTreatmentType() {
        assertThrows(NullPointerException.class, () ->
                new Appointment.Builder("APT004")
                        .patient(patient)
                        .dentist(dentist)
                        .appointmentDateTime(LocalDateTime.now().plusDays(1))
                        .bookedByUserId("1")
                        .build());
    }

    @Test
    void rejectsNullAppointmentDateTime() {
        assertThrows(NullPointerException.class, () ->
                new Appointment.Builder("APT005")
                        .patient(patient)
                        .dentist(dentist)
                        .treatmentType(treatmentType)
                        .bookedByUserId("1")
                        .build());
    }

    @Test
    void rejectsNullBookedByUserId() {
        assertThrows(NullPointerException.class, () ->
                new Appointment.Builder("APT008")
                        .patient(patient)
                        .dentist(dentist)
                        .treatmentType(treatmentType)
                        .appointmentDateTime(LocalDateTime.now().plusHours(1))
                        .build());
    }

    @Test
    void defaultsStatusToScheduledWhenNotSet() {
        Appointment appointment = new Appointment.Builder("APT006")
                .patient(patient)
                .dentist(dentist)
                .treatmentType(treatmentType)
                .appointmentDateTime(LocalDateTime.now().plusHours(2))
                .bookedByUserId("1")
                .build();

        assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());
    }

    @Test
    void allowsStatusToBeUpdatedAfterCreation() {
        Appointment appointment = new Appointment.Builder("APT007")
                .patient(patient)
                .dentist(dentist)
                .treatmentType(treatmentType)
                .appointmentDateTime(LocalDateTime.now().plusHours(3))
                .bookedByUserId("1")
                .build();

        appointment.setStatus(AppointmentStatus.CANCELLED);

        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
    }
}