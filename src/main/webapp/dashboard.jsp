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

  <title>Dashboard | Sunrise Dental</title>

  <link rel="stylesheet"
        href="${pageContext.request.contextPath}/assets/css/styles.css">

  <script src="${pageContext.request.contextPath}/assets/js/app.js"
          defer></script>

</head>

<body class="dashboard-page">

<div class="toast toast--success"
     id="loginToast"
     hidden>

  <div class="toast-symbol">
    ✓
  </div>

  <div class="toast-content">

    <strong>
      Login successful
    </strong>

    <p>
      Welcome,
      <c:out value="${sessionScope.loggedInUser.name}"/>.
    </p>

  </div>

  <button type="button"
          class="toast-close"
          data-toast-close
          aria-label="Close notification">
    ×
  </button>

</div>

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
  <jsp:param name="activePage" value="dashboard"/>
</jsp:include>

<main class="dashboard-main">

  <jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageTitle" value="Dashboard"/>
  </jsp:include>

  <div class="dashboard-content">

    <section class="welcome-panel">

      <div>

        <p class="eyebrow">
          Dashboard
        </p>

        <h2>
          Welcome,
          <c:out value="${sessionScope.loggedInUser.name}"/>
        </h2>

        <p>
          Select an option to continue.
        </p>

      </div>

      <div class="role-badge">

        <span>
          Logged in as
        </span>

        <strong>

          <c:choose>

            <c:when test="${sessionScope.loggedInUser.role.name() eq 'ADMIN'}">
              Admin
            </c:when>

            <c:otherwise>
              Receptionist
            </c:otherwise>

          </c:choose>

        </strong>

      </div>

    </section>

    <div class="section-heading">

      <h2>
        Clinic
      </h2>

      <p>
        Select a section.
      </p>

    </div>

    <section class="module-grid">

      <a href="${pageContext.request.contextPath}/patient"
         class="module-card module-card-link">

        <div class="module-card-icon">
          P
        </div>

        <div>

          <h3>
            Patients
          </h3>

          <p>
            View and manage patients.
          </p>

        </div>

        <span class="module-link-text">
          Open →
        </span>

      </a>

      <a href="${pageContext.request.contextPath}/appointment"
         class="module-card module-card-link">

        <div class="module-card-icon">
          A
        </div>

        <div>

          <h3>
            Appointments
          </h3>

          <p>
            View and manage appointments.
          </p>

        </div>

        <span class="module-link-text">
          Open →
        </span>

      </a>

      <a href="${pageContext.request.contextPath}/bill"
         class="module-card module-card-link">

        <div class="module-card-icon">
          B
        </div>

        <div>

          <h3>
            Billing
          </h3>

          <p>
            View and manage bills.
          </p>

        </div>

        <span class="module-link-text">
          Open →
        </span>

      </a>

      <a href="${pageContext.request.contextPath}/dentist"
         class="module-card module-card-link">

        <div class="module-card-icon">
          D
        </div>

        <div>

          <h3>
            Dentists
          </h3>

          <p>
            View dentists and status.
          </p>

        </div>

        <span class="module-link-text">
          Open →
        </span>

      </a>

      <a href="${pageContext.request.contextPath}/treatmentType"
         class="module-card module-card-link">

        <div class="module-card-icon">
          T
        </div>

        <div>

          <h3>
            Treatments
          </h3>

          <p>
            View treatments and fees.
          </p>

        </div>

        <span class="module-link-text">
          Open →
        </span>

      </a>

      <a href="${pageContext.request.contextPath}/help.jsp"
         class="module-card module-card-link">

        <div class="module-card-icon">
          ?
        </div>

        <div>

          <h3>
            Help
          </h3>

          <p>
            Learn how to use the system.
          </p>

        </div>

        <span class="module-link-text">
          Open →
        </span>

      </a>

    </section>

    <c:if test="${sessionScope.loggedInUser.role.name() eq 'ADMIN'}">

      <div class="section-heading admin-heading">

        <p class="eyebrow">
          Admin
        </p>

        <h2>
          Admin
        </h2>

        <p>
          Admin options.
        </p>

      </div>

      <section class="admin-grid">

        <a href="${pageContext.request.contextPath}/user"
           class="admin-card">

          <div class="admin-panel-icon">
            U
          </div>

          <div>

            <h3>
              Staff Users
            </h3>

            <p>
              View staff accounts.
            </p>

          </div>

          <span class="module-link-text">
            Open →
          </span>

        </a>

        <a href="${pageContext.request.contextPath}/report"
           class="admin-card">

          <div class="admin-panel-icon">
            R
          </div>

          <div>

            <h3>
              Reports
            </h3>

            <p>
              View clinic reports.
            </p>

          </div>

          <span class="module-link-text">
            Open →
          </span>

        </a>

        <a href="${pageContext.request.contextPath}/registerDentist.jsp"
           class="admin-card">

          <div class="admin-panel-icon">
            D
          </div>

          <div>

            <h3>
              Add Dentist
            </h3>

            <p>
              Add a dentist.
            </p>

          </div>

          <span class="module-link-text">
            Open →
          </span>

        </a>

        <a href="${pageContext.request.contextPath}/addTreatmentType.jsp"
           class="admin-card">

          <div class="admin-panel-icon">
            T
          </div>

          <div>

            <h3>
              Add Treatment
            </h3>

            <p>
              Add a treatment.
            </p>

          </div>

          <span class="module-link-text">
            Open →
          </span>

        </a>

        <a href="${pageContext.request.contextPath}/registerUser.jsp"
           class="admin-card">

          <div class="admin-panel-icon">
            U
          </div>

          <div>

            <h3>
              Add Staff
            </h3>

            <p>
              Create a staff account.
            </p>

          </div>

          <span class="module-link-text">
            Open →
          </span>

        </a>

      </section>

    </c:if>

  </div>

  <footer class="dashboard-footer">

    <p>
      Sunrise Dental Patient Management System
    </p>

  </footer>

</main>

</body>

</html>