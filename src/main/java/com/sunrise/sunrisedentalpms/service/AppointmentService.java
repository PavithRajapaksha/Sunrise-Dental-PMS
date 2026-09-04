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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AppointmentService
        implements AppointmentServiceInterface {

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern(
                    "dd MMM yyyy, hh:mm a"
            );

    private final AppointmentDAOInterface appointmentDao;
    private final Notifier notifier;

    public AppointmentService(
            AppointmentDAOInterface appointmentDao,
            Notifier notifier) {

        this.appointmentDao =
                Objects.requireNonNull(
                        appointmentDao,
                        "AppointmentDAOInterface cannot be null"
                );

        this.notifier =
                Objects.requireNonNull(
                        notifier,
                        "Notifier cannot be null"
                );
    }

    // Book a new appointment
    @Override
    public Appointment bookAppointment(
            Patient patient,
            Dentist dentist,
            TreatmentType treatmentType,
            LocalDateTime appointmentDateTime,
            String bookedByUserId)
            throws ValidationException,
            DoubleBookingException {

        if (appointmentDateTime != null
                && appointmentDateTime
                .isBefore(LocalDateTime.now())) {

            throw new ValidationException(
                    "Cannot book an appointment in the past."
            );
        }

        if (dentist != null
                && appointmentDateTime != null
                && isDentistDoubleBooked(
                dentist,
                appointmentDateTime
        )) {

            throw new DoubleBookingException(
                    "Dentist "
                            + dentist.getName()
                            + " already has an appointment at "
                            + appointmentDateTime
            );
        }

        Appointment created =
                appointmentDao.createAppointment(
                        patient,
                        dentist,
                        treatmentType,
                        appointmentDateTime,
                        bookedByUserId
                );

        if (created == null) {

            throw new ValidationException(
                    "Could not book appointment. "
                            + "Check the appointment details."
            );
        }

        notifier.publish(
                patient.getEmail(),
                "Your appointment #"
                        + created.getAppointmentNumber()
                        + " with Dr. "
                        + dentist.getName()
                        + " is confirmed for "
                        + appointmentDateTime
                        .format(DATE_TIME_FORMAT)
                        + ".",
                created.getAppointmentNumber()
        );

        notifier.publish(
                dentist.getEmail(),
                "A new appointment #"
                        + created.getAppointmentNumber()
                        + " has been booked with "
                        + patient.getName()
                        + " for "
                        + appointmentDateTime
                        .format(DATE_TIME_FORMAT)
                        + ".",
                created.getAppointmentNumber()
        );

        return created;
    }

    // Find appointment by number
    @Override
    public Appointment findAppointment(
            String appointmentNumber)
            throws RecordNotFoundException {

        Optional<Appointment> appointment =
                appointmentDao
                        .findByAppointmentNumber(
                                appointmentNumber
                        );

        return appointment.orElseThrow(
                () -> new RecordNotFoundException(
                        "No appointment found with number "
                                + appointmentNumber
                )
        );
    }

    // List appointments booked for a patient
    @Override
    public List<Appointment> listAppointmentsForPatient(
            String patientId) {

        return appointmentDao.findByPatientId(
                patientId
        );
    }

    // List all appointments
    @Override
    public List<Appointment> listAllAppointments() {

        return appointmentDao.findAll();
    }

    /*
     * Update appointment status.
     *
     * The only valid transitions are:
     *
     * SCHEDULED -> CANCELLED
     * SCHEDULED -> COMPLETED
     *
     * CANCELLED is used by the appointment UI.
     * COMPLETED is used by the billing workflow.
     */
    @Override
    public void updateAppointmentStatus(
            String appointmentNumber,
            AppointmentStatus newStatus)
            throws RecordNotFoundException,
            ValidationException {

        if (appointmentNumber == null
                || appointmentNumber
                .trim()
                .isEmpty()) {

            throw new ValidationException(
                    "Appointment number is required."
            );
        }

        if (newStatus == null) {

            throw new ValidationException(
                    "Invalid appointment status."
            );
        }

        String cleanedAppointmentNumber =
                appointmentNumber.trim();

        /*
         * Load the appointment before changing anything
         * so that the current status can be checked.
         */
        Appointment appointment =
                findAppointment(
                        cleanedAppointmentNumber
                );

        /*
         * A completed appointment is final.
         */
        if (appointment.getStatus()
                == AppointmentStatus.COMPLETED) {

            throw new ValidationException(
                    "Completed appointments cannot be changed."
            );
        }

        /*
         * A cancelled appointment is also final.
         */
        if (appointment.getStatus()
                == AppointmentStatus.CANCELLED) {

            throw new ValidationException(
                    "Cancelled appointments cannot be changed."
            );
        }

        /*
         * At this point the appointment must be SCHEDULED.
         *
         * We only permit completion or cancellation.
         */
        if (newStatus != AppointmentStatus.CANCELLED
                && newStatus
                != AppointmentStatus.COMPLETED) {

            throw new ValidationException(
                    "A scheduled appointment can only "
                            + "be completed or cancelled."
            );
        }

        boolean updated =
                appointmentDao.updateStatus(
                        cleanedAppointmentNumber,
                        newStatus
                );

        if (!updated) {

            throw new RecordNotFoundException(
                    "No appointment found with number "
                            + cleanedAppointmentNumber
            );
        }

        /*
         * Cancellation-specific notifications.
         */
        if (newStatus
                == AppointmentStatus.CANCELLED) {

            String formattedDateTime =
                    appointment
                            .getAppointmentDateTime()
                            .format(
                                    DATE_TIME_FORMAT
                            );

            notifier.publish(
                    appointment
                            .getPatient()
                            .getEmail(),
                    "Your appointment #"
                            + cleanedAppointmentNumber
                            + " with "
                            + appointment
                            .getDentist()
                            .getName()
                            + " scheduled for "
                            + formattedDateTime
                            + " has been cancelled.",
                    cleanedAppointmentNumber
            );

            notifier.publish(
                    appointment
                            .getDentist()
                            .getEmail(),
                    "Appointment #"
                            + cleanedAppointmentNumber
                            + " with "
                            + appointment
                            .getPatient()
                            .getName()
                            + " scheduled for "
                            + formattedDateTime
                            + " has been cancelled.",
                    cleanedAppointmentNumber
            );

            return;
        }

        /*
         * COMPLETED normally happens after
         * successful billing.
         */
        notifier.publish(
                appointment
                        .getPatient()
                        .getEmail(),
                "Your appointment #"
                        + cleanedAppointmentNumber
                        + " has been completed.",
                cleanedAppointmentNumber
        );

        notifier.publish(
                appointment
                        .getDentist()
                        .getEmail(),
                "Appointment #"
                        + cleanedAppointmentNumber
                        + " with "
                        + appointment
                        .getPatient()
                        .getName()
                        + " has been completed.",
                cleanedAppointmentNumber
        );
    }

    // Check dentist availability
    private boolean isDentistDoubleBooked(
            Dentist dentist,
            LocalDateTime appointmentDateTime) {

        return appointmentDao
                .findAll()
                .stream()
                .filter(
                        appointment ->
                                appointment.getStatus()
                                        != AppointmentStatus.CANCELLED
                )
                .anyMatch(
                        appointment ->
                                appointment
                                        .getDentist()
                                        .getDentistId()
                                        .equals(
                                                dentist.getDentistId()
                                        )
                                        && appointment
                                        .getAppointmentDateTime()
                                        .equals(
                                                appointmentDateTime
                                        )
                );
    }
}