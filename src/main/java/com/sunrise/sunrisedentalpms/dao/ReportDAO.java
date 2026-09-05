package com.sunrise.sunrisedentalpms.dao;

import com.sunrise.sunrisedentalpms.model.DentistWorkloadReport;
import com.sunrise.sunrisedentalpms.model.TreatmentRevenueReport;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO implements ReportDAOInterface {

    @Override
    public List<TreatmentRevenueReport> getRevenueByTreatmentType() {
        return getRevenueByTreatmentType(null);
    }

    @Override
    public List<TreatmentRevenueReport> getRevenueByTreatmentType(
            String treatmentTypeId) {

        List<TreatmentRevenueReport> report =
                new ArrayList<>();

        String selectedTreatmentTypeId =
                normalizeFilter(treatmentTypeId);

        Connection conn =
                DBConnection
                        .getInstance()
                        .getConnection();

        try (CallableStatement stmt =
                     conn.prepareCall(
                             "{CALL sp_revenue_by_treatment_type()}"
                     )) {

            try (ResultSet rs =
                         stmt.executeQuery()) {

                while (rs.next()) {

                    String currentTreatmentTypeId =
                            String.valueOf(
                                    rs.getInt(
                                            "treatment_type_id"
                                    )
                            );

                    if (selectedTreatmentTypeId != null
                            && !selectedTreatmentTypeId.equals(
                            currentTreatmentTypeId
                    )) {
                        continue;
                    }

                    report.add(
                            new TreatmentRevenueReport(
                                    currentTreatmentTypeId,
                                    rs.getString(
                                            "treatment_name"
                                    ),
                                    rs.getInt(
                                            "bill_count"
                                    ),
                                    rs.getBigDecimal(
                                            "total_revenue"
                                    )
                            )
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error retrieving revenue by treatment type"
            );
            e.printStackTrace();
        }

        return report;
    }

    @Override
    public List<DentistWorkloadReport> getDentistWorkload() {
        return getDentistWorkload(null);
    }

    @Override
    public List<DentistWorkloadReport> getDentistWorkload(
            String dentistId) {

        List<DentistWorkloadReport> report =
                new ArrayList<>();

        String selectedDentistId =
                normalizeFilter(dentistId);

        Connection conn =
                DBConnection
                        .getInstance()
                        .getConnection();

        try (CallableStatement stmt =
                     conn.prepareCall(
                             "{CALL sp_dentist_workload()}"
                     )) {

            try (ResultSet rs =
                         stmt.executeQuery()) {

                while (rs.next()) {

                    String currentDentistId =
                            String.valueOf(
                                    rs.getInt(
                                            "dentist_id"
                                    )
                            );

                    if (selectedDentistId != null
                            && !selectedDentistId.equals(
                            currentDentistId
                    )) {
                        continue;
                    }

                    report.add(
                            new DentistWorkloadReport(
                                    currentDentistId,
                                    rs.getString(
                                            "dentist_name"
                                    ),
                                    rs.getInt(
                                            "appointment_count"
                                    )
                            )
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error retrieving dentist workload report"
            );
            e.printStackTrace();
        }

        return report;
    }

    private String normalizeFilter(
            String value) {

        if (value == null
                || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }
}