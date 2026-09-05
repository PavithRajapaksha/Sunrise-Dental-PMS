package com.sunrise.sunrisedentalpms.util;

import com.sunrise.sunrisedentalpms.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class SessionUtil {

    private SessionUtil() {
    }

    // Reads the logged-in user from the session, or null if not logged in
    public static User getLoggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute("loggedInUser");
    }

    // Ends the current session
    public static void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}