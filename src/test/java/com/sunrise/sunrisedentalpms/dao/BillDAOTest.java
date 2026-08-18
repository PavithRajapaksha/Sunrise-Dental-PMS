package com.sunrise.sunrisedentalpms.dao;

import com.sunrise.sunrisedentalpms.model.Appointment;
import com.sunrise.sunrisedentalpms.model.Bill;
import com.sunrise.sunrisedentalpms.model.Dentist;
import com.sunrise.sunrisedentalpms.model.Patient;
import com.sunrise.sunrisedentalpms.model.TreatmentType;
import com.sunrise.sunrisedentalpms.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BillDAOTest {

    private static final String TEST_PATIENT_CONTACT = "0770000666";
    private static final String TEST_DENTIST_CONTACT = "0770000777";
    private static final String TEST_STAFF_CONTACT = "0770000888";
    private static final String TEST_TREATMENT_NAME = "Test Bill Treatment";
    private static final String TEST_STAFF_USERNAME = "test_bill_staff";
    private static final String TEST_STAFF_PASSWORD = "TestPass123";

    private BillDAOInterface billDao;
    private TreatmentTypeDAOInterface treatmentTypeDao;

    private TreatmentType testTreatmentType;
    private Appointment testAppointment;
    private User testStaff;

    @BeforeEach
    void setUp() {
        billDao = new BillDAO();
        treatmentTypeDao = new TreatmentTypeDAO();

        deleteTestData();

        Patient testPatient = new PatientDAO().createPatient("Test Patient", "123 Test Lane", TEST_PATIENT_CONTACT);
        Dentist testDentist = new DentistDAO().createDentist("Test Dentist", TEST_DENTIST_CONTACT);
        testTreatmentType = treatmentTypeDao.createTreatmentType(TEST_TREATMENT_NAME, new BigDecimal("2000.00"));
        testStaff = new UserDAO().createStaff(TEST_STAFF_USERNAME, TEST_STAFF_PASSWORD, "Test Staff", TEST_STAFF_CONTACT);

        testAppointment = new AppointmentDAO().createAppointment(
                testPatient, testDentist, testTreatmentType, LocalDateTime.now().plusDays(1).withNano(0), testStaff.getUserId());
    }

    @AfterEach
    void tearDown() {
        deleteTestData();
    }

    @Test
    void createBill_ShouldInsertBillWithCorrectAmount() {
        Bill bill = billDao.createBill(testAppointment, testStaff.getUserId());

        assertNotNull(bill);
        assertEquals(0, new BigDecimal("2000.00").compareTo(bill.getTotalAmount()));
    }

    @Test
    void createBill_Duplicate_ShouldReturnNull() {
        billDao.createBill(testAppointment, testStaff.getUserId());

        Bill duplicate = billDao.createBill(testAppointment, testStaff.getUserId());

        assertNull(duplicate);
    }

    @Test
    void findByAppointmentNumber_ShouldReturnCreatedBill() {
        Bill created = billDao.createBill(testAppointment, testStaff.getUserId());

        Optional<Bill> found = billDao.findByAppointmentNumber(testAppointment.getAppointmentNumber());

        assertTrue(found.isPresent());
        assertEquals(created.getBillId(), found.get().getBillId());
    }

    @Test
    void findById_ShouldReturnCreatedBill() {
        Bill created = billDao.createBill(testAppointment, testStaff.getUserId());

        Optional<Bill> found = billDao.findById(created.getBillId());

        assertTrue(found.isPresent());
        assertEquals(testAppointment.getAppointmentNumber(), found.get().getAppointment().getAppointmentNumber());
    }

    @Test
    void findAll_ShouldIncludeCreatedBill() {
        Bill created = billDao.createBill(testAppointment, testStaff.getUserId());

        List<Bill> allBills = billDao.findAll();

        assertTrue(allBills.stream().anyMatch(b -> b.getBillId().equals(created.getBillId())));
    }

    @Test
    void bill_ShouldStayFixedAfterFeeIsChanged() {
        Bill created = billDao.createBill(testAppointment, testStaff.getUserId());

        treatmentTypeDao.updateConsultationFee(testTreatmentType.getTreatmentTypeId(), new BigDecimal("9999.00"));

        Optional<Bill> reloaded = billDao.findById(created.getBillId());

        assertTrue(reloaded.isPresent());
        assertEquals(0, new BigDecimal("2000.00").compareTo(reloaded.get().getTotalAmount()));
    }


    private void deleteTestData() {
        Connection conn = DBConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE b FROM bill b JOIN appointment a ON b.appointment_no = a.appointment_no "
                        + "JOIN patient p ON a.patient_id = p.patient_id WHERE p.contact_number = ?")) {
            stmt.setString(1, TEST_PATIENT_CONTACT);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up test bills", e);
        }

        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE a FROM appointment a JOIN patient p ON a.patient_id = p.patient_id WHERE p.contact_number = ?")) {
            stmt.setString(1, TEST_PATIENT_CONTACT);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up test appointments", e);
        }

        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM patient WHERE contact_number = ?")) {
            stmt.setString(1, TEST_PATIENT_CONTACT);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up test patient", e);
        }

        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM dentist WHERE contact_number = ?")) {
            stmt.setString(1, TEST_DENTIST_CONTACT);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up test dentist", e);
        }

        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM treatment_type WHERE name = ?")) {
            stmt.setString(1, TEST_TREATMENT_NAME);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up test treatment type", e);
        }

        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE username = ?")) {
            stmt.setString(1, TEST_STAFF_USERNAME);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up test staff", e);
        }
    }
}