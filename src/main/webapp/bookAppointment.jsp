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

  <title>Book Appointment | Sunrise Dental</title>

  <link rel="stylesheet"
        href="${pageContext.request.contextPath}/assets/css/styles.css">

  <script src="${pageContext.request.contextPath}/assets/js/app.js"
          defer></script>

</head>

<body class="dashboard-page">

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
  <jsp:param name="activePage" value="appointments"/>
</jsp:include>

<main class="dashboard-main">

  <jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageTitle" value="Book Appointment"/>
  </jsp:include>

  <div class="page-content">

    <div class="page-header">

      <div>

        <h2>
          Book Appointment
        </h2>

      </div>

      <a href="${pageContext.request.contextPath}/appointment"
         class="btn btn-secondary">
        Back to Appointments
      </a>

    </div>

    <c:if test="${param.registered eq 'true'}">

      <div class="form-alert form-alert--success">

        <span class="form-alert-icon form-alert-icon--success">
          ✓
        </span>

        <div>

          <strong>
            Patient registered
          </strong>

          <p>
            The patient has been registered and selected for this appointment.
          </p>

        </div>

      </div>

    </c:if>

    <jsp:include page="/WEB-INF/includes/messages.jsp"/>

    <div class="section-heading">

      <h2>
        Patient
      </h2>

      <p>
        Find the patient using their contact number.
      </p>

    </div>

    <div class="form-card">

      <form method="get"
            action="${pageContext.request.contextPath}/appointment">

        <input type="hidden"
               name="action"
               value="book">

        <div class="form-grid">

          <div class="form-group form-group-full">

            <label for="contactNumber">
              Contact Number
            </label>

            <input type="text"
                   id="contactNumber"
                   name="contactNumber"
                   value="<c:out value='${param.contactNumber}'/>"
                   placeholder="Enter patient contact number"
                   required>

          </div>

        </div>

        <div class="form-actions">

          <button type="submit"
                  class="btn btn-primary">
            Find Patient
          </button>

        </div>

      </form>

    </div>

    <c:if test="${empty selectedPatient and not empty errorMessage and not empty param.contactNumber}">

      <c:url var="registerPatientUrl"
             value="/registerPatient.jsp">

        <c:param name="returnTo"
                 value="appointment"/>

        <c:param name="contactNumber"
                 value="${param.contactNumber}"/>

      </c:url>

      <div class="section-heading">

        <h2>
          Patient Not Registered
        </h2>

        <p>
          Register this patient to continue with the appointment.
        </p>

      </div>

      <div class="form-card">

        <p>
          No registered patient was found with contact number
          <strong>
            <c:out value="${param.contactNumber}"/>
          </strong>.
        </p>

        <div class="form-actions">

          <a href="${registerPatientUrl}"
             class="btn btn-primary">
            Register Patient
          </a>

        </div>

      </div>

    </c:if>

    <c:if test="${not empty selectedPatient}">

      <div class="section-heading">

        <h2>
          Selected Patient
        </h2>

      </div>

      <div class="details-card">

        <div class="details-grid">

          <div class="detail-item">

            <span>
              Patient ID
            </span>

            <strong>
              <c:out value="${selectedPatient.patientId}"/>
            </strong>

          </div>

          <div class="detail-item">

            <span>
              Name
            </span>

            <strong>
              <c:out value="${selectedPatient.name}"/>
            </strong>

          </div>

          <div class="detail-item">

            <span>
              Contact Number
            </span>

            <strong>
              <c:out value="${selectedPatient.contactNumber}"/>
            </strong>

          </div>

          <div class="detail-item">

            <span>
              Address
            </span>

            <strong>
              <c:out value="${selectedPatient.address}"/>
            </strong>

          </div>

        </div>

      </div>

      <div class="section-heading">

        <h2>
          Appointment
        </h2>

      </div>

      <div class="form-card">

        <form method="post"
              action="${pageContext.request.contextPath}/appointment">

          <input type="hidden"
                 name="action"
                 value="book">

          <input type="hidden"
                 name="patientId"
                 value="<c:out value='${selectedPatient.patientId}'/>">

          <div class="form-grid">

            <div class="form-group form-group-full">

              <label for="dentistSearch">
                Find Dentist
              </label>

              <input type="text"
                     id="dentistSearch"
                     placeholder="Filter by dentist ID or name"
                     data-select-filter="dentistId">

            </div>

            <div class="form-group form-group-full">

              <label for="dentistId">
                Dentist
              </label>

              <select id="dentistId"
                      name="dentistId"
                      required>

                <option value="">
                  Select a dentist
                </option>

                <c:forEach var="dentist"
                           items="${dentists}">

                  <option value="${dentist.dentistId}"
                    ${param.dentistId eq dentist.dentistId ? 'selected' : ''}>

                    <c:out value="${dentist.dentistId}"/>
                    -
                    <c:out value="${dentist.name}"/>

                  </option>

                </c:forEach>

              </select>

            </div>

            <div class="form-group form-group-full">

              <label for="treatmentSearch">
                Find Treatment
              </label>

              <input type="text"
                     id="treatmentSearch"
                     placeholder="Filter by treatment ID or name"
                     data-select-filter="treatmentTypeId">

            </div>

            <div class="form-group form-group-full">

              <label for="treatmentTypeId">
                Treatment
              </label>

              <select id="treatmentTypeId"
                      name="treatmentTypeId"
                      required>

                <option value="">
                  Select a treatment
                </option>

                <c:forEach var="treatment"
                           items="${treatmentTypes}">

                  <option value="${treatment.treatmentTypeId}"
                    ${param.treatmentTypeId eq treatment.treatmentTypeId ? 'selected' : ''}>

                    <c:out value="${treatment.treatmentTypeId}"/>
                    -
                    <c:out value="${treatment.name}"/>
                    -
                    Rs.
                    <c:out value="${treatment.consultationFee}"/>

                  </option>

                </c:forEach>

              </select>

            </div>

            <div class="form-group">

              <label for="appointmentDate">
                Appointment Date
              </label>

              <input type="date"
                     id="appointmentDate"
                     name="appointmentDate"
                     value="<c:out value='${param.appointmentDate}'/>"
                     required>

            </div>

            <div class="form-group">

              <label for="appointmentTime">
                Appointment Time
              </label>

              <input type="time"
                     id="appointmentTime"
                     name="appointmentTime"
                     value="<c:out value='${param.appointmentTime}'/>"
                     required>

            </div>

          </div>

          <div class="form-actions">

            <a href="${pageContext.request.contextPath}/appointment"
               class="btn btn-secondary">
              Cancel
            </a>

            <button type="submit"
                    class="btn btn-primary">
              Book Appointment
            </button>

          </div>

        </form>

      </div>

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