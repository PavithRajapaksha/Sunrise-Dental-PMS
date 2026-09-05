<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<header class="topbar">

    <div class="topbar-left">

        <button type="button"
                class="mobile-menu-button"
                id="mobileMenuButton"
                aria-label="Open navigation">
            ☰
        </button>

        <div>

            <p class="eyebrow">
                Sunrise Dental
            </p>

            <h1>
                <c:out value="${param.pageTitle}"/>
            </h1>

        </div>

    </div>

    <div class="topbar-user">

        <span class="status-dot"></span>

        <div>

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

</header>