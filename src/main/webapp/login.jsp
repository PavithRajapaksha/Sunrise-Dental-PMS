<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${not empty sessionScope.loggedInUser}">
    <c:redirect url="/dashboard.jsp"/>
</c:if>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Login | Sunrise Dental</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/styles.css">

    <script src="${pageContext.request.contextPath}/assets/js/app.js"
            defer></script>

</head>

<body class="auth-page">

<div class="toast toast--success"
     id="logoutToast"
     hidden>

    <div class="toast-symbol">
        ✓
    </div>

    <div class="toast-content">

        <strong>
            Logout successful
        </strong>

        <p>
            You have been logged out.
        </p>

    </div>

    <button type="button"
            class="toast-close"
            data-toast-close
            aria-label="Close notification">
        ×
    </button>

</div>

<c:if test="${not empty errorMessage}">
    <div id="loginErrorState" hidden></div>
</c:if>

<main class="auth-container">

    <section class="auth-brand">

        <div class="brand-logo brand-logo--large">
            SD
        </div>

        <p class="brand-label">
            SUNRISE DENTAL
        </p>

        <h1>
            Patient Management System
        </h1>

        <p class="auth-brand-description">
            Manage patients, appointments, treatments and billing.
        </p>

        <div class="clinic-note">

            <span class="clinic-note-icon">
                +
            </span>

            <div>

                <strong>
                    Staff Login
                </strong>

                <p>
                    Authorized staff only
                </p>

            </div>

        </div>

    </section>

    <section class="auth-card">

        <div class="auth-card-header">

            <div class="mobile-brand-logo">
                SD
            </div>

            <p class="eyebrow">
                Staff Login
            </p>

            <h2>
                Welcome
            </h2>

            <p>
                Enter your username and password.
            </p>

        </div>

        <c:if test="${not empty errorMessage}">

            <div class="form-alert form-alert--error">

                <span class="form-alert-icon">
                    !
                </span>

                <div>

                    <strong>
                        Login failed
                    </strong>

                    <p>
                        <c:out value="${errorMessage}"/>
                    </p>

                </div>

            </div>

        </c:if>

        <form method="post"
              action="${pageContext.request.contextPath}/login"
              class="login-form"
              id="loginForm">

            <div class="form-group">

                <label for="username">
                    Username
                </label>

                <input type="text"
                       id="username"
                       name="username"
                       placeholder="Enter your username"
                       autocomplete="username"
                       required
                       autofocus>

            </div>

            <div class="form-group">

                <label for="password">
                    Password
                </label>

                <div class="password-field">

                    <input type="password"
                           id="password"
                           name="password"
                           placeholder="Enter your password"
                           autocomplete="current-password"
                           required>

                    <button type="button"
                            id="togglePassword"
                            class="password-toggle">
                        Show
                    </button>

                </div>

            </div>

            <button type="submit"
                    class="btn btn-primary btn-full">
                Sign in
            </button>

        </form>

        <div class="auth-footer">

            <p>
                Sunrise Dental Patient Management System
            </p>

            <small>
                Internal clinic use only
            </small>

        </div>

    </section>

</main>

</body>

</html>