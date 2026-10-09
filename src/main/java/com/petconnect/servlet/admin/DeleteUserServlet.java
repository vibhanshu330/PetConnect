package com.petconnect.servlet.admin;

import com.petconnect.dao.UserDAO;

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

/**
 * POST-only, same reasoning as DeletePetServlet in Phase 5: a GET-triggered
 * delete is vulnerable to link prefetching or accidental triggering.
 *
 * Unlike the Phase 5 shelter delete servlets, there is NO per-row ownership
 * check needed here - an ADMIN is, by design, authorized to manage every
 * user on the platform. AuthFilter already confirmed "this is a logged-in
 * ADMIN" before this servlet runs, and that role-level check IS sufficient
 * authority for this action (contrast with Phase 5, where a SHELTER role
 * check alone was NOT enough - we also needed a pet-level ownership check,
 * because a shelter's authority is scoped to only their own pets).
 *
 * The one extra guard needed here is different in kind: preventing an
 * admin from deleting their OWN account and locking themselves out.
 */
@WebServlet("/admin/delete-user")
public class DeleteUserServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DeleteUserServlet.class.getName());
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        int currentAdminId = (int) session.getAttribute("userId");

        int targetUserId;
        try {
            targetUserId = Integer.parseInt(request.getParameter("userId"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }

        if (targetUserId == currentAdminId) {
            response.sendRedirect(request.getContextPath() + "/admin/users?error=cannot_delete_self");
            return;
        }

        try {
            // NOTE: if the deleted user is a SHELTER, ON DELETE CASCADE
            // (set up in the Phase 2 schema) automatically removes their
            // pets, and any applications/messages/history referencing
            // those pets, in the same operation. That cascade happens
            // inside MySQL itself, not in this Java code - worth
            // mentioning if an examiner asks "what happens to a
            // shelter's pets when you delete the shelter?"
            userDAO.deleteUser(targetUserId);
            response.sendRedirect(request.getContextPath() + "/admin/users?deleted=true");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while deleting user", e);
            response.sendRedirect(request.getContextPath() + "/admin/users?error=delete_failed");
        }
    }
}
