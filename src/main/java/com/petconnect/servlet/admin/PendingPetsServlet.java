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

@WebServlet("/admin/pending-pets")
public class PendingPetsServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(PendingPetsServlet.class.getName());
    private final PetDAO petDAO = new PetDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Pet> pendingPets = petDAO.findByStatus(Pet.Status.PENDING);
            request.setAttribute("pets", pendingPets);
            request.getRequestDispatcher("/admin/pending-pets.jsp").forward(request, response);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while loading pending pets", e);
            request.setAttribute("error", "Could not load pending pets.");
            request.getRequestDispatcher("/admin/pending-pets.jsp").forward(request, response);
        }
    }
}
