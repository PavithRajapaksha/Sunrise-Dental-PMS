package com.sunrise.sunrisedentalpms.controller;

import com.sunrise.sunrisedentalpms.exception.AuthorizationException;
import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.model.User;
import com.sunrise.sunrisedentalpms.model.UserRole;
import com.sunrise.sunrisedentalpms.service.UserServiceInterface;
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
class UserControllerTest {

    @Mock
    private UserServiceInterface userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private HttpSession session;

    private UserController userController;
    private User adminUser;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService);
        adminUser = new User("1", "admin", "hashedvalue", UserRole.ADMIN, "Admin User", "0711234567");
    }

    @Test
    void Get_whenNotLoggedIn_shouldRedirectToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        userController.doGet(request, response);

        verify(response).sendRedirect("login.jsp");
    }

    @Test
    void Get_withNoUsername_shouldListAllUsers() throws Exception {
        List<User> users = List.of(new User("2", "jdoe", "hashedvalue", UserRole.RECEPTIONIST, "Jane Doe", "0711234568"));

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(adminUser);
        when(request.getParameter("username")).thenReturn(null);
        when(userService.listAllUsers()).thenReturn(users);
        when(request.getRequestDispatcher("userList.jsp")).thenReturn(dispatcher);

        userController.doGet(request, response);

        verify(request).setAttribute("users", users);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Get_withInvalidUsername_shouldShowErrorOnList() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(adminUser);
        when(request.getParameter("username")).thenReturn("unknown");
        when(userService.findUserByUsername("unknown"))
                .thenThrow(new RecordNotFoundException("No user account found with username unknown"));
        when(request.getRequestDispatcher("userList.jsp")).thenReturn(dispatcher);

        userController.doGet(request, response);

        verify(request).setAttribute("errorMessage", "No user account found with username unknown");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Post_register_withValidData_shouldRegisterUser() throws Exception {
        User created = new User("2", "jdoe", "hashedvalue", UserRole.RECEPTIONIST, "Jane Doe", "0711234568");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(adminUser);
        when(request.getParameter("username")).thenReturn("jdoe");
        when(request.getParameter("password")).thenReturn("pass1234");
        when(request.getParameter("fullName")).thenReturn("Jane Doe");
        when(request.getParameter("contactNumber")).thenReturn("0711234568");
        when(userService.registerUser("jdoe", "pass1234", "Jane Doe", "0711234568", UserRole.ADMIN))
                .thenReturn(created);
        when(request.getRequestDispatcher("userDetails.jsp")).thenReturn(dispatcher);

        userController.doPost(request, response);

        verify(request).setAttribute("user", created);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Post_register_withNonAdmin_shouldShowError() throws Exception {
        User receptionist = new User("2", "reception", "hashedvalue", UserRole.RECEPTIONIST, "Reception User", "0711234568");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loggedInUser")).thenReturn(receptionist);
        when(request.getParameter("username")).thenReturn("jdoe");
        when(request.getParameter("password")).thenReturn("pass1234");
        when(request.getParameter("fullName")).thenReturn("Jane Doe");
        when(request.getParameter("contactNumber")).thenReturn("0711234568");
        when(userService.registerUser("jdoe", "pass1234", "Jane Doe", "0711234568", UserRole.RECEPTIONIST))
                .thenThrow(new AuthorizationException("Only an admin can create staff accounts."));
        when(request.getRequestDispatcher("registerUser.jsp")).thenReturn(dispatcher);

        userController.doPost(request, response);

        verify(request).setAttribute("errorMessage", "Only an admin can create staff accounts.");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void Post_whenNotLoggedIn_shouldRedirectToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        userController.doPost(request, response);

        verify(response).sendRedirect("login.jsp");
    }
}