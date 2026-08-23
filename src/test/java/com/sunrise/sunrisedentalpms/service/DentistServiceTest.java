package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.dao.DentistDAOInterface;
import com.sunrise.sunrisedentalpms.exception.AuthorizationException;
import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.Dentist;
import com.sunrise.sunrisedentalpms.model.DentistStatus;
import com.sunrise.sunrisedentalpms.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DentistServiceTest {

    @Mock
    private DentistDAOInterface dentistDao;

    private DentistService dentistService;
    private Dentist sampleDentist;

    @BeforeEach
    void setUp() {
        dentistService = new DentistService(dentistDao);
        sampleDentist = new Dentist("1", "Dr. Perera", "0711234567");
    }

    @Test
    void Register_withValidData_shouldReturnDentist() throws ValidationException, AuthorizationException {
        when(dentistDao.createDentist("Dr. Perera", "0711234567")).thenReturn(sampleDentist);

        Dentist result = dentistService.registerDentist("Dr. Perera", "0711234567", UserRole.ADMIN);

        assertEquals(sampleDentist, result);
    }

    @Test
    void Register_withInvalidData_shouldFail() {
        when(dentistDao.createDentist("", "bad-number")).thenReturn(null);

        assertThrows(ValidationException.class,
                () -> dentistService.registerDentist("", "bad-number", UserRole.ADMIN));
    }

    @Test
    void Register_withNonAdminRole_shouldFail() {
        assertThrows(AuthorizationException.class,
                () -> dentistService.registerDentist("Dr. Perera", "0711234567", UserRole.RECEPTIONIST));

        verify(dentistDao, never()).createDentist(anyString(), anyString());
    }

    @Test
    void Find_withValidId_shouldReturnDentist() throws RecordNotFoundException {
        when(dentistDao.findById("1")).thenReturn(Optional.of(sampleDentist));

        Dentist result = dentistService.findDentist("1");

        assertEquals(sampleDentist, result);
    }

    @Test
    void Find_withInvalidId_shouldFail() {
        when(dentistDao.findById("99")).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> dentistService.findDentist("99"));
    }

    @Test
    void ListAll_shouldReturnAllDentists() {
        when(dentistDao.findAll()).thenReturn(List.of(sampleDentist));

        List<Dentist> result = dentistService.listAllDentists();

        assertEquals(1, result.size());
    }

    @Test
    void ListAvailable_shouldReturnAvailableDentists() {
        when(dentistDao.findAllAvailable()).thenReturn(List.of(sampleDentist));

        List<Dentist> result = dentistService.listAvailableDentists();

        assertEquals(1, result.size());
    }

    @Test
    void UpdateStatus_withValidId_shouldSucceed() {
        when(dentistDao.updateStatus("1", DentistStatus.UNAVAILABLE)).thenReturn(true);

        assertDoesNotThrow(() -> dentistService.updateDentistStatus("1", DentistStatus.UNAVAILABLE));
    }

    @Test
    void UpdateStatus_withInvalidId_shouldFail() {
        when(dentistDao.updateStatus("99", DentistStatus.UNAVAILABLE)).thenReturn(false);

        assertThrows(RecordNotFoundException.class,
                () -> dentistService.updateDentistStatus("99", DentistStatus.UNAVAILABLE));
    }
}