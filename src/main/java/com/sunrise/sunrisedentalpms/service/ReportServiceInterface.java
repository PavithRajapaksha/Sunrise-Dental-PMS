package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.exception.AuthorizationException;
import com.sunrise.sunrisedentalpms.model.DentistWorkloadReport;
import com.sunrise.sunrisedentalpms.model.TreatmentRevenueReport;
import com.sunrise.sunrisedentalpms.model.UserRole;

import java.util.List;

public interface ReportServiceInterface {

    List<TreatmentRevenueReport> getRevenueByTreatmentType(
            UserRole requestingUserRole
    ) throws AuthorizationException;

    List<TreatmentRevenueReport> getRevenueByTreatmentType(
            UserRole requestingUserRole,
            String treatmentTypeId
    ) throws AuthorizationException;

    List<DentistWorkloadReport> getDentistWorkload(
            UserRole requestingUserRole
    ) throws AuthorizationException;

    List<DentistWorkloadReport> getDentistWorkload(
            UserRole requestingUserRole,
            String dentistId
    ) throws AuthorizationException;
}