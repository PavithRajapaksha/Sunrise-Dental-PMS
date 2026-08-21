package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.exception.DoubleBookingException;
import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.Appointment;
import com.sunrise.sunrisedentalpms.model.AppointmentStatus;
import com.sunrise.sunrisedentalpms.model.Dentist;
import com.sunrise.sunrisedentalpms.model.Patient;
import com.sunrise.sunrisedentalpms.model.TreatmentType;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentServiceInterface {

    Appointment bookAppointment(Patient patient, Dentist dentist, TreatmentType treatmentType,
                                LocalDateTime appointmentDateTime, String bookedByUserId)
            throws ValidationException, DoubleBookingException;

    Appointment findAppointment(String appointmentNumber) throws RecordNotFoundException;

    List<Appointment> listAppointmentsForPatient(String patientId);

    List<Appointment> listAllAppointments();

    void updateAppointmentStatus(String appointmentNumber, AppointmentStatus newStatus) throws RecordNotFoundException;
}