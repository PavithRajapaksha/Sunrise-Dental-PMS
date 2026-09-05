<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${empty sessionScope.loggedInUser}">
    <c:redirect url="/login"/>
</c:if>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Appointments | Sunrise Dental</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/styles.css">

    <script src="${pageContext.request.contextPath}/assets/js/app.js"
            defer></script>

</head>

<body class="dashboard-page">

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activePage" value="appointments"/>
</jsp:include>

<main class="dashboard-main">

    <jsp:include page="/WEB-INF/includes/topbar.jsp">
        <jsp:param name="pageTitle" value="Appointments"/>
    </jsp:include>

    <div class="page-content">

        <div class="page-header">

            <div>

                <h2>
                    Appointments
                </h2>

                <p>
                    View and manage clinic appointments.
                </p>

            </div>

            <a href="${pageContext.request.contextPath}/appointment?action=book"
               class="btn btn-primary">
                Book Appointment
            </a>

        </div>

        <jsp:include page="/WEB-INF/includes/messages.jsp"/>

        <div class="section-heading">

            <h2>
                Find Appointment
            </h2>

            <p>
                Search directly using an appointment number.
            </p>

        </div>

        <form method="get"
              action="${pageContext.request.contextPath}/appointment"
              class="search-form search-form-single">

            <input type="text"
                   name="appointmentNumber"
                   placeholder="Enter appointment number"
                   value="<c:out value='${param.appointmentNumber}'/>"
                   required>

            <button type="submit"
                    class="btn btn-secondary">
                Search
            </button>

        </form>

        <div class="section-heading">

            <h2>
                Find Patient Appointments
            </h2>

            <p>
                Search using either the patient ID or contact number.
            </p>

        </div>

        <div class="search-row">

            <form method="get"
                  action="${pageContext.request.contextPath}/appointment"
                  class="search-form">

                <input type="text"
                       name="patientId"
                       placeholder="Enter patient ID"
                       value="<c:out value='${param.patientId}'/>"
                       required>

                <button type="submit"
                        class="btn btn-secondary">
                    Search
                </button>

            </form>

            <form method="get"
                  action="${pageContext.request.contextPath}/appointment"
                  class="search-form">

                <input type="text"
                       name="contactNumber"
                       placeholder="Enter patient contact number"
                       value="<c:out value='${param.contactNumber}'/>"
                       required>

                <button type="submit"
                        class="btn btn-secondary">
                    Search
                </button>

            </form>

        </div>

        <c:if test="${not empty searchedPatient}">

            <div class="section-heading">

                <h2>
                    Appointments for
                    <c:out value="${searchedPatient.name}"/>
                </h2>

                <p>
                    Patient ID:
                    <c:out value="${searchedPatient.patientId}"/>
                    &nbsp;•&nbsp;
                    Contact:
                    <c:out value="${searchedPatient.contactNumber}"/>
                </p>

            </div>

        </c:if>

        <c:if test="${not empty param.patientId
                    or not empty param.contactNumber
                    or not empty param.appointmentNumber}">

            <div class="filter-row">

                <a href="${pageContext.request.contextPath}/appointment"
                   class="filter-link">
                    Show All Appointments
                </a>

            </div>

        </c:if>

        <div class="data-card">

            <div class="table-responsive">

                <table class="data-table">

                    <thead>

                    <tr>
                        <th>Appointment</th>
                        <th>Patient</th>
                        <th>Dentist</th>
                        <th>Treatment</th>
                        <th>Date / Time</th>
                        <th>Status</th>
                        <th></th>
                    </tr>

                    </thead>

                    <tbody>

                    <c:choose>

                        <c:when test="${not empty appointments}">

                            <c:forEach var="appointment"
                                       items="${appointments}">

                                <tr>

                                    <td>

                                        <strong>
                                            <c:out value="${appointment.appointmentNumber}"/>
                                        </strong>

                                    </td>

                                    <td>

                                        <c:out value="${appointment.patient.name}"/>

                                        <br>

                                        <span>
                                            <c:out value="${appointment.patient.patientId}"/>
                                        </span>

                                    </td>

                                    <td>
                                        <c:out value="${appointment.dentist.name}"/>
                                    </td>

                                    <td>
                                        <c:out value="${appointment.treatmentType.name}"/>
                                    </td>

                                    <td>
                                        <c:out value="${appointment.appointmentDateTime}"/>
                                    </td>

                                    <td>

                                        <c:choose>

                                            <c:when test="${appointment.status.name() eq 'COMPLETED'}">

                                                <span class="status-badge status-badge--success">
                                                    Completed
                                                </span>

                                            </c:when>

                                            <c:when test="${appointment.status.name() eq 'CANCELLED'}">

                                                <span class="status-badge status-badge--muted">
                                                    Cancelled
                                                </span>

                                            </c:when>

                                            <c:otherwise>

                                                <span class="status-badge">
                                                    Scheduled
                                                </span>

                                            </c:otherwise>

                                        </c:choose>

                                    </td>

                                    <td class="table-action">

                                        <a href="${pageContext.request.contextPath}/appointment?appointmentNumber=${appointment.appointmentNumber}"
                                           class="table-link">
                                            View
                                        </a>

                                        <c:if test="${appointment.status.name() eq 'SCHEDULED'}">

                                            <form method="post"
                                                  action="${pageContext.request.contextPath}/appointment"
                                                  class="inline-action-form"
                                                  onsubmit="return confirm('Are you sure you want to cancel appointment ${appointment.appointmentNumber}?');">

                                                <input type="hidden"
                                                       name="action"
                                                       value="cancel">

                                                <input type="hidden"
                                                       name="appointmentNumber"
                                                       value="${appointment.appointmentNumber}">

                                                <button type="submit"
                                                        class="table-link table-link-danger">
                                                    Cancel
                                                </button>

                                            </form>

                                        </c:if>

                                    </td>

                                </tr>

                            </c:forEach>

                        </c:when>

                        <c:otherwise>

                            <tr>

                                <td colspan="7"
                                    class="empty-table">

                                    <c:choose>

                                        <c:when test="${not empty searchedPatient}">
                                            No appointments found for this patient.
                                        </c:when>

                                        <c:otherwise>
                                            No appointments found.
                                        </c:otherwise>

                                    </c:choose>

                                </td>

                            </tr>

                        </c:otherwise>

                    </c:choose>

                    </tbody>

                </table>

            </div>

        </div>

    </div>

    <footer class="dashboard-footer">

        <p>
            Sunrise Dental Patient Management System
        </p>

    </footer>

</main>

</body>

</html>