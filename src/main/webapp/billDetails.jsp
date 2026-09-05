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

    <title>Bill Details | Sunrise Dental</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/styles.css">

    <script src="${pageContext.request.contextPath}/assets/js/app.js"
            defer></script>

</head>

<body class="dashboard-page">

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activePage" value="billing"/>
</jsp:include>

<main class="dashboard-main">

    <jsp:include page="/WEB-INF/includes/topbar.jsp">
        <jsp:param name="pageTitle" value="Bill Details"/>
    </jsp:include>

    <div class="page-content">

        <div class="page-header">

            <div>

                <h2>
                    Bill Details
                </h2>

                <p>
                    Billing information.
                </p>

            </div>

            <a href="${pageContext.request.contextPath}/bill"
               class="btn btn-secondary">
                Back to Billing
            </a>

        </div>

        <jsp:include page="/WEB-INF/includes/messages.jsp"/>

        <c:choose>

            <c:when test="${not empty bill}">

                <div class="details-card">

                    <div class="details-grid">

                        <div class="detail-item">

                            <span>
                                Bill ID
                            </span>

                            <strong>
                                <c:out value="${bill.billId}"/>
                            </strong>

                        </div>

                        <div class="detail-item">

                            <span>
                                Status
                            </span>

                            <strong>

                                <c:choose>

                                    <c:when test="${bill.status.name() eq 'PAID'}">

                                        <span class="status-badge status-badge--success">
                                            Paid
                                        </span>

                                    </c:when>

                                    <c:otherwise>

                                        <span class="status-badge">
                                            Pending
                                        </span>

                                    </c:otherwise>

                                </c:choose>

                            </strong>

                        </div>

                        <div class="detail-item">

                            <span>
                                Appointment Number
                            </span>

                            <strong>
                                <c:out value="${bill.appointment.appointmentNumber}"/>
                            </strong>

                        </div>

                        <div class="detail-item">

                            <span>
                                Patient
                            </span>

                            <strong>
                                <c:out value="${bill.appointment.patient.name}"/>
                            </strong>

                        </div>

                        <div class="detail-item">

                            <span>
                                Dentist
                            </span>

                            <strong>
                                <c:out value="${bill.appointment.dentist.name}"/>
                            </strong>

                        </div>

                        <div class="detail-item">

                            <span>
                                Treatment
                            </span>

                            <strong>
                                <c:out value="${bill.appointment.treatmentType.name}"/>
                            </strong>

                        </div>

                        <div class="detail-item">

                            <span>
                                Total Amount
                            </span>

                            <strong>
                                Rs.
                                <c:out value="${bill.totalAmount}"/>
                            </strong>

                        </div>

                        <div class="detail-item">

                            <span>
                                Generated Date
                            </span>

                            <strong>
                                <c:out value="${bill.generatedDate}"/>
                            </strong>

                        </div>

                        <div class="detail-item">

                            <span>
                                Payment Type
                            </span>

                            <strong>

                                <c:choose>

                                    <c:when test="${not empty bill.paymentType}">

                                        <c:choose>

                                            <c:when test="${bill.paymentType.name() eq 'CASH'}">
                                                Cash
                                            </c:when>

                                            <c:when test="${bill.paymentType.name() eq 'CARD'}">
                                                Card
                                            </c:when>

                                            <c:otherwise>
                                                <c:out value="${bill.paymentType}"/>
                                            </c:otherwise>

                                        </c:choose>

                                    </c:when>

                                    <c:otherwise>
                                        Not recorded
                                    </c:otherwise>

                                </c:choose>

                            </strong>

                        </div>

                        <div class="detail-item">

                            <span>
                                Appointment Status
                            </span>

                            <strong>

                                <c:choose>

                                    <c:when test="${bill.appointment.status.name() eq 'COMPLETED'}">

                                        <span class="status-badge status-badge--success">
                                            Completed
                                        </span>

                                    </c:when>

                                    <c:when test="${bill.appointment.status.name() eq 'CANCELLED'}">

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
                                Generated By
                            </span>

                            <strong>
                                <c:out value="${bill.generatedByUserId}"/>
                            </strong>

                        </div>

                    </div>

                </div>

                <div class="section-heading">

                    <h2>
                        Bill Actions
                    </h2>

                </div>

                <div class="form-card">

                    <div class="form-actions">

                        <a href="${pageContext.request.contextPath}/appointment?appointmentNumber=${bill.appointment.appointmentNumber}"
                           class="btn btn-secondary">
                            View Appointment
                        </a>

                        <a href="${pageContext.request.contextPath}/bill?billId=${bill.billId}&format=pdf"
                           class="btn btn-primary"
                           target="_blank">
                            View PDF
                        </a>

                        <button type="button"
                                class="btn btn-secondary"
                                onclick="window.print()">
                            Print
                        </button>

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
                            Bill not found
                        </strong>

                        <p>
                            The requested bill could not be found.
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