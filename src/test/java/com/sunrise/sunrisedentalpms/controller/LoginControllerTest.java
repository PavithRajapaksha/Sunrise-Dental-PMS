package com.sunrise.sunrisedentalpms.controller;

import com.sunrise.sunrisedentalpms.exception.AuthenticationException;
import com.sunrise.sunrisedentalpms.model.User;
import com.sunrise.sunrisedentalpms.model.UserRole;
import com.sunrise.sunrisedentalpms.service.AuthenticationServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private AuthenticationServiceInterface authenticationService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private HttpSession session;

    private LoginController loginController;

    @BeforeEach
    void setUp() {
        loginController = new LoginController(authenticationService);
    }

    @Test
    void Get_shouldForwardToLoginPage() throws Exception {
        when(request.getRequestDispatcher("login.jsp")).thenReturn(dispatcher);

        loginController.doGet(request, response);

        verify(dispatcher).forward(request, response);
    }

    @Test
    void Post_withValidCredentials_shouldCreateSessionAndRedirect() throws Exception {
        User loggedInUser = new User("1", "jdoe", "hashedvalue", UserRole.RECEPTIONIST, "Jane Doe", "0711234567");

        when(request.getParameter("username")).thenReturn("jdoe");
        when(request.getParameter("password")).thenReturn("correctPassword");
        when(authenticationService.login("jdoe", "correctPassword")).thenReturn(loggedInUser);
        when(request.getSession()).thenReturn(session);

        loginController.doPost(request, response);

        verify(session).setAttribute("loggedInUser", loggedInUser);
        verify(response).sendRedirect("dashboard.jsp");
    }

    @Test
    void Post_withInvalidCredentials_shouldShowErrorOnLoginPage() throws Exception {
        when(request.getParameter("username")).thenReturn("jdoe");
        when(request.getParameter("password")).thenReturn("wrongPassword");
        when(authenticationService.login("jdoe", "wrongPassword"))
                .thenThrow(new AuthenticationException("Invalid username or password."));
        when(request.getRequestDispatcher("login.jsp")).thenReturn(dispatcher);

        loginController.doPost(request, response);

        verify(request).setAttribute("errorMessage", "Invalid username or password.");
        verify(dispatcher).forward(request, response);
    }
}