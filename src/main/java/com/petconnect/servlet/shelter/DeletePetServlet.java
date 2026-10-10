package com.petconnect.servlet.shelter;

import com.petconnect.dao.PetDAO;
import com.petconnect.model.Pet;

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

@WebServlet("/shelter/delete-pet")
/**
 * Processes a shelter request to remove one of its own pet listings.
 */
public class DeletePetServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DeletePetServlet.class.getName());
    private final PetDAO petDAO = new PetDAO();

    // POST-only: a GET-triggered delete would be vulnerable to link
    // prefetching or CSRF-style accidental deletion.
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        int shelterId = (int) session.getAttribute("userId");

        int petId;
        try {
            petId = Integer.parseInt(request.getParameter("petId"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/shelter/my-pets");
            return;
        }

        try {
            Pet pet = petDAO.findById(petId);

            if (pet == null || pet.getShelterId() != shelterId) {
                response.sendRedirect(request.getContextPath() + "/shelter/my-pets?error=not_found");
                return;
            }

            petDAO.deletePet(petId);
            response.sendRedirect(request.getContextPath() + "/shelter/my-pets?deleted=true");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while deleting pet", e);
            response.sendRedirect(request.getContextPath() + "/shelter/my-pets?error=delete_failed");
        }
    }
}
