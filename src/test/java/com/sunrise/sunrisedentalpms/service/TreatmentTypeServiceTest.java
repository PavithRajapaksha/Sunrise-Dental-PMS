package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.dao.TreatmentTypeDAOInterface;
import com.sunrise.sunrisedentalpms.exception.AuthorizationException;
import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.TreatmentType;
import com.sunrise.sunrisedentalpms.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TreatmentTypeServiceTest {

    @Mock
    private TreatmentTypeDAOInterface treatmentTypeDao;

    private TreatmentTypeService treatmentTypeService;
    private TreatmentType sampleTreatmentType;

    @BeforeEach
    void setUp() {
        treatmentTypeService = new TreatmentTypeService(treatmentTypeDao);
        sampleTreatmentType = new TreatmentType("1", "Root Canal", new BigDecimal("15000.00"));
    }

    @Test
    void Add_withValidData_shouldReturnTreatmentType() throws ValidationException, AuthorizationException {
        when(treatmentTypeDao.createTreatmentType("Root Canal", new BigDecimal("15000.00"))).thenReturn(sampleTreatmentType);

        TreatmentType result = treatmentTypeService.addTreatmentType("Root Canal", new BigDecimal("15000.00"), UserRole.ADMIN);

        assertEquals(sampleTreatmentType, result);
    }

    @Test
    void Add_withInvalidData_shouldFail() {
        when(treatmentTypeDao.createTreatmentType("", new BigDecimal("-5"))).thenReturn(null);

        assertThrows(ValidationException.class,
                () -> treatmentTypeService.addTreatmentType("", new BigDecimal("-5"), UserRole.ADMIN));
    }

    @Test
    void Add_withNonAdminRole_shouldFail() {
        assertThrows(AuthorizationException.class,
                () -> treatmentTypeService.addTreatmentType("Root Canal", new BigDecimal("15000.00"), UserRole.RECEPTIONIST));

        verify(treatmentTypeDao, never()).createTreatmentType(anyString(), any());
    }

    @Test
    void Find_withValidId_shouldReturnTreatmentType() throws RecordNotFoundException {
        when(treatmentTypeDao.findById("1")).thenReturn(Optional.of(sampleTreatmentType));

        TreatmentType result = treatmentTypeService.findTreatmentType("1");

        assertEquals(sampleTreatmentType, result);
    }

    @Test
    void Find_withInvalidId_shouldFail() {
        when(treatmentTypeDao.findById("99")).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class,
                () -> treatmentTypeService.findTreatmentType("99"));
    }

    @Test
    void ListAll_shouldReturnAllTreatmentTypes() {
        when(treatmentTypeDao.findAll()).thenReturn(List.of(sampleTreatmentType));

        List<TreatmentType> result = treatmentTypeService.listAllTreatmentTypes();

        assertEquals(1, result.size());
    }

    @Test
    void UpdateFee_withValidId_shouldSucceed() throws RecordNotFoundException, AuthorizationException {
        when(treatmentTypeDao.updateConsultationFee("1", new BigDecimal("18000.00"))).thenReturn(true);

        assertDoesNotThrow(() -> treatmentTypeService.updateConsultationFee("1", new BigDecimal("18000.00"), UserRole.ADMIN));
    }

    @Test
    void UpdateFee_withInvalidId_shouldFail() {
        when(treatmentTypeDao.updateConsultationFee("99", new BigDecimal("18000.00"))).thenReturn(false);

        assertThrows(RecordNotFoundException.class,
                () -> treatmentTypeService.updateConsultationFee("99", new BigDecimal("18000.00"), UserRole.ADMIN));
    }

    @Test
    void UpdateFee_withNonAdminRole_shouldFail() {
        assertThrows(AuthorizationException.class,
                () -> treatmentTypeService.updateConsultationFee("1", new BigDecimal("18000.00"), UserRole.RECEPTIONIST));

        verify(treatmentTypeDao, never()).updateConsultationFee(anyString(), any());
    }
}