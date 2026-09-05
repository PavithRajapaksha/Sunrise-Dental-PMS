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

  <title>Staff Details | Sunrise Dental</title>

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
    <jsp:param name="pageTitle" value="Staff Details"/>
  </jsp:include>

  <div class="page-content">

    <div class="page-header">

      <div>
        <h2>
          <c:out value="${user.name}"/>
        </h2>

        <p>
          <c:out value="${user.username}"/>
        </p>
      </div>

      <a href="${pageContext.request.contextPath}/user"
         class="btn btn-secondary">
        Back
      </a>

    </div>

    <jsp:include page="/WEB-INF/includes/messages.jsp"/>

    <div class="details-card">

      <div class="details-grid">

        <div class="detail-item">
          <span>User ID</span>
          <strong>
            <c:out value="${user.userId}"/>
          </strong>
        </div>

        <div class="detail-item">
          <span>Full Name</span>
          <strong>
            <c:out value="${user.name}"/>
          </strong>
        </div>

        <div class="detail-item">
          <span>Username</span>
          <strong>
            <c:out value="${user.username}"/>
          </strong>
        </div>

        <div class="detail-item">
          <span>Contact Number</span>
          <strong>
            <c:out value="${user.contactNumber}"/>
          </strong>
        </div>

        <div class="detail-item">
          <span>Role</span>
          <strong>
                        <span class="status-badge">
                            <c:out value="${user.role}"/>
                        </span>
          </strong>
        </div>

      </div>

    </div>

  </div>

</main>

</body>
</html>