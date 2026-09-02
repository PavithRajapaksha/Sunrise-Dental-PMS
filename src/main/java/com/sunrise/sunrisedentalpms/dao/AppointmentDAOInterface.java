package com.sunrise.sunrisedentalpms.dao;

import com.sunrise.sunrisedentalpms.model.Appointment;
import com.sunrise.sunrisedentalpms.model.AppointmentStatus;
import com.sunrise.sunrisedentalpms.model.Dentist;
import com.sunrise.sunrisedentalpms.model.Patient;
import com.sunrise.sunrisedentalpms.model.TreatmentType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentDAOInterface {

    Appointment createAppointment(Patient patient, Dentist dentist, TreatmentType treatmentType,
                                  LocalDateTime appointmentDateTime, String bookedByUserId);

    Optional<Appointment> findByAppointmentNumber(String appointmentNumber);

    List<Appointment> findByPatientId(String patientId);

    List<Appointment> findAll();

    boolean updateStatus(String appointmentNumber, AppointmentStatus newStatus);
}