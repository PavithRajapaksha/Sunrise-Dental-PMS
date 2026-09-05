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

  <title>Billing | Sunrise Dental</title>

  <link rel="stylesheet"
        href="${pageContext.request.contextPath}/assets/css/styles.css">

  <script src="${pageContext.request.contextPath}/assets/js/app.js"
          defer></script>

</head>

<body class="dashboard-page">

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
  <jsp:param name="activePage" value="billing"/>
</jsp:include>

<main class="dashboard-main">

  <jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageTitle" value="Billing"/>
  </jsp:include>

  <div class="page-content">

    <div class="page-header">

      <div>

        <h2>
          Billing
        </h2>

        <p>
          Manage patient billing and payment records.
        </p>

      </div>

    </div>

    <jsp:include page="/WEB-INF/includes/messages.jsp"/>

    <div class="section-heading">

      <h2>
        Find Bill
      </h2>

      <p>
        Search using a bill ID.
      </p>

    </div>

    <form method="get"
          action="${pageContext.request.contextPath}/bill"
          class="search-form search-form-single">

      <input type="text"
             name="billId"
             value="<c:out value='${param.billId}'/>"
             placeholder="Enter bill ID"
             required>

      <button type="submit"
              class="btn btn-secondary">
        Search
      </button>

    </form>

    <div class="section-heading">

      <h2>
        Generate Bill
      </h2>

      <p>
        Find a patient using their contact number.
      </p>

    </div>

    <div class="form-card">

      <form method="get"
            action="${pageContext.request.contextPath}/bill">

        <div class="form-grid">

          <div class="form-group form-group-full">

            <label for="contactNumber">
              Patient Contact Number
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

    <c:if test="${not empty selectedPatient}">

      <div class="section-heading">

        <h2>
          Selected Patient
        </h2>

        <p>
          Patient details.
        </p>

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
          Appointment Payment
        </h2>

        <p>
          Select an appointment and payment type.
        </p>

      </div>

      <div class="form-card">

        <c:choose>

          <c:when test="${not empty patientAppointments}">

            <form method="post"
                  action="${pageContext.request.contextPath}/bill">

              <input type="hidden"
                     name="contactNumber"
                     value="<c:out value='${selectedPatient.contactNumber}'/>">

              <div class="form-grid">

                <div class="form-group form-group-full">

                  <label for="appointmentNumber">
                    Appointment
                  </label>

                  <select id="appointmentNumber"
                          name="appointmentNumber"
                          required>

                    <option value="">
                      Select an appointment
                    </option>

                    <c:forEach var="appointment"
                               items="${patientAppointments}">

                      <option value="${appointment.appointmentNumber}">

                        <c:out value="${appointment.appointmentNumber}"/>
                        -
                        <c:out value="${appointment.treatmentType.name}"/>
                        -
                        <c:out value="${appointment.appointmentDateTime}"/>
                        -
                        Rs.
                        <c:out value="${appointment.treatmentType.consultationFee}"/>

                      </option>

                    </c:forEach>

                  </select>

                </div>

                <div class="form-group form-group-full">

                  <label for="paymentType">
                    Payment Type
                  </label>

                  <select id="paymentType"
                          name="paymentType"
                          required>

                    <option value="">
                      Select payment type
                    </option>

                    <option value="CASH">
                      Cash
                    </option>

                    <option value="CARD">
                      Card
                    </option>

                  </select>

                </div>

              </div>

              <div class="form-actions">

                <button type="submit"
                        class="btn btn-primary">
                  Confirm Payment & Generate Bill
                </button>

              </div>

            </form>

          </c:when>

          <c:otherwise>

            <div class="form-alert form-alert--error">

                            <span class="form-alert-icon">
                                !
                            </span>

              <div>

                <strong>
                  No appointments available
                </strong>

                <p>
                  There are no appointments available for billing.
                </p>

              </div>

            </div>

          </c:otherwise>

        </c:choose>

      </div>

    </c:if>

    <div class="section-heading">

      <h2>
        All Bills
      </h2>

      <p>
        View billing records.
      </p>

    </div>

    <div class="data-card">

      <div class="table-responsive">

        <table class="data-table">

          <thead>

          <tr>
            <th>Bill ID</th>
            <th>Appointment</th>
            <th>Patient</th>
            <th>Amount</th>
            <th>Date</th>
            <th>Status</th>
            <th>Payment</th>
            <th></th>
          </tr>

          </thead>

          <tbody>

          <c:choose>

            <c:when test="${not empty bills}">

              <c:forEach var="bill"
                         items="${bills}">

                <tr>

                  <td>

                    <strong>
                      <c:out value="${bill.billId}"/>
                    </strong>

                  </td>

                  <td>
                    <c:out value="${bill.appointment.appointmentNumber}"/>
                  </td>

                  <td>
                    <c:out value="${bill.appointment.patient.name}"/>
                  </td>

                  <td>
                    Rs.
                    <c:out value="${bill.totalAmount}"/>
                  </td>

                  <td>
                    <c:out value="${bill.generatedDate}"/>
                  </td>

                  <td>

                    <c:choose>

                      <c:when test="${bill.status.name() eq 'PAID'}">

                                                <span class="status-badge status-badge--success">
                                                    Paid
                                                </span>

                      </c:when>

                      <c:otherwise>

                                                <span class="status-badge">
                                                    Pending
                                                </span>

                      </c:otherwise>

                    </c:choose>

                  </td>

                  <td>

                    <c:choose>

                      <c:when test="${not empty bill.paymentType}">

                        <c:choose>

                          <c:when test="${bill.paymentType.name() eq 'CASH'}">
                            Cash
                          </c:when>

                          <c:when test="${bill.paymentType.name() eq 'CARD'}">
                            Card
                          </c:when>

                          <c:otherwise>
                            <c:out value="${bill.paymentType}"/>
                          </c:otherwise>

                        </c:choose>

                      </c:when>

                      <c:otherwise>
                        -
                      </c:otherwise>

                    </c:choose>

                  </td>

                  <td class="table-action">

                    <a href="${pageContext.request.contextPath}/bill?billId=${bill.billId}"
                       class="table-link">
                      View
                    </a>

                  </td>

                </tr>

              </c:forEach>

            </c:when>

            <c:otherwise>

              <tr>

                <td colspan="8"
                    class="empty-table">
                  No bills found.
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