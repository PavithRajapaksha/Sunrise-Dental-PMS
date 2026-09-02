package com.sunrise.sunrisedentalpms.controller;

import com.sunrise.sunrisedentalpms.exception.AuthorizationException;
import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.User;
import com.sunrise.sunrisedentalpms.service.ServiceFactory;
import com.sunrise.sunrisedentalpms.service.UserServiceInterface;
import com.sunrise.sunrisedentalpms.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/user")
public class UserController extends HttpServlet {

    private UserServiceInterface userService;

    public UserController() {
    }

    // for testing
    UserController(UserServiceInterface userService) {
        this.userService = userService;
    }

    @Override
    public void init() {
        if (userService == null) {
            userService = ServiceFactory.getUserService();
        }
    }

    // Show users
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessionUtil.getLoggedInUser(request) == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String username = request.getParameter("username");

        if (username == null || username.isEmpty()) {
            List<User> users = userService.listAllUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("userList.jsp").forward(request, response);
            return;
        }

        try {
            User user = userService.findUserByUsername(username);
            request.setAttribute("user", user);
            request.getRequestDispatcher("userDetails.jsp").forward(request, response);
        } catch (RecordNotFoundException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("userList.jsp").forward(request, response);
        }
    }

    // register new user
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loggedInUser = SessionUtil.getLoggedInUser(request);

        if (loggedInUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");
        String contactNumber = request.getParameter("contactNumber");

        try {
            User created = userService.registerUser(username, password, fullName, contactNumber, loggedInUser.getRole());

            request.setAttribute("user", created);
            request.setAttribute("successMessage", "User registered successfully.");
            request.getRequestDispatcher("userDetails.jsp").forward(request, response);
        } catch (ValidationException | AuthorizationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("registerUser.jsp").forward(request, response);
        }
    }
}