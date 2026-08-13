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

    @BeforeEach
    void setUp() {
        patient = Mockito.mock(Patient.class);
        dentist = Mockito.mock(Dentist.class);
        Mockito.when(patient.getName()).thenReturn("Kasun Perera");
        Mockito.when(dentist.getName()).thenReturn("Dr. Silva");
        Mockito.when(dentist.getConsultationFee()).thenReturn(new BigDecimal("1500"));
    }

    @Test
    void buildsValidAppointment() {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(1);

        Appointment appointment = new Appointment.Builder("APT001")
                .patient(patient)
                .dentist(dentist)
                .treatmentType(TreatmentType.FILLING)
                .appointmentDateTime(dateTime)
                .build();

        assertEquals("APT001", appointment.getAppointmentNumber());
        assertEquals(patient, appointment.getPatient());
        assertEquals(dentist, appointment.getDentist());
        assertEquals(TreatmentType.FILLING, appointment.getTreatmentType());
        assertEquals(dateTime, appointment.getAppointmentDateTime());
        assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());
    }

    @Test
    void rejectsNullAppointmentNumber() {
        assertThrows(NullPointerException.class, () ->
                new Appointment.Builder(null)
                        .patient(patient)
                        .dentist(dentist)
                        .treatmentType(TreatmentType.FILLING)
                        .appointmentDateTime(LocalDateTime.now().plusDays(1))
                        .build());
    }

    @Test
    void rejectsNullPatient() {
        assertThrows(NullPointerException.class, () ->
                new Appointment.Builder("APT002")
                        .dentist(dentist)
                        .treatmentType(TreatmentType.FILLING)
                        .appointmentDateTime(LocalDateTime.now().plusDays(1))
                        .build());
    }

    @Test
    void rejectsNullDentist() {
        assertThrows(NullPointerException.class, () ->
                new Appointment.Builder("APT003")
                        .patient(patient)
                        .treatmentType(TreatmentType.FILLING)
                        .appointmentDateTime(LocalDateTime.now().plusDays(1))
                        .build());
    }

    @Test
    void rejectsNullTreatmentType() {
        assertThrows(NullPointerException.class, () ->
                new Appointment.Builder("APT004")
                        .patient(patient)
                        .dentist(dentist)
                        .appointmentDateTime(LocalDateTime.now().plusDays(1))
                        .build());
    }

    @Test
    void rejectsNullAppointmentDateTime() {
        assertThrows(NullPointerException.class, () ->
                new Appointment.Builder("APT005")
                        .patient(patient)
                        .dentist(dentist)
                        .treatmentType(TreatmentType.FILLING)
                        .build());
    }

    @Test
    void defaultsStatusToScheduledWhenNotSet() {
        Appointment appointment = new Appointment.Builder("APT006")
                .patient(patient)
                .dentist(dentist)
                .treatmentType(TreatmentType.CONSULTATION)
                .appointmentDateTime(LocalDateTime.now().plusHours(2))
                .build();

        assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());
    }

    @Test
    void allowsStatusToBeUpdatedAfterCreation() {
        Appointment appointment = new Appointment.Builder("APT007")
                .patient(patient)
                .dentist(dentist)
                .treatmentType(TreatmentType.EXTRACTION)
                .appointmentDateTime(LocalDateTime.now().plusHours(3))
                .build();

        appointment.setStatus(AppointmentStatus.CANCELLED);

        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
    }
}