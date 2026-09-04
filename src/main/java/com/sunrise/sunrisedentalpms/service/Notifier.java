package com.sunrise.sunrisedentalpms.service;

import java.util.ArrayList;
import java.util.List;

public class Notifier {

    private final List<NotificationServiceInterface> listeners = new ArrayList<>();

    public void registerListener(NotificationServiceInterface listener) {
        listeners.add(listener);
    }

    public void publish(String recipientEmail, String message, String appointmentNumber) {
        publish(recipientEmail, message, appointmentNumber, null, null);
    }

    public void publish(String recipientEmail, String message, String appointmentNumber,
                        byte[] attachment, String attachmentFileName) {
        if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
            return;
        }

        for (NotificationServiceInterface listener : listeners) {
            listener.send(recipientEmail, message, appointmentNumber, attachment, attachmentFileName);
        }
    }
}