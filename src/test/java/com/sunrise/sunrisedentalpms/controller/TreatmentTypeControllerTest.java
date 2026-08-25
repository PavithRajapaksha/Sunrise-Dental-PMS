package com.sunrise.sunrisedentalpms.controller;

import com.sunrise.sunrisedentalpms.exception.AuthorizationException;
import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.TreatmentType;
import com.sunrise.sunrisedentalpms.model.User;
import com.sunrise.sunrisedentalpms.model.UserRole;
import com.sunrise.sunrisedentalpms.service.TreatmentTypeServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TreatmentTypeControllerTest {

    @Mock
    private TreatmentTypeServiceInterface treatmentTypeService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private HttpSession session;

    private TreatmentTypeController treatmentTypeController;
    private User adminUser;

    @BeforeEach
    void setUp() {
        treatmentTypeController = new TreatmentTypeController(treatmentTypeService);
        adminUser = new User("1", "admin", "hashedvalue", UserRole.ADMIN, "Admin User", "0711234567");
    }

    @Test
    void Get_whenNotLoggedIn_shouldRedirectToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        treatmentTypeController.doGet(request, response);

        verify(response).sendRedirect("login.jsp");
    }

    @Test
    void Get_withNoId_shouldListAllTreatmentTypes() throws Exception {
        List<TreatmentType> treatmentTypes = List.of(new TreatmentType("1", "Root Canal", new BigDecimal("15000.00")));

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(adminUser);
        when(request.getParameter("treatmentTypeId")).thenReturn(null);
        when(treatmentTypeService.listAllTreatmentTypes()).thenReturn(treatmentTypes);
        when(request.getRequestDispatcher("treatmentTypeList.jsp")).thenReturn(dispatcher);

        treatmentTypeController.doGet(request, response);

        verify(request).setAttribute("treatmentTypes", treatmentTypes);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Get_withInvalidId_shouldShowErrorOnList() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(adminUser);
        when(request.getParameter("treatmentTypeId")).thenReturn("99");
        when(treatmentTypeService.findTreatmentType("99"))
                .thenThrow(new RecordNotFoundException("No treatment type found with id 99"));
        when(request.getRequestDispatcher("treatmentTypeList.jsp")).thenReturn(dispatcher);

        treatmentTypeController.doGet(request, response);

        verify(request).setAttribute("errorMessage", "No treatment type found with id 99");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Post_add_withValidData_shouldAddTreatmentType() throws Exception {
        TreatmentType created = new TreatmentType("1", "Root Canal", new BigDecimal("15000.00"));

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(adminUser);
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("name")).thenReturn("Root Canal");
        when(request.getParameter("consultationFee")).thenReturn("15000.00");
        when(treatmentTypeService.addTreatmentType("Root Canal", new BigDecimal("15000.00"), UserRole.ADMIN))
                .thenReturn(created);
        when(request.getRequestDispatcher("treatmentTypeDetails.jsp")).thenReturn(dispatcher);

        treatmentTypeController.doPost(request, response);

        verify(request).setAttribute("treatmentType", created);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Post_add_withNonAdmin_shouldShowError() throws Exception {
        User receptionist = new User("2", "reception", "hashedvalue", UserRole.RECEPTIONIST, "Reception User", "0711234568");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(receptionist);
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("name")).thenReturn("Root Canal");
        when(request.getParameter("consultationFee")).thenReturn("15000.00");
        when(treatmentTypeService.addTreatmentType("Root Canal", new BigDecimal("15000.00"), UserRole.RECEPTIONIST))
                .thenThrow(new AuthorizationException("Only an admin can add a new treatment type."));
        when(request.getRequestDispatcher("addTreatmentType.jsp")).thenReturn(dispatcher);

        treatmentTypeController.doPost(request, response);

        verify(request).setAttribute("errorMessage", "Only an admin can add a new treatment type.");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Post_add_withInvalidFee_shouldShowNumberFormatError() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(adminUser);
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("name")).thenReturn("Root Canal");
        when(request.getParameter("consultationFee")).thenReturn("not-a-number");
        when(request.getRequestDispatcher("addTreatmentType.jsp")).thenReturn(dispatcher);

        treatmentTypeController.doPost(request, response);

        verify(request).setAttribute("errorMessage", "Consultation fee must be a valid number.");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Post_updateFee_withValidData_shouldUpdateFee() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(adminUser);
        when(request.getParameter("action")).thenReturn("updateFee");
        when(request.getParameter("treatmentTypeId")).thenReturn("1");
        when(request.getParameter("consultationFee")).thenReturn("18000.00");
        when(request.getRequestDispatcher("treatmentTypeList.jsp")).thenReturn(dispatcher);

        treatmentTypeController.doPost(request, response);

        verify(treatmentTypeService).updateConsultationFee("1", new BigDecimal("18000.00"), UserRole.ADMIN);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Post_whenNotLoggedIn_shouldRedirectToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        treatmentTypeController.doPost(request, response);

        verify(response).sendRedirect("login.jsp");
    }
}