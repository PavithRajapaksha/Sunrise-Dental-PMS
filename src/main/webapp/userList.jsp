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

  <title>Staff Users | Sunrise Dental</title>

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
    <jsp:param name="pageTitle" value="Staff Users"/>
  </jsp:include>

  <div class="page-content">

    <div class="page-header">

      <div>
        <p class="eyebrow">Admin</p>
        <h2>Staff Users</h2>
        <p>View staff accounts.</p>
      </div>

      <c:if test="${sessionScope.loggedInUser.role.name() eq 'ADMIN'}">

        <a href="${pageContext.request.contextPath}/registerUser.jsp"
           class="btn btn-primary">
          Add Staff
        </a>

      </c:if>

    </div>

    <jsp:include page="/WEB-INF/includes/messages.jsp"/>

    <form method="get"
          action="${pageContext.request.contextPath}/user"
          class="search-form search-form-single">

      <input type="text"
             name="username"
             placeholder="Username"
             required>

      <button type="submit"
              class="btn btn-secondary">
        Find
      </button>

    </form>

    <div class="data-card">

      <div class="table-responsive">

        <table class="data-table">

          <thead>

          <tr>
            <th>User ID</th>
            <th>Name</th>
            <th>Username</th>
            <th>Contact</th>
            <th>Role</th>
            <th></th>
          </tr>

          </thead>

          <tbody>

          <c:forEach var="staffUser"
                     items="${users}">

            <tr>

              <td>
                <c:out value="${staffUser.userId}"/>
              </td>

              <td>
                <strong>
                  <c:out value="${staffUser.name}"/>
                </strong>
              </td>

              <td>
                <c:out value="${staffUser.username}"/>
              </td>

              <td>
                <c:out value="${staffUser.contactNumber}"/>
              </td>

              <td>
                                <span class="status-badge">
                                    <c:out value="${staffUser.role}"/>
                                </span>
              </td>

              <td class="table-action">

                <a href="${pageContext.request.contextPath}/user?username=${staffUser.username}"
                   class="table-link">
                  View
                </a>

              </td>

            </tr>

          </c:forEach>

          <c:if test="${empty users}">

            <tr>
              <td colspan="6"
                  class="empty-table">
                No staff users found.
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