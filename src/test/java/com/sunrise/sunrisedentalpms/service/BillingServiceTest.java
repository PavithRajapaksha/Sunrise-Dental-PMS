package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.dao.BillDAOInterface;
import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.Appointment;
import com.sunrise.sunrisedentalpms.model.Bill;
import com.sunrise.sunrisedentalpms.model.Dentist;
import com.sunrise.sunrisedentalpms.model.Patient;
import com.sunrise.sunrisedentalpms.model.TreatmentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private BillDAOInterface billDao;

    @Mock
    private AppointmentServiceInterface appointmentService;

    private BillingService billingService;
    private Appointment sampleAppointment;
    private Bill sampleBill;

    @BeforeEach
    void setUp() {
        billingService = new BillingService(billDao, appointmentService);

        Patient patient = new Patient("1", "Kasun Silva", "12 Galle Road, Colombo", "0711234567");
        Dentist dentist = new Dentist("1", "Dr. Perera", "0711234567");
        TreatmentType treatmentType = new TreatmentType("1", "Root Canal", new BigDecimal("15000.00"));

        sampleAppointment = new Appointment.Builder("1")
                .patient(patient).dentist(dentist).treatmentType(treatmentType)
                .appointmentDateTime(LocalDateTime.now().plusDays(1)).bookedByUserId("1").build();

        sampleBill = new Bill("1", sampleAppointment, new BigDecimal("15000.00"), LocalDate.now(), "1");
    }

    @Test
    void Generate_withValidAppointment_shouldReturnBill() throws RecordNotFoundException, ValidationException {
        when(appointmentService.findAppointment("1")).thenReturn(sampleAppointment);
        when(billDao.createBill(sampleAppointment, "1")).thenReturn(sampleBill);

        Bill result = billingService.generateBill("1", "1");

        assertEquals(sampleBill, result);
    }

    @Test
    void Generate_withUnknownAppointment_shouldFail() throws RecordNotFoundException {
        when(appointmentService.findAppointment("99")).thenThrow(new RecordNotFoundException("No appointment found with number 99"));

        assertThrows(RecordNotFoundException.class, () -> billingService.generateBill("99", "1"));
    }

    @Test
    void Generate_whenDaoReturnsNull_shouldFail() throws RecordNotFoundException {
        when(appointmentService.findAppointment("1")).thenReturn(sampleAppointment);
        when(billDao.createBill(sampleAppointment, "1")).thenReturn(null);

        assertThrows(ValidationException.class, () -> billingService.generateBill("1", "1"));
    }

    @Test
    void FindByAppointmentNumber_withValidNumber_shouldReturnBill() throws RecordNotFoundException {
        when(billDao.findByAppointmentNumber("1")).thenReturn(Optional.of(sampleBill));

        Bill result = billingService.findBillByAppointmentNumber("1");

        assertEquals(sampleBill, result);
    }

    @Test
    void FindByAppointmentNumber_withInvalidNumber_shouldFail() {
        when(billDao.findByAppointmentNumber("99")).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> billingService.findBillByAppointmentNumber("99"));
    }

    @Test
    void FindById_withValidId_shouldReturnBill() throws RecordNotFoundException {
        when(billDao.findById("1")).thenReturn(Optional.of(sampleBill));

        Bill result = billingService.findBillById("1");

        assertEquals(sampleBill, result);
    }

    @Test
    void FindById_withInvalidId_shouldFail() {
        when(billDao.findById("99")).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> billingService.findBillById("99"));
    }

    @Test
    void ListAll_shouldReturnAllBills() {
        when(billDao.findAll()).thenReturn(List.of(sampleBill));

        List<Bill> result = billingService.listAllBills();

        assertEquals(1, result.size());
    }
}