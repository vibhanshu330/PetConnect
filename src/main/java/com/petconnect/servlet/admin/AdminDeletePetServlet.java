package com.petconnect.servlet.admin;

import com.petconnect.dao.PetDAO;

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
 * Admin-side pet deletion - deliberately a SEPARATE servlet and URL
 * ("/admin/delete-pet") from the shelter's "/shelter/delete-pet"
 * (Phase 5), even though both ultimately call PetDAO.deletePet().
 *
 * Why not just reuse the shelter servlet? Because it enforces an
 * ownership check ("does this pet belong to YOU, the logged-in
 * shelter?") that would incorrectly block an admin from deleting a
 * pet belonging to some other shelter - which is exactly what admin
 * oversight is supposed to allow. Same underlying DAO call, different
 * authorization rule, so a different servlet.
 */
@WebServlet("/admin/delete-pet")
public class AdminDeletePetServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AdminDeletePetServlet.class.getName());
    private final PetDAO petDAO = new PetDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int petId;
        try {
            petId = Integer.parseInt(request.getParameter("petId"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/pets");
            return;
        }

        try {
            petDAO.deletePet(petId);
            response.sendRedirect(request.getContextPath() + "/admin/pets?deleted=true");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while deleting pet (admin)", e);
            response.sendRedirect(request.getContextPath() + "/admin/pets?error=delete_failed");
        }
    }
}
