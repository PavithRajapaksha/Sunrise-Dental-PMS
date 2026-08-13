package com.sunrise.sunrisedentalpms.model;

import java.util.regex.Pattern;

public abstract class Person {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^0\\d{9}$");

    private String name;
    private String contactNumber;

    protected Person(String name, String contactNumber) {
        setName(name);
        setContactNumber(contactNumber);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name.trim();
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        if (contactNumber == null || !PHONE_PATTERN.matcher(contactNumber).matches()) {
            throw new IllegalArgumentException("Contact number must be 10 digits starting with 0");
        }
        this.contactNumber = contactNumber;
    }
}