package com.petconnect.servlet;

import com.petconnect.dao.UserDAO;
import com.petconnect.model.User;
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

/** Shows and updates the profile belonging to the currently authenticated session. */
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ProfileServlet.class.getName());
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer userId = getSessionUserId(session);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required");
            return;
        }

        try {
            User user = userDAO.findById(userId);
            if (user == null) {
                session.invalidate();
                response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required");
                return;
            }
            request.setAttribute("profile", user);
            request.getRequestDispatcher("/WEB-INF/profile.jsp").forward(request, response);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while loading profile", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Could not load your profile right now.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Integer userId = getSessionUserId(session);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=login_required");
            return;
        }

        String name = request.getParameter("name");
        String phone = request.getParameter("phone");
        request.setAttribute("profileFormSubmitted", true);
        request.setAttribute("profileName", name);
        request.setAttribute("profilePhone", phone);
        if (!ValidationUtil.isValidProfileName(name)) {
            showProfileWithError(request, response, userId, "Name is required and must be 100 characters or fewer.");
            return;
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            showProfileWithError(request, response, userId, "Phone number must be empty or exactly 10 digits.");
            return;
        }

        try {
            String cleanName = name.trim();
            String cleanPhone = phone == null || phone.trim().isEmpty() ? null : phone.trim();
            if (!userDAO.updateProfile(userId, cleanName, cleanPhone)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Profile not found.");
                return;
            }
            session.setAttribute("userName", cleanName);
            response.sendRedirect(request.getContextPath() + "/profile?updated=true");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while updating profile", e);
            showProfileWithError(request, response, userId,
                    "Could not save your profile right now. Please try again later.");
        }
    }

    private void showProfileWithError(HttpServletRequest request, HttpServletResponse response,
                                      int userId, String message) throws ServletException, IOException {
        try {
            request.setAttribute("profile", userDAO.findById(userId));
            request.setAttribute("error", message);
            request.getRequestDispatcher("/WEB-INF/profile.jsp").forward(request, response);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while reloading profile", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Could not load your profile right now.");
        }
    }

    private Integer getSessionUserId(HttpSession session) {
        if (session == null) return null;
        Object value = session.getAttribute("userId");
        return value instanceof Integer ? (Integer) value : null;
    }
}
