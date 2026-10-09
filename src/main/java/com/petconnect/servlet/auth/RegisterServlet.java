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
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles GET (show form) and POST (process registration) for /register.
 * Only SHELTER and ADOPTER can self-register; ADMIN is seeded directly
 * in the database (see PasswordHashGenerator).
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(RegisterServlet.class.getName());
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String phone = request.getParameter("phone");
        String role = request.getParameter("role");

        request.setAttribute("name", name);
        request.setAttribute("email", email);
        request.setAttribute("phone", phone);
        request.setAttribute("role", role);

        if (!ValidationUtil.isNonEmpty(name)) {
            forwardWithError(request, response, "Name is required.");
            return;
        }
        if (!ValidationUtil.isValidEmail(email)) {
            forwardWithError(request, response, "Please enter a valid email address.");
            return;
        }
        if (!ValidationUtil.isValidPassword(password)) {
            forwardWithError(request, response, "Password must be at least 6 characters long.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            forwardWithError(request, response, "Passwords do not match.");
            return;
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            forwardWithError(request, response, "Phone number must be exactly 10 digits.");
            return;
        }
        if (!ValidationUtil.isValidRole(role)) {
            forwardWithError(request, response, "Invalid role selected.");
            return;
        }

        try {
            if (userDAO.findByEmail(email) != null) {
                forwardWithError(request, response, "An account with this email already exists.");
                return;
            }

            String hashedPassword = PasswordUtil.hashPassword(password);

            User newUser = new User(
                    name.trim(),
                    email.trim().toLowerCase(),
                    hashedPassword,
                    phone,
                    User.Role.valueOf(role)
            );

            userDAO.createUser(newUser);

            response.sendRedirect(request.getContextPath() + "/login.jsp?registered=true");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during registration", e);
            forwardWithError(request, response, "Something went wrong. Please try again later.");
        }
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response,
                                   String errorMessage) throws ServletException, IOException {
        request.setAttribute("error", errorMessage);
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }
}
