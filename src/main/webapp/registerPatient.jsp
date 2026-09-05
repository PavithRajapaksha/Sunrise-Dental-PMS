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

  <title>Register Patient | Sunrise Dental</title>

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
    <jsp:param name="pageTitle" value="Register Patient"/>
  </jsp:include>

  <div class="page-content">

    <div class="page-header">

      <div>

        <h2>
          Register Patient
        </h2>

        <c:choose>

          <c:when test="${param.returnTo eq 'appointment'}">

            <p>
              Register the patient to continue booking the appointment.
            </p>

          </c:when>

          <c:otherwise>

            <p>
              Add a new patient to the clinic.
            </p>

          </c:otherwise>

        </c:choose>

      </div>

      <c:choose>

        <c:when test="${param.returnTo eq 'appointment'}">

          <a href="${pageContext.request.contextPath}/appointment?action=book"
             class="btn btn-secondary">
            Back to Appointment
          </a>

        </c:when>

        <c:otherwise>

          <a href="${pageContext.request.contextPath}/patient"
             class="btn btn-secondary">
            Back to Patients
          </a>

        </c:otherwise>

      </c:choose>

    </div>

    <jsp:include page="/WEB-INF/includes/messages.jsp"/>

    <div class="form-card">

      <form method="post"
            action="${pageContext.request.contextPath}/patient">

        <c:if test="${param.returnTo eq 'appointment'}">

          <input type="hidden"
                 name="returnTo"
                 value="appointment">

        </c:if>

        <div class="form-grid">

          <div class="form-group form-group-full">

            <label for="name">
              Full Name
            </label>

            <input type="text"
                   id="name"
                   name="name"
                   value="<c:out value='${param.name}'/>"
                   placeholder="Enter patient name"
                   required>

          </div>

          <div class="form-group">

            <label for="contactNumber">
              Contact Number
            </label>

            <input type="tel"
                   id="contactNumber"
                   name="contactNumber"
                   value="<c:out value='${param.contactNumber}'/>"
                   placeholder="Enter contact number"
                   required>

          </div>

          <div class="form-group">

            <label for="email">
              Email
            </label>

            <input type="email"
                   id="email"
                   name="email"
                   value="<c:out value='${param.email}'/>"
                   placeholder="Enter email address"
                   required>

          </div>

          <div class="form-group form-group-full">

            <label for="address">
              Address
            </label>

            <textarea id="address"
                      name="address"
                      rows="4"
                      placeholder="Enter patient address"
                      required><c:out value="${param.address}"/></textarea>

          </div>

        </div>

        <div class="form-actions">

          <c:choose>

            <c:when test="${param.returnTo eq 'appointment'}">

              <a href="${pageContext.request.contextPath}/appointment?action=book"
                 class="btn btn-secondary">
                Cancel
              </a>

            </c:when>

            <c:otherwise>

              <a href="${pageContext.request.contextPath}/patient"
                 class="btn btn-secondary">
                Cancel
              </a>

            </c:otherwise>

          </c:choose>

          <button type="submit"
                  class="btn btn-primary">
            Register Patient
          </button>

        </div>

      </form>

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