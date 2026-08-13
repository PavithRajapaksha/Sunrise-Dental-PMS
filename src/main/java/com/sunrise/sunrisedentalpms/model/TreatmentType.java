package com.sunrise.sunrisedentalpms.model;

import java.math.BigDecimal;

/** Fixed catalogue of treatments offered by the clinic, each with a base cost (LKR). */
public enum TreatmentType {
    CONSULTATION("Consultation", new BigDecimal("1000")),
    SCALING_AND_CLEANING("Scaling and Cleaning", new BigDecimal("3500")),
    FILLING("Filling", new BigDecimal("5000")),
    ROOT_CANAL("Root Canal Treatment", new BigDecimal("15000")),
    EXTRACTION("Tooth Extraction", new BigDecimal("4000")),
    BRACES_FITTING("Braces Fitting", new BigDecimal("45000")),
    TEETH_WHITENING("Teeth Whitening", new BigDecimal("12000")),
    CROWN_FITTING("Crown Fitting", new BigDecimal("18000"));

    private final String displayName;
    private final BigDecimal baseCost;

    TreatmentType(String displayName, BigDecimal baseCost) {
        this.displayName = displayName;
        this.baseCost = baseCost;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BigDecimal getBaseCost() {
        return baseCost;
    }
}