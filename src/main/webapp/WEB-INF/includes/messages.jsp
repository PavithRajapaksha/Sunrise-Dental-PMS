<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${not empty successMessage}">

  <div class="form-alert form-alert--success">

        <span class="form-alert-icon form-alert-icon--success">
            ✓
        </span>

    <div>
      <strong>Success</strong>
      <p><c:out value="${successMessage}"/></p>
    </div>

  </div>

</c:if>

<c:if test="${not empty errorMessage}">

  <div class="form-alert form-alert--error">

        <span class="form-alert-icon">
            !
        </span>

    <div>
      <strong>Error</strong>
      <p><c:out value="${errorMessage}"/></p>
    </div>

  </div>

</c:if>