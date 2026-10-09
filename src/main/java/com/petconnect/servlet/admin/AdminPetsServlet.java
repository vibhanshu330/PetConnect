package com.petconnect.servlet.admin;

import com.petconnect.dao.PetDAO;
import com.petconnect.model.Pet;

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

/**
 * Distinct from PendingPetsServlet (Phase 5), which only lists PENDING
 * pets for approval. This lists EVERY pet regardless of status, across
 * every shelter - the "Pet Management" view, as opposed to the
 * "Pending Pet Approvals" view.
 */
@WebServlet("/admin/pets")
public class AdminPetsServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AdminPetsServlet.class.getName());
    private final PetDAO petDAO = new PetDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Pet> pets = petDAO.findAll();
            request.setAttribute("pets", pets);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while loading all pets", e);
            request.setAttribute("error", "Could not load pets right now.");
        }
        request.getRequestDispatcher("/admin/pets.jsp").forward(request, response);
    }
}
