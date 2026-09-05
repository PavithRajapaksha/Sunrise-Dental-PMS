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

  <title>Treatments | Sunrise Dental</title>

  <link rel="stylesheet"
        href="${pageContext.request.contextPath}/assets/css/styles.css">

  <script src="${pageContext.request.contextPath}/assets/js/app.js"
          defer></script>

</head>

<body class="dashboard-page">

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
  <jsp:param name="activePage" value="treatments"/>
</jsp:include>

<main class="dashboard-main">

  <jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageTitle" value="Treatments"/>
  </jsp:include>

  <div class="page-content">

    <div class="page-header">

      <div>

        <h2>
          Treatment Types
        </h2>

        <p>
          View clinic treatments and consultation fees.
        </p>

      </div>

      <c:if test="${sessionScope.loggedInUser.role.name() eq 'ADMIN'}">

        <a href="${pageContext.request.contextPath}/addTreatmentType.jsp"
           class="btn btn-primary">
          Add Treatment
        </a>

      </c:if>

    </div>

    <jsp:include page="/WEB-INF/includes/messages.jsp"/>

    <div class="data-card">

      <div class="table-responsive">

        <table class="data-table">

          <thead>

          <tr>
            <th>Treatment ID</th>
            <th>Treatment Name</th>
            <th>Consultation Fee</th>
            <th></th>
          </tr>

          </thead>

          <tbody>

          <c:choose>

            <c:when test="${not empty treatmentTypes}">

              <c:forEach var="treatment"
                         items="${treatmentTypes}">

                <tr>

                  <td>

                    <strong>
                      <c:out value="${treatment.treatmentTypeId}"/>
                    </strong>

                  </td>

                  <td>
                    <c:out value="${treatment.name}"/>
                  </td>

                  <td>
                    LKR
                    <c:out value="${treatment.consultationFee}"/>
                  </td>

                  <td class="table-action">

                    <a href="${pageContext.request.contextPath}/treatmentType?treatmentTypeId=${treatment.treatmentTypeId}"
                       class="table-link">
                      View
                    </a>

                  </td>

                </tr>

              </c:forEach>

            </c:when>

            <c:otherwise>

              <tr>

                <td colspan="4"
                    class="empty-table">
                  No treatment types found.
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