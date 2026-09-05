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

  <title>Reports | Sunrise Dental</title>

  <link rel="stylesheet"
        href="${pageContext.request.contextPath}/assets/css/styles.css">

  <script src="${pageContext.request.contextPath}/assets/js/app.js"
          defer></script>

</head>

<body class="dashboard-page">

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
  <jsp:param name="activePage" value="reports"/>
</jsp:include>

<main class="dashboard-main">

  <jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageTitle" value="Reports"/>
  </jsp:include>

  <div class="page-content">

    <div class="page-header">

      <div>

        <h2>
          Reports
        </h2>

        <p>
          Clinic reporting.
        </p>

      </div>

    </div>

    <jsp:include page="/WEB-INF/includes/messages.jsp"/>

    <c:if test="${sessionScope.loggedInUser.role.name() eq 'ADMIN'}">

      <div class="module-grid">

        <a href="${pageContext.request.contextPath}/report?type=revenue"
           class="module-card module-card-link">

          <div class="module-card-icon">
            TR
          </div>

          <h3>
            Treatment Revenue
          </h3>

          <p>
            Revenue totals by treatment type.
          </p>

          <span class="module-link-text">
                        View Report
                    </span>

        </a>

        <a href="${pageContext.request.contextPath}/report?type=workload"
           class="module-card module-card-link">

          <div class="module-card-icon">
            DW
          </div>

          <h3>
            Dentist Workload
          </h3>

          <p>
            Appointment totals by dentist.
          </p>

          <span class="module-link-text">
                        View Report
                    </span>

        </a>

      </div>

    </c:if>

  </div>

  <footer class="dashboard-footer">

    <p>
      Sunrise Dental Patient Management System
    </p>

  </footer>

</main>

</body>

</html>