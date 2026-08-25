package com.sunrise.sunrisedentalpms.controller;

import com.sunrise.sunrisedentalpms.exception.AuthorizationException;
import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.TreatmentType;
import com.sunrise.sunrisedentalpms.model.User;
import com.sunrise.sunrisedentalpms.service.ServiceFactory;
import com.sunrise.sunrisedentalpms.service.TreatmentTypeServiceInterface;
import com.sunrise.sunrisedentalpms.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/treatmentType")
public class TreatmentTypeController extends HttpServlet {

    private TreatmentTypeServiceInterface treatmentTypeService;

    public TreatmentTypeController() {
    }

    // for testing
    TreatmentTypeController(TreatmentTypeServiceInterface treatmentTypeService) {
        this.treatmentTypeService = treatmentTypeService;
    }

    @Override
    public void init() {
        if (treatmentTypeService == null) {
            treatmentTypeService = ServiceFactory.getTreatmentTypeService();
        }
    }

    // Show treatment types
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessionUtil.getLoggedInUser(request) == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String treatmentTypeId = request.getParameter("treatmentTypeId");

        if (treatmentTypeId == null || treatmentTypeId.isEmpty()) {
            List<TreatmentType> treatmentTypes = treatmentTypeService.listAllTreatmentTypes();
            request.setAttribute("treatmentTypes", treatmentTypes);
            request.getRequestDispatcher("treatmentTypeList.jsp").forward(request, response);
            return;
        }

        try {
            TreatmentType treatmentType = treatmentTypeService.findTreatmentType(treatmentTypeId);
            request.setAttribute("treatmentType", treatmentType);
            request.getRequestDispatcher("treatmentTypeDetails.jsp").forward(request, response);
        } catch (RecordNotFoundException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("treatmentTypeList.jsp").forward(request, response);
        }
    }

    // manage treatment types
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loggedInUser = SessionUtil.getLoggedInUser(request);

        if (loggedInUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if ("updateFee".equals(action)) {
            handleUpdateFee(request, response, loggedInUser);
        } else {
            handleAdd(request, response, loggedInUser);
        }
    }

    // Add new treatment type
    private void handleAdd(HttpServletRequest request, HttpServletResponse response, User loggedInUser)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String feeText = request.getParameter("consultationFee");

        try {
            BigDecimal fee = new BigDecimal(feeText);
            TreatmentType created = treatmentTypeService.addTreatmentType(name, fee, loggedInUser.getRole());

            request.setAttribute("treatmentType", created);
            request.setAttribute("successMessage", "Treatment type added successfully.");
            request.getRequestDispatcher("treatmentTypeDetails.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Consultation fee must be a valid number.");
            request.getRequestDispatcher("addTreatmentType.jsp").forward(request, response);
        } catch (ValidationException | AuthorizationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("addTreatmentType.jsp").forward(request, response);
        }
    }

    // Update consultation fee
    private void handleUpdateFee(HttpServletRequest request, HttpServletResponse response, User loggedInUser)
            throws ServletException, IOException {
        String treatmentTypeId = request.getParameter("treatmentTypeId");
        String feeText = request.getParameter("consultationFee");

        try {
            BigDecimal fee = new BigDecimal(feeText);
            treatmentTypeService.updateConsultationFee(treatmentTypeId, fee, loggedInUser.getRole());

            request.setAttribute("successMessage", "Consultation fee updated successfully.");
            request.getRequestDispatcher("treatmentTypeList.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Consultation fee must be a valid number.");
            request.getRequestDispatcher("treatmentTypeList.jsp").forward(request, response);
        } catch (RecordNotFoundException | AuthorizationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("treatmentTypeList.jsp").forward(request, response);
        }
    }
}