document.addEventListener("DOMContentLoaded", function () {
    initialiseLoginForm();
    initialiseLoginToast();
    initialiseLogout();
    initialiseLogoutToast();
    initialisePasswordToggle();
    initialiseSidebar();
    initialiseSelectFilters();
    initialiseAppointmentDate();
});

function initialiseLoginForm() {
    const loginForm =
        document.getElementById("loginForm");

    if (!loginForm) {
        return;
    }

    const loginError =
        document.getElementById("loginErrorState");

    if (loginError) {
        sessionStorage.removeItem(
            "sunriseLoginSubmitted"
        );
    }

    loginForm.addEventListener("submit", function () {
        sessionStorage.setItem(
            "sunriseLoginSubmitted",
            "true"
        );
    });
}

function initialiseLoginToast() {
    const toast =
        document.getElementById("loginToast");

    if (!toast) {
        return;
    }

    const loginSubmitted =
        sessionStorage.getItem(
            "sunriseLoginSubmitted"
        );

    if (loginSubmitted !== "true") {
        return;
    }

    sessionStorage.removeItem(
        "sunriseLoginSubmitted"
    );

    showToast(toast);
}

function initialiseLogout() {
    const logoutLink =
        document.getElementById("logoutLink");

    if (!logoutLink) {
        return;
    }

    logoutLink.addEventListener(
        "click",
        function (event) {

            const confirmed =
                window.confirm(
                    "Are you sure you want to logout?"
                );

            if (!confirmed) {
                event.preventDefault();
                return;
            }

            sessionStorage.setItem(
                "sunriseLogoutSubmitted",
                "true"
            );
        }
    );
}

function initialiseLogoutToast() {
    const toast =
        document.getElementById("logoutToast");

    if (!toast) {
        return;
    }

    const logoutSubmitted =
        sessionStorage.getItem(
            "sunriseLogoutSubmitted"
        );

    if (logoutSubmitted !== "true") {
        return;
    }

    sessionStorage.removeItem(
        "sunriseLogoutSubmitted"
    );

    showToast(toast);
}

function showToast(toast) {
    toast.hidden = false;

    const closeButton =
        toast.querySelector(
            "[data-toast-close]"
        );

    function closeToast() {
        toast.classList.add(
            "toast--hide"
        );

        setTimeout(function () {
            toast.remove();
        }, 300);
    }

    if (closeButton) {
        closeButton.addEventListener(
            "click",
            closeToast
        );
    }

    setTimeout(
        closeToast,
        5000
    );
}

function initialisePasswordToggle() {
    const passwordInput =
        document.getElementById("password");

    const toggleButton =
        document.getElementById(
            "togglePassword"
        );

    if (!passwordInput || !toggleButton) {
        return;
    }

    toggleButton.addEventListener(
        "click",
        function () {

            const passwordVisible =
                passwordInput.type === "text";

            if (passwordVisible) {
                passwordInput.type = "password";
                toggleButton.textContent = "Show";
            } else {
                passwordInput.type = "text";
                toggleButton.textContent = "Hide";
            }
        }
    );
}

function initialiseSidebar() {
    const menuButton =
        document.getElementById(
            "mobileMenuButton"
        );

    const overlay =
        document.getElementById(
            "sidebarOverlay"
        );

    if (!menuButton) {
        return;
    }

    menuButton.addEventListener(
        "click",
        function () {
            document.body.classList.toggle(
                "sidebar-open"
            );
        }
    );

    if (overlay) {
        overlay.addEventListener(
            "click",
            function () {
                document.body.classList.remove(
                    "sidebar-open"
                );
            }
        );
    }
}

function initialiseSelectFilters() {
    const filters =
        document.querySelectorAll(
            "[data-select-filter]"
        );

    filters.forEach(function (filter) {
        const selectId =
            filter.getAttribute(
                "data-select-filter"
            );

        const select =
            document.getElementById(
                selectId
            );

        if (!select) {
            return;
        }

        const originalOptions =
            Array.from(select.options).map(
                function (option) {
                    return {
                        value: option.value,
                        text: option.text,
                        selected: option.selected
                    };
                }
            );

        filter.addEventListener(
            "input",
            function () {

                const query =
                    filter.value
                        .trim()
                        .toLowerCase();

                const currentValue =
                    select.value;

                select.innerHTML = "";

                originalOptions.forEach(
                    function (optionData) {

                        const matches =
                            optionData.value === ""
                            || optionData.value
                                .toLowerCase()
                                .includes(query)
                            || optionData.text
                                .toLowerCase()
                                .includes(query);

                        if (!matches) {
                            return;
                        }

                        const option =
                            document.createElement(
                                "option"
                            );

                        option.value =
                            optionData.value;

                        option.textContent =
                            optionData.text;

                        if (optionData.value
                            === currentValue) {
                            option.selected = true;
                        }

                        select.appendChild(
                            option
                        );
                    }
                );
            }
        );
    });
}

function initialiseAppointmentDate() {
    const appointmentDate =
        document.getElementById(
            "appointmentDate"
        );

    if (!appointmentDate) {
        return;
    }

    const today =
        new Date();

    const year =
        today.getFullYear();

    const month =
        String(
            today.getMonth() + 1
        ).padStart(2, "0");

    const day =
        String(
            today.getDate()
        ).padStart(2, "0");

    appointmentDate.min =
        year
        + "-"
        + month
        + "-"
        + day;
}