package com.sunrise.sunrisedentalpms.dao;

public interface NotificationDAOInterface {

    void logNotification(String recipient, String channel, String message, String appointmentNumber);
}