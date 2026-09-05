package com.sunrise.sunrisedentalpms.controller;

import com.sunrise.sunrisedentalpms.exception.AuthorizationException;
import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.Dentist;
import com.sunrise.sunrisedentalpms.model.DentistStatus;
import com.sunrise.sunrisedentalpms.model.User;
import com.sunrise.sunrisedentalpms.service.DentistServiceInterface;
import com.sunrise.sunrisedentalpms.service.ServiceFactory;
import com.sunrise.sunrisedentalpms.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/dentist")
public class DentistController extends HttpServlet {

    private DentistServiceInterface dentistService;

    public DentistController() {
    }

    // for testing
    DentistController(DentistServiceInterface dentistService) {
        this.dentistService = dentistService;
    }

    @Override
    public void init() {
        if (dentistService == null) {
            dentistService = ServiceFactory.getDentistService();
        }
    }

    // Show dentists
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessionUtil.getLoggedInUser(request) == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String dentistId = request.getParameter("dentistId");

        if (dentistId == null || dentistId.isEmpty()) {
            boolean availableOnly = "true".equals(request.getParameter("availableOnly"));

            List<Dentist> dentists = availableOnly
                    ? dentistService.listAvailableDentists()
                    : dentistService.listAllDentists();

            request.setAttribute("dentists", dentists);
            request.getRequestDispatcher("dentistList.jsp").forward(request, response);
            return;
        }

        try {
            Dentist dentist = dentistService.findDentist(dentistId);
            request.setAttribute("dentist", dentist);
            request.getRequestDispatcher("dentistDetails.jsp").forward(request, response);
        } catch (RecordNotFoundException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("dentistList.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loggedInUser = SessionUtil.getLoggedInUser(request);

        if (loggedInUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if ("updateStatus".equals(action)) {
            handleUpdateStatus(request, response);
        } else {
            handleRegister(request, response, loggedInUser);
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response, User loggedInUser)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String contactNumber = request.getParameter("contactNumber");
        String email = request.getParameter("email");

        try {
            Dentist created = dentistService.registerDentist(
                    name,
                    contactNumber,
                    email,
                    loggedInUser.getRole()
            );

            request.setAttribute("dentist", created);
            request.setAttribute("successMessage", "Dentist registered successfully.");
            request.getRequestDispatcher("dentistDetails.jsp").forward(request, response);
        } catch (ValidationException | AuthorizationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("registerDentist.jsp").forward(request, response);
        }
    }

    // update status
    private void handleUpdateStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String dentistId = request.getParameter("dentistId");
        String statusText = request.getParameter("status");

        if (statusText == null || statusText.isEmpty()) {
            request.setAttribute("errorMessage", "Invalid dentist status.");
            request.getRequestDispatcher("dentistList.jsp").forward(request, response);
            return;
        }

        try {
            DentistStatus newStatus = DentistStatus.valueOf(statusText);

            dentistService.updateDentistStatus(dentistId, newStatus);

            response.sendRedirect(request.getContextPath() + "/dentist");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", "Invalid dentist status.");
            request.getRequestDispatcher("dentistList.jsp").forward(request, response);
        } catch (RecordNotFoundException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("dentistList.jsp").forward(request, response);
        }
    }
}