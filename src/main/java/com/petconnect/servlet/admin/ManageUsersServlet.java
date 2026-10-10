package com.petconnect.servlet.admin;

import com.petconnect.dao.UserDAO;
import com.petconnect.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/admin/users")
/**
 * Loads user records for the administrator user-management page.
 */
public class ManageUsersServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ManageUsersServlet.class.getName());
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<User> users = userDAO.findAll();
            request.setAttribute("users", users);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while loading users", e);
            request.setAttribute("error", "Could not load users right now.");
        }
        request.getRequestDispatcher("/admin/users.jsp").forward(request, response);
    }
}
