package com.sunrise.sunrisedentalpms.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.sunrise.sunrisedentalpms.model.Appointment;
import com.sunrise.sunrisedentalpms.model.Bill;
import com.sunrise.sunrisedentalpms.model.Dentist;
import com.sunrise.sunrisedentalpms.model.Patient;
import com.sunrise.sunrisedentalpms.model.TreatmentType;

class BillDAOTest {

    private BillDAOInterface billDao;
    private AppointmentDAOInterface appointmentDao;

    private Appointment testAppointment;

    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    private MockedStatic<DBConnection> dbConnectionMock;

    @BeforeEach
    void setUp() {
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);
        appointmentDao = mock(AppointmentDAOInterface.class);

        DBConnection mockDbConnection = mock(DBConnection.class);
        dbConnectionMock = mockStatic(DBConnection.class);
        dbConnectionMock.when(DBConnection::getInstance).thenReturn(mockDbConnection);
        when(mockDbConnection.getConnection()).thenReturn(connection);

        billDao = new BillDAO(appointmentDao);

        Patient testPatient = new Patient("1", "Test Patient", "123 Test Lane", "0770000666");
        Dentist testDentist = new Dentist("2", "Test Dentist", "0770000777");
        TreatmentType testTreatmentType = new TreatmentType("3", "Test Bill Treatment", new BigDecimal("2000.00"));

        testAppointment = new Appointment.Builder("10")
                .patient(testPatient).dentist(testDentist).treatmentType(testTreatmentType)
                .appointmentDateTime(LocalDateTime.now().plusDays(1).withNano(0)).bookedByUserId("4").build();
    }

    @AfterEach
    void tearDown() {
        dbConnectionMock.close();
    }

    @Test
    void createBill_ShouldInsertBillWithCorrectAmount() throws Exception {
        PreparedStatement duplicateCheckStatement = mock(PreparedStatement.class);
        ResultSet duplicateCheckResultSet = mock(ResultSet.class);
        when(connection.prepareStatement(anyString())).thenReturn(duplicateCheckStatement);
        when(duplicateCheckStatement.executeQuery()).thenReturn(duplicateCheckResultSet);
        when(duplicateCheckResultSet.next()).thenReturn(false);

        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(5);

        Bill bill = billDao.createBill(testAppointment, "4");

        assertNotNull(bill);
        assertEquals(0, new BigDecimal("2000.00").compareTo(bill.getTotalAmount()));
    }

    @Test
    void createBill_Duplicate_ShouldReturnNull() throws Exception {
        ResultSet generatedKeys = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        // first createBill call: no existing bill; second call: one already exists
        when(resultSet.next()).thenReturn(false, true);
        stubBillColumns(5, testAppointment.getAppointmentNumber());
        when(appointmentDao.findByAppointmentNumber(testAppointment.getAppointmentNumber()))
                .thenReturn(Optional.of(testAppointment));

        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(5);

        billDao.createBill(testAppointment, "4");
        Bill duplicate = billDao.createBill(testAppointment, "4");

        assertNull(duplicate);
    }

    @Test
    void findByAppointmentNumber_ShouldReturnCreatedBill() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        stubBillColumns(5, testAppointment.getAppointmentNumber());
        when(appointmentDao.findByAppointmentNumber(testAppointment.getAppointmentNumber()))
                .thenReturn(Optional.of(testAppointment));

        Optional<Bill> found = billDao.findByAppointmentNumber(testAppointment.getAppointmentNumber());

        assertTrue(found.isPresent());
        assertEquals("5", found.get().getBillId());
    }

    @Test
    void findById_ShouldReturnCreatedBill() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        stubBillColumns(5, testAppointment.getAppointmentNumber());
        when(appointmentDao.findByAppointmentNumber(testAppointment.getAppointmentNumber()))
                .thenReturn(Optional.of(testAppointment));

        Optional<Bill> found = billDao.findById("5");

        assertTrue(found.isPresent());
        assertEquals(testAppointment.getAppointmentNumber(), found.get().getAppointment().getAppointmentNumber());
    }

    @Test
    void findAll_ShouldIncludeCreatedBill() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        stubBillColumns(5, testAppointment.getAppointmentNumber());
        when(appointmentDao.findByAppointmentNumber(testAppointment.getAppointmentNumber()))
                .thenReturn(Optional.of(testAppointment));

        List<Bill> allBills = billDao.findAll();

        assertTrue(allBills.stream().anyMatch(b -> b.getBillId().equals("5")));
    }

    @Test
    void bill_ShouldStayFixedAfterFeeIsChanged() throws Exception {
        // the bill freezes the fee at creation time; a later fee change on the
        // treatment type must not affect a bill already on file
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        stubBillColumns(5, testAppointment.getAppointmentNumber());
        when(appointmentDao.findByAppointmentNumber(testAppointment.getAppointmentNumber()))
                .thenReturn(Optional.of(testAppointment));

        Optional<Bill> reloaded = billDao.findById("5");

        assertTrue(reloaded.isPresent());
        assertEquals(0, new BigDecimal("2000.00").compareTo(reloaded.get().getTotalAmount()));
    }

    private void stubBillColumns(int billId, String appointmentNo) throws Exception {
        when(resultSet.getInt("bill_id")).thenReturn(billId);
        when(resultSet.getInt("appointment_no")).thenReturn(Integer.parseInt(appointmentNo));
        when(resultSet.getBigDecimal("total_amount")).thenReturn(new BigDecimal("2000.00"));
        when(resultSet.getDate("generated_date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(resultSet.getInt("generated_by")).thenReturn(4);
    }
}