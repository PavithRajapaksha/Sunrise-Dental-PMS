package com.sunrise.sunrisedentalpms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class NotificationDAO implements NotificationDAOInterface {

    @Override
    public void logNotification(String recipient, String channel, String message, String appointmentNumber) {
        String sql = "INSERT INTO notification_log (appointment_no, recipient, channel, message) VALUES (?, ?, ?, ?)";

        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (appointmentNumber != null) {
                stmt.setInt(1, Integer.parseInt(appointmentNumber));
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setString(2, recipient);
            stmt.setString(3, channel);
            stmt.setString(4, message);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error logging notification");
            e.printStackTrace();
        }
    }
}