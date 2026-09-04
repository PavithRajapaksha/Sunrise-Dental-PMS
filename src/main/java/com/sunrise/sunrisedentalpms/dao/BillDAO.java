package com.sunrise.sunrisedentalpms.dao;

import com.sunrise.sunrisedentalpms.model.Appointment;
import com.sunrise.sunrisedentalpms.model.Bill;
import com.sunrise.sunrisedentalpms.model.BillStatus;
import com.sunrise.sunrisedentalpms.model.PaymentType;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BillDAO implements BillDAOInterface {

    private final AppointmentDAOInterface appointmentDao;

    public BillDAO() {
        this.appointmentDao = new AppointmentDAO();
    }

    public BillDAO(AppointmentDAOInterface appointmentDao) {
        this.appointmentDao = appointmentDao;
    }

    // Creates a new paid bill for an appointment
    @Override
    public Bill createBill(
            Appointment appointment,
            PaymentType paymentType,
            String generatedByUserId) {

        if (appointment == null) {
            System.err.println("Cannot create a bill without an appointment");
            return null;
        }

        if (paymentType == null) {
            System.err.println("Cannot create a bill without a payment type");
            return null;
        }

        if (findByAppointmentNumber(
                appointment.getAppointmentNumber()).isPresent()) {

            System.err.println(
                    "A bill already exists for appointment "
                            + appointment.getAppointmentNumber()
            );

            return null;
        }

        BigDecimal frozenAmount =
                appointment.getTreatmentType().getConsultationFee();

        Integer appointmentId =
                parseId(appointment.getAppointmentNumber());

        Integer userId =
                parseId(generatedByUserId);

        if (appointmentId == null || userId == null) {
            System.err.println(
                    "Invalid ID supplied while creating bill"
            );

            return null;
        }

        String sql =
                "INSERT INTO bill " +
                        "(appointment_no, total_amount, payment_type, status, " +
                        "generated_date, generated_by) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn =
                DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt =
                     conn.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            LocalDate today = LocalDate.now();

            stmt.setInt(
                    1,
                    appointmentId
            );

            stmt.setBigDecimal(
                    2,
                    frozenAmount
            );

            stmt.setString(
                    3,
                    paymentType.name()
            );

            /*
             * Because the receptionist selects the payment method
             * before generating the bill, the payment is being
             * confirmed at this point.
             */
            stmt.setString(
                    4,
                    BillStatus.PAID.name()
            );

            stmt.setDate(
                    5,
                    Date.valueOf(today)
            );

            stmt.setInt(
                    6,
                    userId
            );

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {

                if (keys.next()) {

                    String generatedId =
                            String.valueOf(keys.getInt(1));

                    Bill created =
                            new Bill(
                                    generatedId,
                                    appointment,
                                    frozenAmount,
                                    today,
                                    generatedByUserId
                            );

                    created.setPaymentType(paymentType);
                    created.setStatus(BillStatus.PAID);

                    return created;
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Error creating bill record"
            );

            e.printStackTrace();
        }

        return null;
    }

    // Finds a bill by appointment number
    @Override
    public Optional<Bill> findByAppointmentNumber(
            String appointmentNumber) {

        Integer id = parseId(appointmentNumber);

        if (id == null) {
            return Optional.empty();
        }

        String sql =
                "SELECT bill_id, appointment_no, total_amount, " +
                        "payment_type, status, generated_date, generated_by " +
                        "FROM bill WHERE appointment_no = ?";

        Connection conn =
                DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(
                            mapRow(rs)
                    );
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Error finding bill by appointment number"
            );

            e.printStackTrace();
        }

        return Optional.empty();
    }

    // Finds a bill by ID
    @Override
    public Optional<Bill> findById(String billId) {

        Integer id = parseId(billId);

        if (id == null) {
            return Optional.empty();
        }

        String sql =
                "SELECT bill_id, appointment_no, total_amount, " +
                        "payment_type, status, generated_date, generated_by " +
                        "FROM bill WHERE bill_id = ?";

        Connection conn =
                DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(
                            mapRow(rs)
                    );
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Error finding bill by ID"
            );

            e.printStackTrace();
        }

        return Optional.empty();
    }

    // Gets all bills
    @Override
    public List<Bill> findAll() {

        String sql =
                "SELECT bill_id, appointment_no, total_amount, " +
                        "payment_type, status, generated_date, generated_by " +
                        "FROM bill ORDER BY generated_date DESC, bill_id DESC";

        List<Bill> bills =
                new ArrayList<>();

        Connection conn =
                DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    bills.add(
                            mapRow(rs)
                    );
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Error retrieving bill list"
            );

            e.printStackTrace();
        }

        return bills;
    }

    // Converts a database row to a Bill object
    private Bill mapRow(ResultSet rs)
            throws SQLException {

        String appointmentNumber =
                String.valueOf(
                        rs.getInt("appointment_no")
                );

        Appointment appointment =
                appointmentDao
                        .findByAppointmentNumber(
                                appointmentNumber
                        )
                        .orElseThrow(
                                () -> new SQLException(
                                        "Bill references appointment "
                                                + appointmentNumber
                                                + " which no longer exists"
                                )
                        );

        Bill bill =
                new Bill(
                        String.valueOf(
                                rs.getInt("bill_id")
                        ),
                        appointment,
                        rs.getBigDecimal(
                                "total_amount"
                        ),
                        rs.getDate(
                                "generated_date"
                        ).toLocalDate(),
                        String.valueOf(
                                rs.getInt("generated_by")
                        )
                );

        String statusText =
                rs.getString("status");

        if (statusText != null) {
            bill.setStatus(
                    BillStatus.valueOf(statusText)
            );
        }

        String paymentTypeText =
                rs.getString("payment_type");

        if (paymentTypeText != null) {
            bill.setPaymentType(
                    PaymentType.valueOf(
                            paymentTypeText
                    )
            );
        }

        return bill;
    }

    private Integer parseId(String id) {

        try {
            return Integer.parseInt(id);

        } catch (NumberFormatException |
                 NullPointerException e) {

            return null;
        }
    }
}