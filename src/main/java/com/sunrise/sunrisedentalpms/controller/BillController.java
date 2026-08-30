package com.sunrise.sunrisedentalpms.controller;

import com.sunrise.sunrisedentalpms.exception.RecordNotFoundException;
import com.sunrise.sunrisedentalpms.exception.ValidationException;
import com.sunrise.sunrisedentalpms.model.Bill;
import com.sunrise.sunrisedentalpms.model.User;
import com.sunrise.sunrisedentalpms.service.BillingServiceInterface;
import com.sunrise.sunrisedentalpms.service.ServiceFactory;
import com.sunrise.sunrisedentalpms.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/bill")
public class BillController extends HttpServlet {

    private BillingServiceInterface billingService;

    public BillController() {
    }

    // for testing
    BillController(BillingServiceInterface billingService) {
        this.billingService = billingService;
    }

    @Override
    public void init() {
        if (billingService == null) {
            billingService = ServiceFactory.getBillingService();
        }
    }

    // Show bills
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessionUtil.getLoggedInUser(request) == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String billId = request.getParameter("billId");
        String appointmentNumber = request.getParameter("appointmentNumber");

        try {
            if (billId != null && !billId.isEmpty()) {
                Bill bill = billingService.findBillById(billId);
                request.setAttribute("bill", bill);
                request.getRequestDispatcher("billDetails.jsp").forward(request, response);
                return;
            }

            if (appointmentNumber != null && !appointmentNumber.isEmpty()) {
                Bill bill = billingService.findBillByAppointmentNumber(appointmentNumber);
                request.setAttribute("bill", bill);
                request.getRequestDispatcher("billDetails.jsp").forward(request, response);
                return;
            }

            List<Bill> bills = billingService.listAllBills();
            request.setAttribute("bills", bills);
            request.getRequestDispatcher("billList.jsp").forward(request, response);
        } catch (RecordNotFoundException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("billList.jsp").forward(request, response);
        }
    }

    // Generate bill
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loggedInUser = SessionUtil.getLoggedInUser(request);

        if (loggedInUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String appointmentNumber = request.getParameter("appointmentNumber");

        try {
            Bill created = billingService.generateBill(appointmentNumber, loggedInUser.getUserId());

            request.setAttribute("bill", created);
            request.setAttribute("successMessage", "Bill generated successfully.");
            request.getRequestDispatcher("billDetails.jsp").forward(request, response);
        } catch (RecordNotFoundException | ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("appointmentDetails.jsp").forward(request, response);
        }
    }
}