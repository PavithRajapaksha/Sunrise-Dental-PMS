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

  <title>Treatment Details | Sunrise Dental</title>

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
    <jsp:param name="pageTitle" value="Treatment Details"/>
  </jsp:include>

  <div class="page-content">

    <div class="page-header">

      <div>

        <h2>
          Treatment Details
        </h2>

        <p>
          View treatment information.
        </p>

      </div>

      <a href="${pageContext.request.contextPath}/treatmentType"
         class="btn btn-secondary">
        Back to Treatments
      </a>

    </div>

    <jsp:include page="/WEB-INF/includes/messages.jsp"/>

    <c:choose>

      <c:when test="${not empty treatmentType}">

        <div class="details-card">

          <div class="details-grid">

            <div class="detail-item">

                            <span>
                                Treatment ID
                            </span>

              <strong>
                <c:out value="${treatmentType.treatmentTypeId}"/>
              </strong>

            </div>

            <div class="detail-item">

                            <span>
                                Treatment Name
                            </span>

              <strong>
                <c:out value="${treatmentType.name}"/>
              </strong>

            </div>

            <div class="detail-item">

                            <span>
                                Consultation Fee
                            </span>

              <strong>
                LKR
                <c:out value="${treatmentType.consultationFee}"/>
              </strong>

            </div>

          </div>

        </div>

        <c:if test="${sessionScope.loggedInUser.role.name() eq 'ADMIN'}">

          <div class="section-heading">

            <p class="eyebrow">
              Admin
            </p>

            <h2>
              Update Consultation Fee
            </h2>

            <p>
              Change the consultation fee for this treatment.
            </p>

          </div>

          <div class="form-card">

            <form method="post"
                  action="${pageContext.request.contextPath}/treatmentType">

              <input type="hidden"
                     name="action"
                     value="updateFee">

              <input type="hidden"
                     name="treatmentTypeId"
                     value="<c:out value='${treatmentType.treatmentTypeId}'/>">

              <div class="form-grid">

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
                           value="<c:out value='${treatmentType.consultationFee}'/>"
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
                  Update Fee
                </button>

              </div>

            </form>

          </div>

        </c:if>

      </c:when>

      <c:otherwise>

        <div class="form-alert form-alert--error">

                    <span class="form-alert-icon">
                        !
                    </span>

          <div>

            <strong>
              Treatment not found
            </strong>

            <p>
              The requested treatment could not be found.
            </p>

          </div>

        </div>

      </c:otherwise>

    </c:choose>

  </div>

  <footer class="dashboard-footer">

    <p>
      Sunrise Dental Patient Management System
    </p>

  </footer>

</main>

</body>

</html>