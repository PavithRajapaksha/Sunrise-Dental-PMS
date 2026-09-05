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
  <meta name="viewport" content="width=device-width, initial-scale=1.0">

  <title>Add Staff | Sunrise Dental</title>

  <link rel="stylesheet"
        href="${pageContext.request.contextPath}/assets/css/styles.css">

  <script src="${pageContext.request.contextPath}/assets/js/app.js"
          defer></script>

</head>

<body class="dashboard-page">

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
  <jsp:param name="activePage" value="users"/>
</jsp:include>

<main class="dashboard-main">

  <jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageTitle" value="Add Staff"/>
  </jsp:include>

  <div class="page-content">

    <div class="page-header">

      <div>
        <p class="eyebrow">Admin</p>
        <h2>Add Staff</h2>
        <p>Create a staff account.</p>
      </div>

      <a href="${pageContext.request.contextPath}/user"
         class="btn btn-secondary">
        Back
      </a>

    </div>

    <jsp:include page="/WEB-INF/includes/messages.jsp"/>

    <form method="post"
          action="${pageContext.request.contextPath}/user"
          class="form-card">

      <div class="form-grid">

        <div class="form-group">

          <label for="fullName">
            Full Name
          </label>

          <input type="text"
                 id="fullName"
                 name="fullName"
                 value="<c:out value='${param.fullName}'/>"
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
                 pattern="0[0-9]{9}"
                 maxlength="10"
                 placeholder="07XXXXXXXX"
                 required>

        </div>

        <div class="form-group">

          <label for="username">
            Username
          </label>

          <input type="text"
                 id="username"
                 name="username"
                 value="<c:out value='${param.username}'/>"
                 minlength="4"
                 required>

        </div>

        <div class="form-group">

          <label for="password">
            Password
          </label>

          <div class="password-field">

            <input type="password"
                   id="password"
                   name="password"
                   required>

            <button type="button"
                    id="togglePassword"
                    class="password-toggle">
              Show
            </button>

          </div>

        </div>

      </div>

      <div class="form-actions">

        <a href="${pageContext.request.contextPath}/user"
           class="btn btn-secondary">
          Cancel
        </a>

        <button type="submit"
                class="btn btn-primary">
          Add Staff
        </button>

      </div>

    </form>

  </div>

</main>

</body>
</html>