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

    <title>Add Patient | Sunrise Dental</title>

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
        <jsp:param name="pageTitle" value="Add Patient"/>
    </jsp:include>

    <div class="page-content">

        <div class="page-header">

            <div>
                <h2>Add Patient</h2>
                <p>Enter the patient details.</p>
            </div>

            <a href="${pageContext.request.contextPath}/patient"
               class="btn btn-secondary">
                Back
            </a>

        </div>

        <jsp:include page="/WEB-INF/includes/messages.jsp"/>

        <form method="post"
              action="${pageContext.request.contextPath}/patient"
              class="form-card">

            <div class="form-grid">

                <div class="form-group">

                    <label for="name">
                        Full Name
                    </label>

                    <input type="text"
                           id="name"
                           name="name"
                           value="<c:out value='${param.name}'/>"
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

                <div class="form-group form-group-full">

                    <label for="address">
                        Address
                    </label>

                    <textarea id="address"
                              name="address"
                              rows="3"
                              required><c:out value="${param.address}"/></textarea>

                </div>

                <div class="form-group form-group-full">

                    <label for="email">
                        Email
                    </label>

                    <input type="email"
                           id="email"
                           name="email"
                           value="<c:out value='${param.email}'/>"
                           placeholder="Optional">

                </div>

            </div>

            <div class="form-actions">

                <a href="${pageContext.request.contextPath}/patient"
                   class="btn btn-secondary">
                    Cancel
                </a>

                <button type="submit"
                        class="btn btn-primary">
                    Add Patient
                </button>

            </div>

        </form>

    </div>

</main>

</body>
</html>