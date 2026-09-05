package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.dao.NotificationDAOInterface;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class EmailService implements NotificationServiceInterface {

    private static final Properties properties = new Properties();

    // Load application.properties
    static {
        try (InputStream input = EmailService.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input == null) {
                throw new RuntimeException(
                        "application.properties file not found"
                );
            }

            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load application.properties", e
            );
        }
    }

    private final NotificationDAOInterface notificationDao;

    public EmailService(NotificationDAOInterface notificationDao) {
        this.notificationDao = Objects.requireNonNull(notificationDao, "NotificationDAOInterface cannot be null");
    }

    // send email with attached bill
    @Override
    public void send(String recipientEmail, String message, String appointmentNumber, byte[] attachment, String attachmentFileName) {
        String host = properties.getProperty("mail.smtp.host");
        String port = properties.getProperty("mail.smtp.port");
        String username = properties.getProperty("mail.smtp.username");
        String password = properties.getProperty("mail.smtp.password");
        String from = properties.getProperty("mail.from");

        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.auth", "true");
        mailProperties.put("mail.smtp.starttls.enable", "true");
        mailProperties.put("mail.smtp.host", host);
        mailProperties.put("mail.smtp.port", port);

        Session session = Session.getInstance(mailProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message emailMessage = new MimeMessage(session);
            emailMessage.setFrom(new InternetAddress(from));
            emailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            emailMessage.setSubject("Sunrise Dental Clinic");

            if (attachment != null) {
                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setText(message);

                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.setDataHandler(new DataHandler(new ByteArrayDataSource(attachment, "application/pdf")));
                attachmentPart.setFileName(attachmentFileName);

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(textPart);
                multipart.addBodyPart(attachmentPart);

                emailMessage.setContent(multipart);
            } else {
                emailMessage.setText(message);
            }

            Transport.send(emailMessage);

            notificationDao.logNotification(recipientEmail, "EMAIL", message, appointmentNumber);

        } catch (MessagingException e) {
            System.err.println("Error sending email notification");
            e.printStackTrace();
        }
    }
}