package com.petconnect.servlet.auth;

import com.petconnect.dao.UserDAO;
import com.petconnect.model.User;
import com.petconnect.util.PasswordUtil;
import com.petconnect.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/login")
/**
 * Validates login submissions and creates a session for an authenticated user.
 */
public class LoginServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (!ValidationUtil.isNonEmpty(email) || !ValidationUtil.isNonEmpty(password)) {
            forwardWithError(request, response, "Email and password are required.");
            return;
        }

        try {
            User user = userDAO.findByEmail(email.trim().toLowerCase());

            if (user == null || !PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                forwardWithError(request, response, "Invalid email or password.");
                return;
            }

            // Upgrade the old salted SHA-256 representation only after a valid login.
            if (PasswordUtil.isLegacyHash(user.getPasswordHash())) {
                try {
                    userDAO.updatePasswordHash(user.getUserId(), PasswordUtil.hashPassword(password));
                } catch (SQLException migrationFailure) {
                    // Do not lock out a valid existing account if opportunistic migration
                    // cannot be written; retry on its next successful login.
                    LOGGER.log(Level.WARNING, "Could not upgrade password hash for user " + user.getUserId(), migrationFailure);
                }
            }

            // Prevent session fixation: invalidate any pre-existing session
            // and start a fresh one.
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession session = request.getSession(true);

            session.setAttribute("userId", user.getUserId());
            session.setAttribute("userName", user.getName());
            session.setAttribute("userRole", user.getRole().name());
            session.setMaxInactiveInterval(30 * 60);

            switch (user.getRole()) {
                case ADMIN:
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
                    break;
                case SHELTER:
                    response.sendRedirect(request.getContextPath() + "/shelter/dashboard.jsp");
                    break;
                case ADOPTER:
                    response.sendRedirect(request.getContextPath() + "/adopter/dashboard.jsp");
                    break;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during login", e);
            forwardWithError(request, response, "Something went wrong. Please try again later.");
        }
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response,
                                   String errorMessage) throws ServletException, IOException {
        request.setAttribute("error", errorMessage);
        request.setAttribute("email", request.getParameter("email"));
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}
