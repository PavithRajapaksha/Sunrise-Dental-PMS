package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.dao.PatientDAOInterface;
import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientDAOInterface patientDao;

    @Mock
    private Notifier notifier;

    private PatientService patientService;
    private Patient samplePatient;

    @BeforeEach
    void setUp() {
        patientService = new PatientService(patientDao, notifier);
        samplePatient = new Patient("1", "Kasun Silva", "12 Galle Road, Colombo", "0711234567");
    }

    @Test
    void Register_withValidData_shouldReturnPatient() throws ValidationException {
        when(patientDao.createPatient("Kasun Silva", "12 Galle Road, Colombo", "0711234567", "kasun.silva@example.com"))
                .thenReturn(samplePatient);

        Patient result = patientService.registerPatient("Kasun Silva", "12 Galle Road, Colombo", "0711234567", "kasun.silva@example.com");

        assertEquals(samplePatient, result);
    }

    @Test
    void Register_whenDaoReturnsNull_shouldFail() {
        when(patientDao.createPatient("Kasun Silva", "12 Galle Road, Colombo", "0711234567", "kasun.silva@example.com"))
                .thenReturn(null);

        assertThrows(ValidationException.class,
                () -> patientService.registerPatient("Kasun Silva", "12 Galle Road, Colombo", "0711234567", "kasun.silva@example.com"));
    }

    @Test
    void Register_whenDaoThrowsInvalidData_shouldFail() {
        when(patientDao.createPatient("", "12 Galle Road, Colombo", "0711234567", "kasun.silva@example.com"))
                .thenThrow(new IllegalArgumentException("Name cannot be empty"));

        assertThrows(ValidationException.class,
                () -> patientService.registerPatient("", "12 Galle Road, Colombo", "0711234567", "kasun.silva@example.com"));
    }

    @Test
    void Find_withValidId_shouldReturnPatient() throws RecordNotFoundException {
        when(patientDao.findById("1")).thenReturn(Optional.of(samplePatient));

        Patient result = patientService.findPatient("1");

        assertEquals(samplePatient, result);
    }

    @Test
    void Find_withInvalidId_shouldFail() {
        when(patientDao.findById("99")).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> patientService.findPatient("99"));
    }

    @Test
    void FindByContactNumber_withValidNumber_shouldReturnPatient() throws RecordNotFoundException {
        when(patientDao.findByContactNumber("0711234567")).thenReturn(Optional.of(samplePatient));

        Patient result = patientService.findPatientByContactNumber("0711234567");

        assertEquals(samplePatient, result);
    }

    @Test
    void FindByContactNumber_withInvalidNumber_shouldFail() {
        when(patientDao.findByContactNumber("0799999999")).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> patientService.findPatientByContactNumber("0799999999"));
    }

    @Test
    void ListAll_shouldReturnAllPatients() {
        when(patientDao.findAll()).thenReturn(List.of(samplePatient));

        List<Patient> result = patientService.listAllPatients();

        assertEquals(1, result.size());
    }

    @Test
    void FindOrRegister_shouldReturnPatient() throws ValidationException {
        when(patientDao.findOrCreate("Kasun Silva", "12 Galle Road, Colombo", "0711234567")).thenReturn(samplePatient);

        Patient result = patientService.findOrRegisterPatient("Kasun Silva", "12 Galle Road, Colombo", "0711234567");

        assertEquals(samplePatient, result);
    }

    @Test
    void FindOrRegister_whenDaoThrowsInvalidData_shouldFail() {
        when(patientDao.findOrCreate("", "12 Galle Road, Colombo", "0711234567"))
                .thenThrow(new IllegalArgumentException("Name cannot be empty"));

        assertThrows(ValidationException.class,
                () -> patientService.findOrRegisterPatient("", "12 Galle Road, Colombo", "0711234567"));
    }
}