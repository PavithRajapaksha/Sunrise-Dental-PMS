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

  <title>Add Treatment | Sunrise Dental</title>

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
    <jsp:param name="pageTitle" value="Add Treatment"/>
  </jsp:include>

  <div class="page-content">

    <div class="page-header">

      <div>

        <h2>
          Add Treatment
        </h2>

        <p>
          Add a new treatment type.
        </p>

      </div>

      <a href="${pageContext.request.contextPath}/treatmentType"
         class="btn btn-secondary">
        Back to Treatments
      </a>

    </div>

    <jsp:include page="/WEB-INF/includes/messages.jsp"/>

    <div class="form-card">

      <form method="post"
            action="${pageContext.request.contextPath}/treatmentType">

        <div class="form-grid">

          <div class="form-group form-group-full">

            <label for="name">
              Treatment Name
            </label>

            <input type="text"
                   id="name"
                   name="name"
                   value="<c:out value='${param.name}'/>"
                   placeholder="Enter treatment name"
                   required>

          </div>

          <div class="form-group form-group-full">

            <label for="consultationFee">
              Consultation Fee
            </label>

            <div class="input-prefix">

                            <span>
                                LKR
                            </span>

              <input type="number"
                     id="consultationFee"
                     name="consultationFee"
                     value="<c:out value='${param.consultationFee}'/>"
                     placeholder="Enter consultation fee"
                     min="0"
                     step="0.01"
                     required>

            </div>

          </div>

        </div>

        <div class="form-actions">

          <a href="${pageContext.request.contextPath}/treatmentType"
             class="btn btn-secondary">
            Cancel
          </a>

          <button type="submit"
                  class="btn btn-primary">
            Add Treatment
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