package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.dao.AppointmentDAO;
import com.sunrise.sunrisedentalpms.dao.BillDAO;
import com.sunrise.sunrisedentalpms.dao.DentistDAO;
import com.sunrise.sunrisedentalpms.dao.PatientDAO;
import com.sunrise.sunrisedentalpms.dao.TreatmentTypeDAO;
import com.sunrise.sunrisedentalpms.dao.UserDAO;

public final class ServiceFactory {

    private static AuthenticationServiceInterface authenticationService;
    private static UserServiceInterface userService;
    private static PatientServiceInterface patientService;
    private static DentistServiceInterface dentistService;
    private static TreatmentTypeServiceInterface treatmentTypeService;
    private static AppointmentServiceInterface appointmentService;
    private static BillingServiceInterface billingService;

    private ServiceFactory() {
    }

    public static AuthenticationServiceInterface getAuthenticationService() {
        if (authenticationService == null) {
            authenticationService = new AuthenticationService(new UserDAO());
        }
        return authenticationService;
    }

    public static UserServiceInterface getUserService() {
        if (userService == null) {
            userService = new UserService(new UserDAO());
        }
        return userService;
    }

    public static PatientServiceInterface getPatientService() {
        if (patientService == null) {
            patientService = new PatientService(new PatientDAO());
        }
        return patientService;
    }

    public static DentistServiceInterface getDentistService() {
        if (dentistService == null) {
            dentistService = new DentistService(new DentistDAO());
        }
        return dentistService;
    }

    public static TreatmentTypeServiceInterface getTreatmentTypeService() {
        if (treatmentTypeService == null) {
            treatmentTypeService = new TreatmentTypeService(new TreatmentTypeDAO());
        }
        return treatmentTypeService;
    }

    public static AppointmentServiceInterface getAppointmentService() {
        if (appointmentService == null) {
            appointmentService = new AppointmentService(new AppointmentDAO());
        }
        return appointmentService;
    }

    public static BillingServiceInterface getBillingService() {
        if (billingService == null) {
            billingService = new BillingService(new BillDAO(), getAppointmentService());
        }
        return billingService;
    }
}