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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AppointmentService implements AppointmentServiceInterface {

    private final AppointmentDAOInterface appointmentDao;

    public AppointmentService(AppointmentDAOInterface appointmentDao) {
        this.appointmentDao = Objects.requireNonNull(appointmentDao, "AppointmentDAOInterface cannot be null");
    }

    // Books a new appointment, rejecting past dates and double-booked dentists
    @Override
    public Appointment bookAppointment(Patient patient, Dentist dentist, TreatmentType treatmentType,
                                       LocalDateTime appointmentDateTime, String bookedByUserId)
            throws ValidationException, DoubleBookingException {

        if (appointmentDateTime != null && appointmentDateTime.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Cannot book an appointment in the past.");
        }

        if (dentist != null && appointmentDateTime != null && isDentistDoubleBooked(dentist, appointmentDateTime)) {
            throw new DoubleBookingException("Dentist " + dentist.getName() + " already has an appointment at " + appointmentDateTime);
        }

        Appointment created = appointmentDao.createAppointment(patient, dentist, treatmentType, appointmentDateTime, bookedByUserId);

        if (created == null) {
            throw new ValidationException("Could not book appointment. Check the appointment details.");
        }

        return created;
    }

    // Finds an appointment by its appointment number
    @Override
    public Appointment findAppointment(String appointmentNumber) throws RecordNotFoundException {
        Optional<Appointment> appointment = appointmentDao.findByAppointmentNumber(appointmentNumber);
        return appointment.orElseThrow(() -> new RecordNotFoundException("No appointment found with number " + appointmentNumber));
    }

    // Lists every appointment booked for a patient
    @Override
    public List<Appointment> listAppointmentsForPatient(String patientId) {
        return appointmentDao.findByPatientId(patientId);
    }

    // Lists every appointment in the system
    @Override
    public List<Appointment> listAllAppointments() {
        return appointmentDao.findAll();
    }

    // Updates an appointment's status
    @Override
    public void updateAppointmentStatus(String appointmentNumber, AppointmentStatus newStatus) throws RecordNotFoundException {
        boolean updated = appointmentDao.updateStatus(appointmentNumber, newStatus);

        if (!updated) {
            throw new RecordNotFoundException("No appointment found with number " + appointmentNumber);
        }
    }

    // Checks whether the dentist already has an active appointment at the given date and time
    private boolean isDentistDoubleBooked(Dentist dentist, LocalDateTime appointmentDateTime) {
        return appointmentDao.findAll().stream()
                .filter(appointment -> appointment.getStatus() != AppointmentStatus.CANCELLED)
                .anyMatch(appointment ->
                        appointment.getDentist().getDentistId().equals(dentist.getDentistId())
                                && appointment.getAppointmentDateTime().equals(appointmentDateTime));
    }
}