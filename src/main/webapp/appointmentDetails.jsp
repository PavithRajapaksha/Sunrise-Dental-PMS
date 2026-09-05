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

    <title>Appointment Details | Sunrise Dental</title>

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
        <jsp:param name="pageTitle" value="Appointment Details"/>
    </jsp:include>

    <div class="page-content">

        <div class="page-header">

            <div>

                <h2>
                    Appointment Details
                </h2>

                <p>
                    Appointment information.
                </p>

            </div>

            <a href="${pageContext.request.contextPath}/appointment"
               class="btn btn-secondary">
                Back to Appointments
            </a>

        </div>

        <jsp:include page="/WEB-INF/includes/messages.jsp"/>

        <c:choose>

            <c:when test="${not empty appointment}">

                <div class="details-card">

                    <div class="details-grid">

                        <div class="detail-item">

                            <span>
                                Appointment Number
                            </span>

                            <strong>
                                <c:out value="${appointment.appointmentNumber}"/>
                            </strong>

                        </div>

                        <div class="detail-item">

                            <span>
                                Status
                            </span>

                            <strong>

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

                            </strong>

                        </div>

                        <div class="detail-item">

                            <span>
                                Patient
                            </span>

                            <strong>
                                <c:out value="${appointment.patient.name}"/>
                            </strong>

                        </div>

                        <div class="detail-item">

                            <span>
                                Patient ID
                            </span>

                            <strong>
                                <c:out value="${appointment.patient.patientId}"/>
                            </strong>

                        </div>

                        <div class="detail-item">

                            <span>
                                Contact Number
                            </span>

                            <strong>
                                <c:out value="${appointment.patient.contactNumber}"/>
                            </strong>

                        </div>

                        <div class="detail-item">

                            <span>
                                Email
                            </span>

                            <strong>
                                <c:out value="${appointment.patient.email}"/>
                            </strong>

                        </div>

                        <div class="detail-item detail-item-full">

                            <span>
                                Address
                            </span>

                            <strong>
                                <c:out value="${appointment.patient.address}"/>
                            </strong>

                        </div>

                        <div class="detail-item">

                            <span>
                                Dentist
                            </span>

                            <strong>
                                <c:out value="${appointment.dentist.name}"/>
                            </strong>

                        </div>

                        <div class="detail-item">

                            <span>
                                Treatment
                            </span>

                            <strong>
                                <c:out value="${appointment.treatmentType.name}"/>
                            </strong>

                        </div>

                        <div class="detail-item">

                            <span>
                                Appointment Date / Time
                            </span>

                            <strong>
                                <c:out value="${appointment.appointmentDateTime}"/>
                            </strong>

                        </div>

                        <div class="detail-item">

                            <span>
                                Consultation Fee
                            </span>

                            <strong>
                                Rs.
                                <c:out value="${appointment.treatmentType.consultationFee}"/>
                            </strong>

                        </div>

                        <div class="detail-item">

                            <span>
                                Booked By User ID
                            </span>

                            <strong>
                                <c:out value="${appointment.bookedByUserId}"/>
                            </strong>

                        </div>

                    </div>

                </div>

                <div class="section-heading">

                    <h2>
                        Actions
                    </h2>

                </div>

                <div class="form-card">

                    <div class="form-actions">

                        <a href="${pageContext.request.contextPath}/appointment"
                           class="btn btn-secondary">
                            Back to Appointments
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
                                        class="btn btn-danger">
                                    Cancel Appointment
                                </button>

                            </form>

                        </c:if>

                    </div>

                </div>

            </c:when>

            <c:otherwise>

                <div class="form-alert form-alert--error">

                    <span class="form-alert-icon">
                        !
                    </span>

                    <div>

                        <strong>
                            Appointment not found
                        </strong>

                        <p>
                            The requested appointment could not be found.
                        </p>

                    </div>

                </div>

            </c:otherwise>

        </c:choose>

    </div>

    <footer class="dashboard-footer">

        <p>
            Sunrise Dental Patient Management System
        </p>

    </footer>

</main>

</body>

</html>