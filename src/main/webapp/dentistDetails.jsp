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

  <title>Dentist Details | Sunrise Dental</title>

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
    <jsp:param name="pageTitle" value="Dentist Details"/>
  </jsp:include>

  <div class="page-content">

    <div class="page-header">

      <div>

        <h2>
          Dr. <c:out value="${dentist.name}"/>
        </h2>

        <p>
          Dentist ID:
          <c:out value="${dentist.dentistId}"/>
        </p>

      </div>

      <a href="${pageContext.request.contextPath}/dentist"
         class="btn btn-secondary">
        Back
      </a>

    </div>

    <jsp:include page="/WEB-INF/includes/messages.jsp"/>

    <div class="details-card">

      <div class="details-grid">

        <div class="detail-item">

                    <span>
                        Dentist ID
                    </span>

          <strong>
            <c:out value="${dentist.dentistId}"/>
          </strong>

        </div>

        <div class="detail-item">

                    <span>
                        Name
                    </span>

          <strong>
            Dr. <c:out value="${dentist.name}"/>
          </strong>

        </div>

        <div class="detail-item">

                    <span>
                        Contact Number
                    </span>

          <strong>
            <c:out value="${dentist.contactNumber}"/>
          </strong>

        </div>

        <div class="detail-item">

                    <span>
                        Email
                    </span>

          <strong>

            <c:choose>

              <c:when test="${not empty dentist.email}">
                <c:out value="${dentist.email}"/>
              </c:when>

              <c:otherwise>
                -
              </c:otherwise>

            </c:choose>

          </strong>

        </div>

        <div class="detail-item">

                    <span>
                        Status
                    </span>

          <strong>

                        <span class="status-badge ${dentist.status.name() eq 'AVAILABLE' ? 'status-badge--success' : 'status-badge--muted'}">

                            <c:out value="${dentist.status}"/>

                        </span>

          </strong>

        </div>

      </div>

    </div>

    <div class="section-heading">

      <h2>
        Change Status
      </h2>

      <p>
        Set the dentist as available or unavailable.
      </p>

    </div>

    <form method="post"
          action="${pageContext.request.contextPath}/dentist"
          class="form-card">

      <input type="hidden"
             name="action"
             value="updateStatus">

      <input type="hidden"
             name="dentistId"
             value="<c:out value='${dentist.dentistId}'/>">

      <div class="form-grid">

        <div class="form-group form-group-full">

          <label for="status">
            Status
          </label>

          <select id="status"
                  name="status"
                  required>

            <option value="AVAILABLE"
            ${dentist.status.name() eq 'AVAILABLE' ? 'selected' : ''}>
              Available
            </option>

            <option value="UNAVAILABLE"
            ${dentist.status.name() eq 'UNAVAILABLE' ? 'selected' : ''}>
              Unavailable
            </option>

          </select>

        </div>

      </div>

      <div class="form-actions">

        <a href="${pageContext.request.contextPath}/dentist"
           class="btn btn-secondary">
          Cancel
        </a>

        <button type="submit"
                class="btn btn-primary">
          Update Status
        </button>

      </div>

    </form>

  </div>

</main>

</body>

</html>