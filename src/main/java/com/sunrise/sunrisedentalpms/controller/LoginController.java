package com.sunrise.sunrisedentalpms.controller;

import com.sunrise.sunrisedentalpms.exception.AuthenticationException;
import com.sunrise.sunrisedentalpms.model.User;
import com.sunrise.sunrisedentalpms.service.AuthenticationServiceInterface;
import com.sunrise.sunrisedentalpms.service.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginController extends HttpServlet {

    private AuthenticationServiceInterface authenticationService;

    public LoginController() {
    }

    // for testing
    LoginController(AuthenticationServiceInterface authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public void init() {
        if (authenticationService == null) {
            authenticationService = ServiceFactory.getAuthenticationService();
        }
    }

    // Shows the login page
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    // Handles the submitted username and password
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            User loggedInUser = authenticationService.login(username, password);

            HttpSession session = request.getSession();
            session.setAttribute("loggedInUser", loggedInUser);

            response.sendRedirect("dashboard.jsp");
        } catch (AuthenticationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}