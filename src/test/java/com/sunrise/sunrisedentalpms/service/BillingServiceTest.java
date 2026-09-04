package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.dao.BillDAOInterface;
import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.Appointment;
import com.sunrise.sunrisedentalpms.model.AppointmentStatus;
import com.sunrise.sunrisedentalpms.model.Bill;
import com.sunrise.sunrisedentalpms.model.BillStatus;
import com.sunrise.sunrisedentalpms.model.Dentist;
import com.sunrise.sunrisedentalpms.model.Patient;
import com.sunrise.sunrisedentalpms.model.PaymentType;
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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private BillDAOInterface billDao;

    @Mock
    private AppointmentServiceInterface appointmentService;

    @Mock
    private Notifier notifier;

    private BillingService billingService;
    private Patient samplePatient;
    private Dentist sampleDentist;
    private TreatmentType sampleTreatmentType;

    @BeforeEach
    void setUp() {
        billingService =
                new BillingService(
                        billDao,
                        appointmentService,
                        notifier
                );

        samplePatient =
                new Patient(
                        "1",
                        "Kasun Silva",
                        "12 Galle Road, Colombo",
                        "0711234567"
                );

        sampleDentist =
                new Dentist(
                        "1",
                        "Dr. Perera",
                        "0711234567"
                );

        sampleTreatmentType =
                new TreatmentType(
                        "1",
                        "Root Canal",
                        new BigDecimal("15000.00")
                );
    }

    @Test
    void Generate_withValidAppointment_shouldReturnBill()
            throws Exception {

        Appointment appointment =
                sampleAppointment(
                        AppointmentStatus.SCHEDULED
                );

        Bill bill =
                sampleBill(
                        appointment,
                        PaymentType.CASH
                );

        when(appointmentService.findAppointment("1"))
                .thenReturn(appointment);

        when(billDao.createBill(
                appointment,
                PaymentType.CASH,
                "1"
        )).thenReturn(bill);

        Bill result =
                billingService.generateBill(
                        "1",
                        PaymentType.CASH,
                        "1"
                );

        assertSame(
                bill,
                result
        );

        assertEquals(
                PaymentType.CASH,
                result.getPaymentType()
        );

        assertEquals(
                BillStatus.PAID,
                result.getStatus()
        );

        verify(billDao)
                .createBill(
                        appointment,
                        PaymentType.CASH,
                        "1"
                );

        verify(appointmentService)
                .updateAppointmentStatus(
                        "1",
                        AppointmentStatus.COMPLETED
                );
    }

    @Test
    void Generate_withCardPayment_shouldReturnBill()
            throws Exception {

        Appointment appointment =
                sampleAppointment(
                        AppointmentStatus.SCHEDULED
                );

        Bill bill =
                sampleBill(
                        appointment,
                        PaymentType.CARD
                );

        when(appointmentService.findAppointment("1"))
                .thenReturn(appointment);

        when(billDao.createBill(
                appointment,
                PaymentType.CARD,
                "1"
        )).thenReturn(bill);

        Bill result =
                billingService.generateBill(
                        "1",
                        PaymentType.CARD,
                        "1"
                );

        assertSame(
                bill,
                result
        );

        assertEquals(
                PaymentType.CARD,
                result.getPaymentType()
        );

        verify(appointmentService)
                .updateAppointmentStatus(
                        "1",
                        AppointmentStatus.COMPLETED
                );
    }

    @Test
    void Generate_withUnknownAppointment_shouldFail()
            throws Exception {

        when(appointmentService.findAppointment("99"))
                .thenThrow(
                        new RecordNotFoundException(
                                "No appointment found with number 99"
                        )
                );

        assertThrows(
                RecordNotFoundException.class,
                () -> billingService.generateBill(
                        "99",
                        PaymentType.CASH,
                        "1"
                )
        );

        verify(billDao, never())
                .createBill(
                        any(Appointment.class),
                        any(PaymentType.class),
                        anyString()
                );
    }

    @Test
    void Generate_whenDaoReturnsNull_shouldFail()
            throws Exception {

        Appointment appointment =
                sampleAppointment(
                        AppointmentStatus.SCHEDULED
                );

        when(appointmentService.findAppointment("1"))
                .thenReturn(appointment);

        when(billDao.createBill(
                appointment,
                PaymentType.CASH,
                "1"
        )).thenReturn(null);

        assertThrows(
                ValidationException.class,
                () -> billingService.generateBill(
                        "1",
                        PaymentType.CASH,
                        "1"
                )
        );

        verify(appointmentService, never())
                .updateAppointmentStatus(
                        "1",
                        AppointmentStatus.COMPLETED
                );
    }

    @Test
    void Generate_withCompletedAppointment_shouldFail()
            throws Exception {

        Appointment appointment =
                sampleAppointment(
                        AppointmentStatus.COMPLETED
                );

        when(appointmentService.findAppointment("1"))
                .thenReturn(appointment);

        ValidationException exception =
                assertThrows(
                        ValidationException.class,
                        () -> billingService.generateBill(
                                "1",
                                PaymentType.CASH,
                                "1"
                        )
                );

        assertEquals(
                "Only scheduled appointments can be billed.",
                exception.getMessage()
        );

        verify(billDao, never())
                .createBill(
                        any(Appointment.class),
                        any(PaymentType.class),
                        anyString()
                );
    }

    @Test
    void Generate_withCancelledAppointment_shouldFail()
            throws Exception {

        Appointment appointment =
                sampleAppointment(
                        AppointmentStatus.CANCELLED
                );

        when(appointmentService.findAppointment("1"))
                .thenReturn(appointment);

        ValidationException exception =
                assertThrows(
                        ValidationException.class,
                        () -> billingService.generateBill(
                                "1",
                                PaymentType.CASH,
                                "1"
                        )
                );

        assertEquals(
                "Only scheduled appointments can be billed.",
                exception.getMessage()
        );

        verify(billDao, never())
                .createBill(
                        any(Appointment.class),
                        any(PaymentType.class),
                        anyString()
                );
    }

    @Test
    void Generate_withoutPaymentType_shouldFail() {

        ValidationException exception =
                assertThrows(
                        ValidationException.class,
                        () -> billingService.generateBill(
                                "1",
                                null,
                                "1"
                        )
                );

        assertEquals(
                "Please select a payment type.",
                exception.getMessage()
        );
    }

    @Test
    void Generate_withoutAppointmentNumber_shouldFail() {

        ValidationException exception =
                assertThrows(
                        ValidationException.class,
                        () -> billingService.generateBill(
                                "",
                                PaymentType.CASH,
                                "1"
                        )
                );

        assertEquals(
                "Please select an appointment.",
                exception.getMessage()
        );
    }

    @Test
    void FindByAppointmentNumber_withValidNumber_shouldReturnBill()
            throws RecordNotFoundException {

        Appointment appointment =
                sampleAppointment(
                        AppointmentStatus.COMPLETED
                );

        Bill bill =
                sampleBill(
                        appointment,
                        PaymentType.CASH
                );

        when(billDao.findByAppointmentNumber("1"))
                .thenReturn(
                        Optional.of(bill)
                );

        Bill result =
                billingService
                        .findBillByAppointmentNumber(
                                "1"
                        );

        assertEquals(
                bill,
                result
        );
    }

    @Test
    void FindByAppointmentNumber_withInvalidNumber_shouldFail() {

        when(billDao.findByAppointmentNumber("99"))
                .thenReturn(
                        Optional.empty()
                );

        assertThrows(
                RecordNotFoundException.class,
                () -> billingService
                        .findBillByAppointmentNumber(
                                "99"
                        )
        );
    }

    @Test
    void FindById_withValidId_shouldReturnBill()
            throws RecordNotFoundException {

        Appointment appointment =
                sampleAppointment(
                        AppointmentStatus.COMPLETED
                );

        Bill bill =
                sampleBill(
                        appointment,
                        PaymentType.CASH
                );

        when(billDao.findById("1"))
                .thenReturn(
                        Optional.of(bill)
                );

        Bill result =
                billingService.findBillById(
                        "1"
                );

        assertEquals(
                bill,
                result
        );
    }

    @Test
    void FindById_withInvalidId_shouldFail() {

        when(billDao.findById("99"))
                .thenReturn(
                        Optional.empty()
                );

        assertThrows(
                RecordNotFoundException.class,
                () -> billingService
                        .findBillById(
                                "99"
                        )
        );
    }

    @Test
    void ListAll_shouldReturnAllBills() {

        Appointment appointment =
                sampleAppointment(
                        AppointmentStatus.COMPLETED
                );

        Bill bill =
                sampleBill(
                        appointment,
                        PaymentType.CASH
                );

        when(billDao.findAll())
                .thenReturn(
                        List.of(bill)
                );

        List<Bill> result =
                billingService.listAllBills();

        assertEquals(
                1,
                result.size()
        );
    }

    private Appointment sampleAppointment(
            AppointmentStatus status) {

        return new Appointment.Builder("1")
                .patient(samplePatient)
                .dentist(sampleDentist)
                .treatmentType(sampleTreatmentType)
                .appointmentDateTime(
                        LocalDateTime.now()
                                .plusDays(1)
                )
                .status(status)
                .bookedByUserId("1")
                .build();
    }

    private Bill sampleBill(
            Appointment appointment,
            PaymentType paymentType) {

        Bill bill =
                new Bill(
                        "1",
                        appointment,
                        new BigDecimal("15000.00"),
                        LocalDate.now(),
                        "1"
                );

        bill.setPaymentType(
                paymentType
        );

        bill.setStatus(
                BillStatus.PAID
        );

        return bill;
    }
}