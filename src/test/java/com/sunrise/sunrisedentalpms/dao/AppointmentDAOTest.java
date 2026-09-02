package com.sunrise.sunrisedentalpms.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.sunrise.sunrisedentalpms.model.Appointment;
import com.sunrise.sunrisedentalpms.model.AppointmentStatus;
import com.sunrise.sunrisedentalpms.model.Dentist;
import com.sunrise.sunrisedentalpms.model.DentistStatus;
import com.sunrise.sunrisedentalpms.model.Patient;
import com.sunrise.sunrisedentalpms.model.TreatmentType;

class AppointmentDAOTest {

    private AppointmentDAOInterface appointmentDao;

    private Patient testPatient;
    private Dentist testDentist;
    private TreatmentType testTreatmentType;

    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    private MockedStatic<DBConnection> dbConnectionMock;

    @BeforeEach
    void setUp() {
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);

        DBConnection mockDbConnection = mock(DBConnection.class);
        dbConnectionMock = mockStatic(DBConnection.class);
        dbConnectionMock.when(DBConnection::getInstance).thenReturn(mockDbConnection);
        when(mockDbConnection.getConnection()).thenReturn(connection);

        appointmentDao = new AppointmentDAO();

        testPatient = new Patient("1", "Test Patient", "123 Test Lane", "0770000333");
        testDentist = new Dentist("2", "Test Dentist", "0770000444");
        testTreatmentType = new TreatmentType("3", "Test Appointment Treatment", new BigDecimal("2000.00"));
    }

    @AfterEach
    void tearDown() {
        dbConnectionMock.close();
    }

    @Test
    void createAppointment_ShouldInsertAppointmentWithScheduledStatus() throws Exception {
        LocalDateTime dateTime = testDateTime(1);

        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(10);

        Appointment created = appointmentDao.createAppointment(
                testPatient, testDentist, testTreatmentType, dateTime, "4");

        assertNotNull(created);
        assertEquals(AppointmentStatus.SCHEDULED, created.getStatus());
        assertEquals("4", created.getBookedByUserId());

        verify(preparedStatement).setInt(1, 1);
        verify(preparedStatement).setInt(2, 2);
        verify(preparedStatement).setInt(3, 3);
        verify(preparedStatement).setInt(4, 4);
        verify(preparedStatement).setString(7, "SCHEDULED");
    }

    @Test
    void findByAppointmentNumber_ShouldReturnCreatedAppointment() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        stubAppointmentColumns(2, testDateTime(2));

        Optional<Appointment> found = appointmentDao.findByAppointmentNumber("2");

        assertTrue(found.isPresent());
        assertEquals("2", found.get().getAppointmentNumber());
        assertEquals(testPatient.getPatientId(), found.get().getPatient().getPatientId());
        assertEquals(testDentist.getDentistId(), found.get().getDentist().getDentistId());
        assertEquals(testTreatmentType.getTreatmentTypeId(), found.get().getTreatmentType().getTreatmentTypeId());
    }

    @Test
    void findByPatientId_ShouldReturnAppointmentsForThatPatient() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        stubAppointmentColumns(3, testDateTime(3));

        List<Appointment> results = appointmentDao.findByPatientId(testPatient.getPatientId());

        assertFalse(results.isEmpty());
        assertTrue(results.stream()
                .allMatch(a -> a.getPatient().getPatientId().equals(testPatient.getPatientId())));
    }

    @Test
    void findAll_ShouldIncludeCreatedAppointment() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        stubAppointmentColumns(4, testDateTime(4));

        List<Appointment> all = appointmentDao.findAll();

        assertTrue(all.stream()
                .anyMatch(a -> a.getAppointmentNumber().equals("4")));
    }

    @Test
    void updateStatus_ShouldChangeAppointmentStatus() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        stubAppointmentColumns(5, testDateTime(5));
        when(resultSet.getString("status")).thenReturn(AppointmentStatus.COMPLETED.name());

        boolean updated = appointmentDao.updateStatus("5", AppointmentStatus.COMPLETED);

        assertTrue(updated);

        Optional<Appointment> found = appointmentDao.findByAppointmentNumber("5");
        assertTrue(found.isPresent());
        assertEquals(AppointmentStatus.COMPLETED, found.get().getStatus());
    }

    @Test
    void updateStatus_WithInvalidAppointmentNumber_ShouldReturnFalse() {
        boolean updated = appointmentDao.updateStatus("not-a-number", AppointmentStatus.CANCELLED);
        assertFalse(updated);
    }

    // appointment_time has no fractional-second precision in the DB, so nanos
    // are dropped before insert to keep round-tripped values comparable
    private LocalDateTime testDateTime(int daysFromNow) {
        return LocalDateTime.now().plusDays(daysFromNow).withNano(0);
    }

    private void stubAppointmentColumns(int appointmentNo, LocalDateTime dateTime) throws Exception {
        when(resultSet.getInt("appointment_no")).thenReturn(appointmentNo);
        when(resultSet.getDate("appointment_date")).thenReturn(Date.valueOf(dateTime.toLocalDate()));
        when(resultSet.getTime("appointment_time")).thenReturn(Time.valueOf(dateTime.toLocalTime()));
        when(resultSet.getString("status")).thenReturn(AppointmentStatus.SCHEDULED.name());
        when(resultSet.getInt("user_id")).thenReturn(4);

        when(resultSet.getInt("patient_id")).thenReturn(Integer.parseInt(testPatient.getPatientId()));
        when(resultSet.getString("patient_name")).thenReturn(testPatient.getName());
        when(resultSet.getString("patient_address")).thenReturn(testPatient.getAddress());
        when(resultSet.getString("patient_contact")).thenReturn(testPatient.getContactNumber());

        when(resultSet.getInt("dentist_id")).thenReturn(Integer.parseInt(testDentist.getDentistId()));
        when(resultSet.getString("dentist_name")).thenReturn(testDentist.getName());
        when(resultSet.getString("dentist_contact")).thenReturn(testDentist.getContactNumber());
        when(resultSet.getString("dentist_status")).thenReturn(DentistStatus.AVAILABLE.name());

        when(resultSet.getInt("treatment_type_id")).thenReturn(Integer.parseInt(testTreatmentType.getTreatmentTypeId()));
        when(resultSet.getString("treatment_name")).thenReturn(testTreatmentType.getName());
        when(resultSet.getBigDecimal("treatment_fee")).thenReturn(testTreatmentType.getConsultationFee());
    }
}