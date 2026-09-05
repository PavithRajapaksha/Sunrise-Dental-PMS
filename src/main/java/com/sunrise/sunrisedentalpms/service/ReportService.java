package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.dao.ReportDAOInterface;
import com.sunrise.sunrisedentalpms.exception.AuthorizationException;
import com.sunrise.sunrisedentalpms.model.DentistWorkloadReport;
import com.sunrise.sunrisedentalpms.model.TreatmentRevenueReport;
import com.sunrise.sunrisedentalpms.model.UserRole;

import java.util.List;
import java.util.Objects;

public class ReportService
        implements ReportServiceInterface {

    private final ReportDAOInterface reportDao;

    public ReportService(
            ReportDAOInterface reportDao) {

        this.reportDao =
                Objects.requireNonNull(
                        reportDao,
                        "ReportDAOInterface cannot be null"
                );
    }

    @Override
    public List<TreatmentRevenueReport>
    getRevenueByTreatmentType(
            UserRole requestingUserRole)
            throws AuthorizationException {

        return getRevenueByTreatmentType(
                requestingUserRole,
                null
        );
    }

    @Override
    public List<TreatmentRevenueReport>
    getRevenueByTreatmentType(
            UserRole requestingUserRole,
            String treatmentTypeId)
            throws AuthorizationException {

        validateAdmin(
                requestingUserRole
        );

        return reportDao
                .getRevenueByTreatmentType(
                        treatmentTypeId
                );
    }

    @Override
    public List<DentistWorkloadReport>
    getDentistWorkload(
            UserRole requestingUserRole)
            throws AuthorizationException {

        return getDentistWorkload(
                requestingUserRole,
                null
        );
    }

    @Override
    public List<DentistWorkloadReport>
    getDentistWorkload(
            UserRole requestingUserRole,
            String dentistId)
            throws AuthorizationException {

        validateAdmin(
                requestingUserRole
        );

        return reportDao
                .getDentistWorkload(
                        dentistId
                );
    }

    private void validateAdmin(
            UserRole requestingUserRole)
            throws AuthorizationException {

        if (requestingUserRole
                != UserRole.ADMIN) {

            throw new AuthorizationException(
                    "Only an admin can view reports."
            );
        }
    }
}