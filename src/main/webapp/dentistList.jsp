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

    <title>Dentists | Sunrise Dental</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/styles.css">

    <script src="${pageContext.request.contextPath}/assets/js/app.js"
            defer></script>

</head>

<body class="dashboard-page">

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activePage" value="dentists"/>
</jsp:include>

<main class="dashboard-main">

    <jsp:include page="/WEB-INF/includes/topbar.jsp">
        <jsp:param name="pageTitle" value="Dentists"/>
    </jsp:include>

    <div class="page-content">

        <div class="page-header">

            <div>

                <h2>
                    Dentists
                </h2>

                <p>
                    View clinic dentists and their availability.
                </p>

            </div>

            <c:if test="${sessionScope.loggedInUser.role.name() eq 'ADMIN'}">

                <a href="${pageContext.request.contextPath}/registerDentist.jsp"
                   class="btn btn-primary">
                    Add Dentist
                </a>

            </c:if>

        </div>

        <jsp:include page="/WEB-INF/includes/messages.jsp"/>

        <div class="filter-row">

            <a href="${pageContext.request.contextPath}/dentist"
               class="filter-link ${param.availableOnly ne 'true' ? 'active' : ''}">
                All Dentists
            </a>

            <a href="${pageContext.request.contextPath}/dentist?availableOnly=true"
               class="filter-link ${param.availableOnly eq 'true' ? 'active' : ''}">
                Available Only
            </a>

        </div>

        <div class="data-card">

            <div class="table-responsive">

                <table class="data-table">

                    <thead>

                    <tr>
                        <th>Dentist ID</th>
                        <th>Name</th>
                        <th>Contact Number</th>
                        <th>Email</th>
                        <th>Status</th>
                        <th></th>
                    </tr>

                    </thead>

                    <tbody>

                    <c:choose>

                        <c:when test="${not empty dentists}">

                            <c:forEach var="dentist"
                                       items="${dentists}">

                                <tr>

                                    <td>

                                        <strong>
                                            <c:out value="${dentist.dentistId}"/>
                                        </strong>

                                    </td>

                                    <td>
                                        <c:out value="${dentist.name}"/>
                                    </td>

                                    <td>
                                        <c:out value="${dentist.contactNumber}"/>
                                    </td>

                                    <td>
                                        <c:out value="${dentist.email}"/>
                                    </td>

                                    <td>

                                        <c:choose>

                                            <c:when test="${dentist.status eq 'AVAILABLE'}">

                                                <span class="status-badge status-badge--success">
                                                    Available
                                                </span>

                                            </c:when>

                                            <c:otherwise>

                                                <span class="status-badge status-badge--muted">
                                                    Unavailable
                                                </span>

                                            </c:otherwise>

                                        </c:choose>

                                    </td>

                                    <td class="table-action">

                                        <a href="${pageContext.request.contextPath}/dentist?dentistId=${dentist.dentistId}"
                                           class="table-link">
                                            View
                                        </a>

                                    </td>

                                </tr>

                            </c:forEach>

                        </c:when>

                        <c:otherwise>

                            <tr>

                                <td colspan="6"
                                    class="empty-table">
                                    No dentists found.
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