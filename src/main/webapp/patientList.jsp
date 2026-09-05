<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${empty sessionScope.loggedInUser}">
  <c:redirect url="/login"/>
</c:if>

<!DOCTYPE html>
<html lang="en">

<head>

  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">

  <title>Patients | Sunrise Dental</title>

  <link rel="stylesheet"
        href="${pageContext.request.contextPath}/assets/css/styles.css">

  <script src="${pageContext.request.contextPath}/assets/js/app.js"
          defer></script>

</head>

<body class="dashboard-page">

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
  <jsp:param name="activePage" value="patients"/>
</jsp:include>

<main class="dashboard-main">

  <jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageTitle" value="Patients"/>
  </jsp:include>

  <div class="page-content">

    <div class="page-header">

      <div>
        <h2>Patients</h2>
        <p>View and find patients.</p>
      </div>

      <a href="${pageContext.request.contextPath}/registerPatient.jsp"
         class="btn btn-primary">
        Add Patient
      </a>

    </div>

    <jsp:include page="/WEB-INF/includes/messages.jsp"/>

    <div class="search-row">

      <form method="get"
            action="${pageContext.request.contextPath}/patient"
            class="search-form">

        <input type="text"
               name="patientId"
               placeholder="Patient ID"
               required>

        <button type="submit"
                class="btn btn-secondary">
          Find
        </button>

      </form>

      <form method="get"
            action="${pageContext.request.contextPath}/patient"
            class="search-form">

        <input type="text"
               name="contactNumber"
               placeholder="Contact number"
               pattern="0[0-9]{9}"
               required>

        <button type="submit"
                class="btn btn-secondary">
          Find
        </button>

      </form>

    </div>

    <div class="data-card">

      <div class="table-responsive">

        <table class="data-table">

          <thead>

          <tr>
            <th>Patient ID</th>
            <th>Name</th>
            <th>Contact</th>
            <th>Email</th>
            <th></th>
          </tr>

          </thead>

          <tbody>

          <c:forEach var="patient"
                     items="${patients}">

            <tr>

              <td>
                <c:out value="${patient.patientId}"/>
              </td>

              <td>
                <strong>
                  <c:out value="${patient.name}"/>
                </strong>
              </td>

              <td>
                <c:out value="${patient.contactNumber}"/>
              </td>

              <td>
                <c:choose>
                  <c:when test="${not empty patient.email}">
                    <c:out value="${patient.email}"/>
                  </c:when>
                  <c:otherwise>
                    -
                  </c:otherwise>
                </c:choose>
              </td>

              <td class="table-action">

                <a href="${pageContext.request.contextPath}/patient?patientId=${patient.patientId}"
                   class="table-link">
                  View
                </a>

              </td>

            </tr>

          </c:forEach>

          <c:if test="${empty patients}">

            <tr>
              <td colspan="5"
                  class="empty-table">
                No patients found.
              </td>
            </tr>

          </c:if>

          </tbody>

        </table>

      </div>

    </div>

  </div>

</main>

</body>
</html>