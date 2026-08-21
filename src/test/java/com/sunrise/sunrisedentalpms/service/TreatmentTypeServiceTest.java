package com.sunrise.sunrisedentalpms.service;

import com.sunrise.sunrisedentalpms.dao.TreatmentTypeDAOInterface;
import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.TreatmentType;
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
    void Register_withValidData_shouldReturnTreatmentType() throws ValidationException {
        when(treatmentTypeDao.createTreatmentType("Root Canal", new BigDecimal("15000.00"))).thenReturn(sampleTreatmentType);

        TreatmentType result = treatmentTypeService.registerTreatmentType("Root Canal", new BigDecimal("15000.00"));

        assertEquals(sampleTreatmentType, result);
    }

    @Test
    void Register_withInvalidData_shouldFail() {
        when(treatmentTypeDao.createTreatmentType("", new BigDecimal("-5"))).thenReturn(null);

        assertThrows(ValidationException.class,
                () -> treatmentTypeService.registerTreatmentType("", new BigDecimal("-5")));
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
    void UpdateFee_withValidId_shouldSucceed() {
        when(treatmentTypeDao.updateConsultationFee("1", new BigDecimal("18000.00"))).thenReturn(true);

        assertDoesNotThrow(() -> treatmentTypeService.updateConsultationFee("1", new BigDecimal("18000.00")));
    }

    @Test
    void UpdateFee_withInvalidId_shouldFail() {
        when(treatmentTypeDao.updateConsultationFee("99", new BigDecimal("18000.00"))).thenReturn(false);

        assertThrows(RecordNotFoundException.class,
                () -> treatmentTypeService.updateConsultationFee("99", new BigDecimal("18000.00")));
    }
}