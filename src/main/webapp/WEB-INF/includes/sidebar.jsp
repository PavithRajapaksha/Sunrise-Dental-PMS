<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<aside class="sidebar"
       id="sidebar">

    <div class="sidebar-header">

        <div class="brand-logo">
            SD
        </div>

        <div class="brand-details">

            <strong>
                Sunrise Dental
            </strong>

            <span>
                Clinic PMS
            </span>

        </div>

    </div>

    <nav class="sidebar-nav">

        <p class="nav-section-label">
            Main
        </p>

        <a href="${pageContext.request.contextPath}/dashboard.jsp"
           class="nav-link ${param.activePage eq 'dashboard' ? 'active' : ''}">

            <span class="nav-icon">
                H
            </span>

            <span>
                Dashboard
            </span>

        </a>

        <a href="${pageContext.request.contextPath}/patient"
           class="nav-link ${param.activePage eq 'patients' ? 'active' : ''}">

            <span class="nav-icon">
                P
            </span>

            <span>
                Patients
            </span>

        </a>

        <a href="${pageContext.request.contextPath}/appointment"
           class="nav-link ${param.activePage eq 'appointments' ? 'active' : ''}">

            <span class="nav-icon">
                A
            </span>

            <span>
                Appointments
            </span>

        </a>

        <a href="${pageContext.request.contextPath}/bill"
           class="nav-link ${param.activePage eq 'billing' ? 'active' : ''}">

            <span class="nav-icon">
                B
            </span>

            <span>
                Billing
            </span>

        </a>

        <a href="${pageContext.request.contextPath}/dentist"
           class="nav-link ${param.activePage eq 'dentists' ? 'active' : ''}">

            <span class="nav-icon">
                D
            </span>

            <span>
                Dentists
            </span>

        </a>

        <a href="${pageContext.request.contextPath}/treatmentType"
           class="nav-link ${param.activePage eq 'treatments' ? 'active' : ''}">

            <span class="nav-icon">
                T
            </span>

            <span>
                Treatments
            </span>

        </a>

        <a href="${pageContext.request.contextPath}/help.jsp"
           class="nav-link ${param.activePage eq 'help' ? 'active' : ''}">

            <span class="nav-icon">
                ?
            </span>

            <span>
                Help
            </span>

        </a>

        <c:if test="${sessionScope.loggedInUser.role.name() eq 'ADMIN'}">

            <p class="nav-section-label nav-section-label--admin">
                Admin
            </p>

            <a href="${pageContext.request.contextPath}/user"
               class="nav-link ${param.activePage eq 'users' ? 'active' : ''}">

                <span class="nav-icon">
                    U
                </span>

                <span>
                    Staff Users
                </span>

            </a>

            <a href="${pageContext.request.contextPath}/report"
               class="nav-link ${param.activePage eq 'reports' ? 'active' : ''}">

                <span class="nav-icon">
                    R
                </span>

                <span>
                    Reports
                </span>

            </a>

        </c:if>

    </nav>

    <div class="sidebar-bottom">

        <div class="sidebar-user">

            <div class="user-avatar">

                <c:choose>

                    <c:when test="${sessionScope.loggedInUser.role.name() eq 'ADMIN'}">
                        A
                    </c:when>

                    <c:otherwise>
                        R
                    </c:otherwise>

                </c:choose>

            </div>

            <div class="sidebar-user-details">

                <strong>
                    <c:out value="${sessionScope.loggedInUser.name}"/>
                </strong>

                <span>

                    <c:choose>

                        <c:when test="${sessionScope.loggedInUser.role.name() eq 'ADMIN'}">
                            Admin
                        </c:when>

                        <c:otherwise>
                            Receptionist
                        </c:otherwise>

                    </c:choose>

                </span>

            </div>

        </div>

        <a href="${pageContext.request.contextPath}/logout"
           class="logout-link"
           id="logoutLink">

            <span class="nav-icon">
                L
            </span>

            <span>
                Logout
            </span>

        </a>

    </div>

</aside>

<div class="sidebar-overlay"
     id="sidebarOverlay">
</div>