package com.sunrise.sunrisedentalpms.model;

import java.math.BigDecimal;

public class TreatmentRevenueReport {

    private final String treatmentTypeId;
    private final String treatmentName;
    private final int billCount;
    private final BigDecimal totalRevenue;

    public TreatmentRevenueReport(String treatmentTypeId, String treatmentName, int billCount, BigDecimal totalRevenue) {
        this.treatmentTypeId = treatmentTypeId;
        this.treatmentName = treatmentName;
        this.billCount = billCount;
        this.totalRevenue = totalRevenue;
    }

    public String getTreatmentTypeId() {
        return treatmentTypeId;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public int getBillCount() {
        return billCount;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
}