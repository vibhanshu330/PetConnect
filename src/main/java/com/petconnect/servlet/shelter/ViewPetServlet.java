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

@WebServlet("/shelter/view-pet")
/**
 * Loads a shelter-owned pet listing for viewing its details.
 */
public class ViewPetServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ViewPetServlet.class.getName());
    private final PetDAO petDAO = new PetDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        int shelterId = (int) session.getAttribute("userId");

        int petId;
        try {
            petId = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/shelter/my-pets");
            return;
        }

        try {
            Pet pet = petDAO.findById(petId);

            // OWNERSHIP CHECK: this pet must belong to the logged-in shelter,
            // preventing IDOR (one shelter viewing another's pet by editing
            // the ?id= value in the URL).
            if (pet == null || pet.getShelterId() != shelterId) {
                response.sendRedirect(request.getContextPath() + "/shelter/my-pets?error=not_found");
                return;
            }

            request.setAttribute("pet", pet);
            request.getRequestDispatcher("/shelter/view-pet.jsp").forward(request, response);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while viewing pet", e);
            response.sendRedirect(request.getContextPath() + "/shelter/my-pets");
        }
    }
}
