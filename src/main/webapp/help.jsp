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

    <title>Help | Sunrise Dental</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/styles.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/help.css">

    <script src="${pageContext.request.contextPath}/assets/js/app.js"
            defer></script>

</head>

<body class="dashboard-page">

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activePage" value="help"/>
</jsp:include>

<main class="dashboard-main">

    <jsp:include page="/WEB-INF/includes/topbar.jsp">
        <jsp:param name="pageTitle" value="Help"/>
    </jsp:include>

    <div class="page-content">

        <div class="page-header">

            <div>

                <h2>
                    Help & User Guide
                </h2>

                <p>
                    Instructions for common clinic tasks.
                </p>

            </div>

        </div>

        <nav class="help-navigation">

            <a href="#patients"
               class="filter-link">
                Patients
            </a>

            <a href="#appointments"
               class="filter-link">
                Appointments
            </a>

            <a href="#billing"
               class="filter-link">
                Billing
            </a>

            <a href="#dentists"
               class="filter-link">
                Dentists
            </a>

            <a href="#treatments"
               class="filter-link">
                Treatments
            </a>

            <c:if test="${sessionScope.loggedInUser.role.name() eq 'ADMIN'}">

                <a href="#admin"
                   class="filter-link">
                    Admin
                </a>

            </c:if>

            <a href="#navigation"
               class="filter-link">
                Navigation
            </a>

        </nav>

        <section id="patients"
                 class="help-section">

            <div class="section-heading">

                <h2>
                    Patients
                </h2>

                <p>
                    Register, find and view patient records.
                </p>

            </div>

            <div class="help-grid">

                <article class="help-card">

                    <h3>
                        Register a Patient
                    </h3>

                    <ol class="help-steps">

                        <li>
                            Open <strong>Patients</strong>.
                        </li>

                        <li>
                            Select <strong>Register Patient</strong>.
                        </li>

                        <li>
                            Enter the patient's name, address,
                            contact number and email address.
                        </li>

                        <li>
                            Select <strong>Register Patient</strong>.
                        </li>

                        <li>
                            The new patient record will be displayed.
                        </li>

                    </ol>

                </article>

                <article class="help-card">

                    <h3>
                        Find a Patient
                    </h3>

                    <ol class="help-steps">

                        <li>
                            Open <strong>Patients</strong>.
                        </li>

                        <li>
                            Enter the patient's
                            <strong>Patient ID</strong> or
                            <strong>Contact Number</strong>.
                        </li>

                        <li>
                            Select <strong>Search</strong>.
                        </li>

                        <li>
                            The patient details and appointment
                            history will be displayed.
                        </li>

                    </ol>

                </article>

                <article class="help-card">

                    <h3>
                        View All Patients
                    </h3>

                    <ol class="help-steps">

                        <li>
                            Open <strong>Patients</strong>.
                        </li>

                        <li>
                            All registered patients are shown
                            in the patient list.
                        </li>

                        <li>
                            Select <strong>View</strong>
                            beside a patient to open the record.
                        </li>

                    </ol>

                </article>

            </div>

        </section>

        <section id="appointments"
                 class="help-section">

            <div class="section-heading">

                <h2>
                    Appointments
                </h2>

                <p>
                    Book, find, view and cancel appointments.
                </p>

            </div>

            <div class="help-grid">

                <article class="help-card">

                    <h3>
                        Book an Appointment
                    </h3>

                    <ol class="help-steps">

                        <li>
                            Open <strong>Appointments</strong>.
                        </li>

                        <li>
                            Select <strong>Book Appointment</strong>.
                        </li>

                        <li>
                            Enter the patient's contact number
                            and select <strong>Find Patient</strong>.
                        </li>

                        <li>
                            If the patient is not registered,
                            select <strong>Register Patient</strong>.
                        </li>

                        <li>
                            Complete the patient registration.
                            The patient will then be selected
                            for the appointment.
                        </li>

                        <li>
                            Select the dentist and treatment.
                        </li>

                        <li>
                            Select the appointment date and time.
                        </li>

                        <li>
                            Select <strong>Book Appointment</strong>.
                        </li>

                    </ol>

                </article>

                <article class="help-card">

                    <h3>
                        Find an Appointment
                    </h3>

                    <ol class="help-steps">

                        <li>
                            Open <strong>Appointments</strong>.
                        </li>

                        <li>
                            Search using the appointment number,
                            patient ID or patient contact number.
                        </li>

                        <li>
                            Select <strong>Search</strong>.
                        </li>

                        <li>
                            Select <strong>View</strong>
                            to open an appointment.
                        </li>

                    </ol>

                </article>

                <article class="help-card">

                    <h3>
                        View All Appointments
                    </h3>

                    <ol class="help-steps">

                        <li>
                            Open <strong>Appointments</strong>.
                        </li>

                        <li>
                            The appointment list is displayed.
                        </li>

                        <li>
                            Use the status column to identify
                            Scheduled, Completed and Cancelled
                            appointments.
                        </li>

                    </ol>

                </article>

                <article class="help-card">

                    <h3>
                        Cancel an Appointment
                    </h3>

                    <ol class="help-steps">

                        <li>
                            Find the required scheduled appointment.
                        </li>

                        <li>
                            Select <strong>Cancel</strong>
                            from the list, or open the appointment
                            and select
                            <strong>Cancel Appointment</strong>.
                        </li>

                        <li>
                            Confirm the cancellation.
                        </li>

                        <li>
                            The status will change to
                            <strong>Cancelled</strong>.
                        </li>

                    </ol>

                </article>

            </div>

        </section>

        <section id="billing"
                 class="help-section">

            <div class="section-heading">

                <h2>
                    Billing
                </h2>

                <p>
                    Record payments and manage bills.
                </p>

            </div>

            <div class="help-grid">

                <article class="help-card">

                    <h3>
                        Generate a Bill
                    </h3>

                    <ol class="help-steps">

                        <li>
                            Open <strong>Billing</strong>.
                        </li>

                        <li>
                            Enter the patient's contact number
                            and select <strong>Find Patient</strong>.
                        </li>

                        <li>
                            Select the required appointment.
                        </li>

                        <li>
                            Select <strong>Cash</strong> or
                            <strong>Card</strong> as the payment type.
                        </li>

                        <li>
                            Select
                            <strong>Confirm Payment & Generate Bill</strong>.
                        </li>

                        <li>
                            The generated bill will be displayed.
                        </li>

                    </ol>

                </article>

                <article class="help-card">

                    <h3>
                        Appointment Billing
                    </h3>

                    <ol class="help-steps">

                        <li>
                            Only scheduled appointments are
                            available for payment.
                        </li>

                        <li>
                            After payment is recorded successfully,
                            the appointment is marked
                            <strong>Completed</strong>.
                        </li>

                    </ol>

                </article>

                <article class="help-card">

                    <h3>
                        Find a Bill
                    </h3>

                    <ol class="help-steps">

                        <li>
                            Open <strong>Billing</strong>.
                        </li>

                        <li>
                            Enter the bill ID in
                            <strong>Find Bill</strong>.
                        </li>

                        <li>
                            Select <strong>Search</strong>.
                        </li>

                        <li>
                            The bill details will be displayed.
                        </li>

                    </ol>

                </article>

                <article class="help-card">

                    <h3>
                        View All Bills
                    </h3>

                    <ol class="help-steps">

                        <li>
                            Open <strong>Billing</strong>.
                        </li>

                        <li>
                            Scroll to <strong>All Bills</strong>.
                        </li>

                        <li>
                            Select <strong>View</strong>
                            beside a bill to open it.
                        </li>

                    </ol>

                </article>

                <article class="help-card">

                    <h3>
                        PDF and Printing
                    </h3>

                    <ol class="help-steps">

                        <li>
                            Open the required bill.
                        </li>

                        <li>
                            Select <strong>View PDF</strong>
                            to open the PDF version.
                        </li>

                        <li>
                            Select <strong>Print</strong>
                            to open the print window.
                        </li>

                    </ol>

                </article>

            </div>

        </section>

        <section id="dentists"
                 class="help-section">

            <div class="section-heading">

                <h2>
                    Dentists
                </h2>

                <p>
                    Find and view dentist information.
                </p>

            </div>

            <div class="help-grid">

                <article class="help-card">

                    <h3>
                        View All Dentists
                    </h3>

                    <ol class="help-steps">

                        <li>
                            Open <strong>Dentists</strong>.
                        </li>

                        <li>
                            The dentist list will be displayed.
                        </li>

                        <li>
                            Select <strong>View</strong>
                            to open a dentist record.
                        </li>

                    </ol>

                </article>

                <article class="help-card">

                    <h3>
                        Find a Dentist
                    </h3>

                    <ol class="help-steps">

                        <li>
                            Open <strong>Dentists</strong>.
                        </li>

                        <li>
                            Search using the dentist ID.
                        </li>

                        <li>
                            Use the availability filter when
                            you want to view available dentists.
                        </li>

                    </ol>

                </article>

            </div>

        </section>

        <section id="treatments"
                 class="help-section">

            <div class="section-heading">

                <h2>
                    Treatments
                </h2>

                <p>
                    View treatment types and consultation fees.
                </p>

            </div>

            <div class="help-grid">

                <article class="help-card">

                    <h3>
                        View Treatments
                    </h3>

                    <ol class="help-steps">

                        <li>
                            Open <strong>Treatments</strong>.
                        </li>

                        <li>
                            The available treatment types
                            and consultation fees are displayed.
                        </li>

                        <li>
                            Select a treatment to open
                            its details.
                        </li>

                    </ol>

                </article>

            </div>

        </section>

        <c:if test="${sessionScope.loggedInUser.role.name() eq 'ADMIN'}">

            <section id="admin"
                     class="help-section">

                <div class="section-heading">

                    <p class="eyebrow">
                        Admin
                    </p>

                    <h2>
                        Admin Tasks
                    </h2>

                    <p>
                        Administration and reporting.
                    </p>

                </div>

                <div class="help-grid">

                    <article class="help-card">

                        <span class="help-admin-badge">
                            Admin
                        </span>

                        <h3>
                            Add a Dentist
                        </h3>

                        <ol class="help-steps">

                            <li>
                                Open the Dashboard.
                            </li>

                            <li>
                                Select <strong>Add Dentist</strong>.
                            </li>

                            <li>
                                Enter the dentist details.
                            </li>

                            <li>
                                Select
                                <strong>Register Dentist</strong>.
                            </li>

                        </ol>

                    </article>

                    <article class="help-card">

                        <span class="help-admin-badge">
                            Admin
                        </span>

                        <h3>
                            Change Dentist Status
                        </h3>

                        <ol class="help-steps">

                            <li>
                                Open <strong>Dentists</strong>.
                            </li>

                            <li>
                                Find the required dentist.
                            </li>

                            <li>
                                Change the dentist status
                                to Available or Unavailable.
                            </li>

                            <li>
                                Save the change.
                            </li>

                        </ol>

                    </article>

                    <article class="help-card">

                        <span class="help-admin-badge">
                            Admin
                        </span>

                        <h3>
                            Add a Treatment
                        </h3>

                        <ol class="help-steps">

                            <li>
                                Open the Dashboard.
                            </li>

                            <li>
                                Select <strong>Add Treatment</strong>.
                            </li>

                            <li>
                                Enter the treatment name
                                and consultation fee.
                            </li>

                            <li>
                                Save the treatment.
                            </li>

                        </ol>

                    </article>

                    <article class="help-card">

                        <span class="help-admin-badge">
                            Admin
                        </span>

                        <h3>
                            Update a Treatment Fee
                        </h3>

                        <ol class="help-steps">

                            <li>
                                Open <strong>Treatments</strong>.
                            </li>

                            <li>
                                Find the required treatment.
                            </li>

                            <li>
                                Enter the new consultation fee.
                            </li>

                            <li>
                                Save the change.
                            </li>

                        </ol>

                    </article>

                    <article class="help-card">

                        <span class="help-admin-badge">
                            Admin
                        </span>

                        <h3>
                            View Staff Users
                        </h3>

                        <ol class="help-steps">

                            <li>
                                Open <strong>Staff Users</strong>
                                from the Admin section.
                            </li>

                            <li>
                                The registered staff accounts
                                will be displayed.
                            </li>

                        </ol>

                    </article>

                    <article class="help-card">

                        <span class="help-admin-badge">
                            Admin
                        </span>

                        <h3>
                            Add a Staff User
                        </h3>

                        <ol class="help-steps">

                            <li>
                                Open the Dashboard.
                            </li>

                            <li>
                                Select <strong>Add Staff</strong>.
                            </li>

                            <li>
                                Complete the staff account form.
                            </li>

                            <li>
                                Save the new account.
                            </li>

                        </ol>

                    </article>

                    <article class="help-card">

                        <span class="help-admin-badge">
                            Admin
                        </span>

                        <h3>
                            Treatment Revenue Report
                        </h3>

                        <ol class="help-steps">

                            <li>
                                Open <strong>Reports</strong>.
                            </li>

                            <li>
                                Select
                                <strong>Treatment Revenue</strong>.
                            </li>

                            <li>
                                Select a treatment type,
                                or select all treatment types.
                            </li>

                            <li>
                                Select <strong>Apply</strong>.
                            </li>

                            <li>
                                Select <strong>Save as PDF</strong>
                                to save the report.
                            </li>

                        </ol>

                    </article>

                    <article class="help-card">

                        <span class="help-admin-badge">
                            Admin
                        </span>

                        <h3>
                            Dentist Workload Report
                        </h3>

                        <ol class="help-steps">

                            <li>
                                Open <strong>Reports</strong>.
                            </li>

                            <li>
                                Select
                                <strong>Dentist Workload</strong>.
                            </li>

                            <li>
                                Select a dentist,
                                or select all dentists.
                            </li>

                            <li>
                                Select <strong>Apply</strong>.
                            </li>

                            <li>
                                Select <strong>Save as PDF</strong>
                                to save the report.
                            </li>

                        </ol>

                    </article>

                </div>

            </section>

        </c:if>

        <section id="navigation"
                 class="help-section">

            <div class="section-heading">

                <h2>
                    Navigation
                </h2>

            </div>

            <div class="help-grid">

                <article class="help-card">

                    <h3>
                        Dashboard
                    </h3>

                    <ol class="help-steps">

                        <li>
                            Use the Dashboard cards to open
                            the main sections of the system.
                        </li>

                        <li>
                            Use the sidebar to move between
                            sections at any time.
                        </li>

                        <li>
                            Select <strong>Help</strong>
                            whenever you need instructions.
                        </li>

                    </ol>

                </article>

                <article class="help-card">

                    <h3>
                        Logout
                    </h3>

                    <ol class="help-steps">

                        <li>
                            Select <strong>Logout</strong>
                            at the bottom of the sidebar.
                        </li>

                        <li>
                            You will be returned to the login page.
                        </li>

                    </ol>

                </article>

            </div>

        </section>

        <div class="help-back-top">

            <a href="#"
               class="btn btn-secondary">
                Back to Top
            </a>

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