package com.sunrise.sunrisedentalpms.dao;

import com.sunrise.sunrisedentalpms.model.DentistWorkloadReport;
import com.sunrise.sunrisedentalpms.model.TreatmentRevenueReport;

import java.util.List;

public interface ReportDAOInterface {

    List<TreatmentRevenueReport> getRevenueByTreatmentType();

    List<TreatmentRevenueReport> getRevenueByTreatmentType(
            String treatmentTypeId
    );

    List<DentistWorkloadReport> getDentistWorkload();

    List<DentistWorkloadReport> getDentistWorkload(
            String dentistId
    );
}