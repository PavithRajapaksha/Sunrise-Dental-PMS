package com.sunrise.sunrisedentalpms.controller;

import com.sunrise.sunrisedentalpms.exception.AuthorizationException;
import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.Dentist;
import com.sunrise.sunrisedentalpms.model.DentistStatus;
import com.sunrise.sunrisedentalpms.model.User;
import com.sunrise.sunrisedentalpms.model.UserRole;
import com.sunrise.sunrisedentalpms.service.DentistServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DentistControllerTest {

    @Mock
    private DentistServiceInterface dentistService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private HttpSession session;

    private DentistController dentistController;
    private User adminUser;

    @BeforeEach
    void setUp() {
        dentistController = new DentistController(dentistService);
        adminUser = new User("1", "admin", "hashedvalue", UserRole.ADMIN, "Admin User", "0711234567");
    }

    @Test
    void Get_whenNotLoggedIn_shouldRedirectToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        dentistController.doGet(request, response);

        verify(response).sendRedirect("login.jsp");
    }

    @Test
    void Get_withNoId_shouldListAllDentists() throws Exception {
        List<Dentist> dentists = List.of(new Dentist("1", "Dr. Perera", "0711234567"));

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(adminUser);
        when(request.getParameter("dentistId")).thenReturn(null);
        when(request.getParameter("availableOnly")).thenReturn(null);
        when(dentistService.listAllDentists()).thenReturn(dentists);
        when(request.getRequestDispatcher("dentistList.jsp")).thenReturn(dispatcher);

        dentistController.doGet(request, response);

        verify(request).setAttribute("dentists", dentists);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Get_withAvailableOnly_shouldListAvailableDentists() throws Exception {
        List<Dentist> dentists = List.of(new Dentist("1", "Dr. Perera", "0711234567"));

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(adminUser);
        when(request.getParameter("dentistId")).thenReturn(null);
        when(request.getParameter("availableOnly")).thenReturn("true");
        when(dentistService.listAvailableDentists()).thenReturn(dentists);
        when(request.getRequestDispatcher("dentistList.jsp")).thenReturn(dispatcher);

        dentistController.doGet(request, response);

        verify(request).setAttribute("dentists", dentists);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Get_withInvalidId_shouldShowErrorOnList() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(adminUser);
        when(request.getParameter("dentistId")).thenReturn("99");
        when(dentistService.findDentist("99"))
                .thenThrow(new RecordNotFoundException("No dentist found with id 99"));
        when(request.getRequestDispatcher("dentistList.jsp")).thenReturn(dispatcher);

        dentistController.doGet(request, response);

        verify(request).setAttribute("errorMessage", "No dentist found with id 99");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Post_register_withValidData_shouldRegisterDentist() throws Exception {
        Dentist created = new Dentist("1", "Dr. Perera", "0711234567");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(adminUser);
        when(request.getParameter("action")).thenReturn("register");
        when(request.getParameter("name")).thenReturn("Dr. Perera");
        when(request.getParameter("contactNumber")).thenReturn("0711234567");
        when(dentistService.registerDentist("Dr. Perera", "0711234567", UserRole.ADMIN)).thenReturn(created);
        when(request.getRequestDispatcher("dentistDetails.jsp")).thenReturn(dispatcher);

        dentistController.doPost(request, response);

        verify(request).setAttribute("dentist", created);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Post_register_withNonAdmin_shouldShowError() throws Exception {
        User receptionist = new User("2", "reception", "hashedvalue", UserRole.RECEPTIONIST, "Reception User", "0711234568");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(receptionist);
        when(request.getParameter("action")).thenReturn("register");
        when(request.getParameter("name")).thenReturn("Dr. Perera");
        when(request.getParameter("contactNumber")).thenReturn("0711234567");
        when(dentistService.registerDentist("Dr. Perera", "0711234567", UserRole.RECEPTIONIST))
                .thenThrow(new AuthorizationException("Only an admin can register a new dentist."));
        when(request.getRequestDispatcher("registerDentist.jsp")).thenReturn(dispatcher);

        dentistController.doPost(request, response);

        verify(request).setAttribute("errorMessage", "Only an admin can register a new dentist.");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Post_updateStatus_withValidData_shouldUpdateStatus() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(adminUser);
        when(request.getParameter("action")).thenReturn("updateStatus");
        when(request.getParameter("dentistId")).thenReturn("1");
        when(request.getParameter("status")).thenReturn("UNAVAILABLE");
        when(request.getRequestDispatcher("dentistList.jsp")).thenReturn(dispatcher);

        dentistController.doPost(request, response);

        verify(dentistService).updateDentistStatus("1", DentistStatus.UNAVAILABLE);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Post_updateStatus_withInvalidStatus_shouldShowError() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(adminUser);
        when(request.getParameter("action")).thenReturn("updateStatus");
        when(request.getParameter("dentistId")).thenReturn("1");
        when(request.getParameter("status")).thenReturn("ON_HOLIDAY");
        when(request.getRequestDispatcher("dentistList.jsp")).thenReturn(dispatcher);

        dentistController.doPost(request, response);

        verify(request).setAttribute("errorMessage", "Invalid dentist status.");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Post_whenNotLoggedIn_shouldRedirectToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        dentistController.doPost(request, response);

        verify(response).sendRedirect("login.jsp");
    }
}