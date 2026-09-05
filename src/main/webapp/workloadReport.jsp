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

  <title>Dentist Workload Report | Sunrise Dental</title>

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
    <jsp:param name="pageTitle" value="Dentist Workload"/>
  </jsp:include>

  <div class="page-content">

    <div class="page-header">

      <div>

        <h2>
          Dentist Workload
        </h2>

        <p>
          Appointment totals by dentist.
        </p>

      </div>

      <a href="${pageContext.request.contextPath}/report"
         class="btn btn-secondary">
        Back to Reports
      </a>

    </div>

    <jsp:include page="/WEB-INF/includes/messages.jsp"/>

    <div class="form-card">

      <form method="get"
            action="${pageContext.request.contextPath}/report">

        <input type="hidden"
               name="type"
               value="workload">

        <div class="form-grid">

          <div class="form-group form-group-full">

            <label for="dentistId">
              Dentist
            </label>

            <select id="dentistId"
                    name="dentistId">

              <option value="">
                All Dentists
              </option>

              <c:forEach var="dentist"
                         items="${dentists}">

                <option value="${dentist.dentistId}"
                  ${selectedDentistId eq dentist.dentistId
                          ? 'selected'
                          : ''}>

                  <c:out value="${dentist.dentistId}"/>
                  -
                  <c:out value="${dentist.name}"/>

                </option>

              </c:forEach>

            </select>

          </div>

        </div>

        <div class="form-actions">

          <button type="submit"
                  class="btn btn-primary">
            Apply
          </button>

          <a href="${pageContext.request.contextPath}/report?type=workload&amp;format=pdf&amp;dentistId=${selectedDentistId}"
             class="btn btn-secondary">
            Save as PDF
          </a>

        </div>

      </form>

    </div>

    <div class="section-heading">

      <h2>
        Results
      </h2>

    </div>

    <div class="data-card">

      <div class="table-responsive">

        <table class="data-table">

          <thead>

          <tr>
            <th>Dentist ID</th>
            <th>Dentist</th>
            <th>Appointments</th>
          </tr>

          </thead>

          <tbody>

          <c:choose>

            <c:when test="${not empty workloadReport}">

              <c:forEach var="row"
                         items="${workloadReport}">

                <tr>

                  <td>

                    <strong>
                      <c:out value="${row.dentistId}"/>
                    </strong>

                  </td>

                  <td>
                    <c:out value="${row.dentistName}"/>
                  </td>

                  <td>
                    <c:out value="${row.appointmentCount}"/>
                  </td>

                </tr>

              </c:forEach>

            </c:when>

            <c:otherwise>

              <tr>

                <td colspan="3"
                    class="empty-table">
                  No report data found.
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