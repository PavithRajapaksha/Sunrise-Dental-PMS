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

  <title>Treatment Revenue Report | Sunrise Dental</title>

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
    <jsp:param name="pageTitle" value="Treatment Revenue"/>
  </jsp:include>

  <div class="page-content">

    <div class="page-header">

      <div>

        <h2>
          Treatment Revenue
        </h2>

        <p>
          Revenue report by treatment type.
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
               value="revenue">

        <div class="form-grid">

          <div class="form-group form-group-full">

            <label for="treatmentTypeId">
              Treatment Type
            </label>

            <select id="treatmentTypeId"
                    name="treatmentTypeId">

              <option value="">
                All Treatment Types
              </option>

              <c:forEach var="treatment"
                         items="${treatmentTypes}">

                <option value="${treatment.treatmentTypeId}"
                  ${selectedTreatmentTypeId eq treatment.treatmentTypeId
                          ? 'selected'
                          : ''}>

                  <c:out value="${treatment.treatmentTypeId}"/>
                  -
                  <c:out value="${treatment.name}"/>

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

          <a href="${pageContext.request.contextPath}/report?type=revenue&amp;format=pdf&amp;treatmentTypeId=${selectedTreatmentTypeId}"
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
            <th>Treatment ID</th>
            <th>Treatment</th>
            <th>Bills</th>
            <th>Revenue</th>
          </tr>

          </thead>

          <tbody>

          <c:choose>

            <c:when test="${not empty revenueReport}">

              <c:forEach var="row"
                         items="${revenueReport}">

                <tr>

                  <td>

                    <strong>
                      <c:out value="${row.treatmentTypeId}"/>
                    </strong>

                  </td>

                  <td>
                    <c:out value="${row.treatmentName}"/>
                  </td>

                  <td>
                    <c:out value="${row.billCount}"/>
                  </td>

                  <td>
                    Rs.
                    <c:out value="${row.totalRevenue}"/>
                  </td>

                </tr>

              </c:forEach>

            </c:when>

            <c:otherwise>

              <tr>

                <td colspan="4"
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