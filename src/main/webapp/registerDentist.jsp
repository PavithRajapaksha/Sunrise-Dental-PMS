<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${empty sessionScope.loggedInUser}">
  <c:redirect url="/login"/>
</c:if>

<c:if test="${sessionScope.loggedInUser.role.name() ne 'ADMIN'}">
  <c:redirect url="/dashboard.jsp"/>
</c:if>

<!DOCTYPE html>
<html lang="en">

<head>

  <meta charset="UTF-8">

  <meta name="viewport"
        content="width=device-width, initial-scale=1.0">

  <title>Add Dentist | Sunrise Dental</title>

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
    <jsp:param name="pageTitle" value="Add Dentist"/>
  </jsp:include>

  <div class="page-content">

    <div class="page-header">

      <div>

        <h2>
          Add Dentist
        </h2>

        <p>
          Add a new dentist to the clinic.
        </p>

      </div>

      <a href="${pageContext.request.contextPath}/dentist"
         class="btn btn-secondary">
        Back to Dentists
      </a>

    </div>

    <jsp:include page="/WEB-INF/includes/messages.jsp"/>

    <div class="form-card">

      <form method="post"
            action="${pageContext.request.contextPath}/dentist">

        <div class="form-grid">

          <div class="form-group form-group-full">

            <label for="name">
              Full Name
            </label>

            <input type="text"
                   id="name"
                   name="name"
                   value="<c:out value='${param.name}'/>"
                   placeholder="Enter dentist name"
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

        </div>

        <div class="form-actions">

          <a href="${pageContext.request.contextPath}/dentist"
             class="btn btn-secondary">
            Cancel
          </a>

          <button type="submit"
                  class="btn btn-primary">
            Add Dentist
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