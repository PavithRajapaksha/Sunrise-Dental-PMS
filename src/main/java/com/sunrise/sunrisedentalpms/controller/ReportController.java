package com.sunrise.sunrisedentalpms.controller;

import com.sunrise.sunrisedentalpms.exception.AuthorizationException;
import com.sunrise.sunrisedentalpms.model.Dentist;
import com.sunrise.sunrisedentalpms.model.DentistWorkloadReport;
import com.sunrise.sunrisedentalpms.model.TreatmentRevenueReport;
import com.sunrise.sunrisedentalpms.model.TreatmentType;
import com.sunrise.sunrisedentalpms.model.User;
import com.sunrise.sunrisedentalpms.model.UserRole;
import com.sunrise.sunrisedentalpms.service.DentistServiceInterface;
import com.sunrise.sunrisedentalpms.service.ReportServiceInterface;
import com.sunrise.sunrisedentalpms.service.ServiceFactory;
import com.sunrise.sunrisedentalpms.service.TreatmentTypeServiceInterface;
import com.sunrise.sunrisedentalpms.util.ReportPdfGenerator;
import com.sunrise.sunrisedentalpms.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/report")
public class ReportController extends HttpServlet {

    private ReportServiceInterface reportService;

    private DentistServiceInterface dentistService;

    private TreatmentTypeServiceInterface
            treatmentTypeService;

    public ReportController() {
    }

    ReportController(
            ReportServiceInterface reportService) {

        this.reportService =
                reportService;
    }

    ReportController(
            ReportServiceInterface reportService,
            DentistServiceInterface dentistService,
            TreatmentTypeServiceInterface treatmentTypeService) {

        this.reportService =
                reportService;

        this.dentistService =
                dentistService;

        this.treatmentTypeService =
                treatmentTypeService;
    }

    @Override
    public void init() {

        if (reportService == null) {

            reportService =
                    ServiceFactory
                            .getReportService();
        }

        if (dentistService == null) {

            dentistService =
                    ServiceFactory
                            .getDentistService();
        }

        if (treatmentTypeService == null) {

            treatmentTypeService =
                    ServiceFactory
                            .getTreatmentTypeService();
        }
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException,
            IOException {

        User loggedInUser =
                SessionUtil.getLoggedInUser(
                        request
                );

        if (loggedInUser == null) {

            response.sendRedirect(
                    "login.jsp"
            );

            return;
        }

        if (loggedInUser.getRole()
                != UserRole.ADMIN) {

            request.setAttribute(
                    "errorMessage",
                    "Only an admin can view reports."
            );

            request.getRequestDispatcher(
                    "reportMenu.jsp"
            ).forward(
                    request,
                    response
            );

            return;
        }

        String type =
                normalize(
                        request.getParameter(
                                "type"
                        )
                );

        String format =
                normalize(
                        request.getParameter(
                                "format"
                        )
                );

        try {

            if ("revenue".equals(type)) {

                showRevenueReport(
                        request,
                        response,
                        loggedInUser,
                        format
                );

                return;
            }

            if ("workload".equals(type)) {

                showWorkloadReport(
                        request,
                        response,
                        loggedInUser,
                        format
                );

                return;
            }

            request.getRequestDispatcher(
                    "reportMenu.jsp"
            ).forward(
                    request,
                    response
            );

        } catch (AuthorizationException e) {

            request.setAttribute(
                    "errorMessage",
                    e.getMessage()
            );

            request.getRequestDispatcher(
                    "reportMenu.jsp"
            ).forward(
                    request,
                    response
            );
        }
    }

    private void showRevenueReport(
            HttpServletRequest request,
            HttpServletResponse response,
            User loggedInUser,
            String format)
            throws ServletException,
            IOException,
            AuthorizationException {

        String treatmentTypeId =
                normalize(
                        request.getParameter(
                                "treatmentTypeId"
                        )
                );

        List<TreatmentType> treatmentTypes =
                treatmentTypeService
                        .listAllTreatmentTypes();

        List<TreatmentRevenueReport> report =
                reportService
                        .getRevenueByTreatmentType(
                                loggedInUser.getRole(),
                                treatmentTypeId
                        );

        String filterLabel =
                findTreatmentLabel(
                        treatmentTypes,
                        treatmentTypeId
                );

        if ("pdf".equalsIgnoreCase(format)) {

            byte[] pdf =
                    ReportPdfGenerator
                            .generateRevenueReport(
                                    report,
                                    filterLabel
                            );

            streamPdf(
                    response,
                    pdf,
                    "treatment-revenue-report.pdf"
            );

            return;
        }

        request.setAttribute(
                "treatmentTypes",
                treatmentTypes
        );

        request.setAttribute(
                "revenueReport",
                report
        );

        request.setAttribute(
                "selectedTreatmentTypeId",
                treatmentTypeId
        );

        request.getRequestDispatcher(
                "revenueReport.jsp"
        ).forward(
                request,
                response
        );
    }

    private void showWorkloadReport(
            HttpServletRequest request,
            HttpServletResponse response,
            User loggedInUser,
            String format)
            throws ServletException,
            IOException,
            AuthorizationException {

        String dentistId =
                normalize(
                        request.getParameter(
                                "dentistId"
                        )
                );

        List<Dentist> dentists =
                dentistService
                        .listAllDentists();

        List<DentistWorkloadReport> report =
                reportService
                        .getDentistWorkload(
                                loggedInUser.getRole(),
                                dentistId
                        );

        String filterLabel =
                findDentistLabel(
                        dentists,
                        dentistId
                );

        if ("pdf".equalsIgnoreCase(format)) {

            byte[] pdf =
                    ReportPdfGenerator
                            .generateDentistWorkloadReport(
                                    report,
                                    filterLabel
                            );

            streamPdf(
                    response,
                    pdf,
                    "dentist-workload-report.pdf"
            );

            return;
        }

        request.setAttribute(
                "dentists",
                dentists
        );

        request.setAttribute(
                "workloadReport",
                report
        );

        request.setAttribute(
                "selectedDentistId",
                dentistId
        );

        request.getRequestDispatcher(
                "workloadReport.jsp"
        ).forward(
                request,
                response
        );
    }

    private String findTreatmentLabel(
            List<TreatmentType> treatmentTypes,
            String treatmentTypeId) {

        if (treatmentTypeId == null) {
            return "All Treatment Types";
        }

        return treatmentTypes
                .stream()
                .filter(
                        treatment ->
                                treatment
                                        .getTreatmentTypeId()
                                        .equals(
                                                treatmentTypeId
                                        )
                )
                .map(TreatmentType::getName)
                .findFirst()
                .orElse(
                        "Treatment ID "
                                + treatmentTypeId
                );
    }

    private String findDentistLabel(
            List<Dentist> dentists,
            String dentistId) {

        if (dentistId == null) {
            return "All Dentists";
        }

        return dentists
                .stream()
                .filter(
                        dentist ->
                                dentist
                                        .getDentistId()
                                        .equals(
                                                dentistId
                                        )
                )
                .map(Dentist::getName)
                .findFirst()
                .orElse(
                        "Dentist ID "
                                + dentistId
                );
    }

    private void streamPdf(
            HttpServletResponse response,
            byte[] pdf,
            String fileName)
            throws IOException {

        response.setContentType(
                "application/pdf"
        );

        response.setHeader(
                "Content-Disposition",
                "attachment; filename="
                        + fileName
        );

        response.setContentLength(
                pdf.length
        );

        response
                .getOutputStream()
                .write(
                        pdf
                );

        response
                .getOutputStream()
                .flush();
    }

    private String normalize(
            String value) {

        if (value == null
                || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }
}