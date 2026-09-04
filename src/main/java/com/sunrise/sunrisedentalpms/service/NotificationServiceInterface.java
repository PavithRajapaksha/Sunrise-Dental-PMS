package com.sunrise.sunrisedentalpms.service;

public interface NotificationServiceInterface {

    void send(String recipientEmail, String message, String appointmentNumber, byte[] attachment, String attachmentFileName);
}