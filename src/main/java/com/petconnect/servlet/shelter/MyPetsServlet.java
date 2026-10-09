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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/shelter/my-pets")
public class MyPetsServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(MyPetsServlet.class.getName());
    private final PetDAO petDAO = new PetDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        int shelterId = (int) session.getAttribute("userId");

        try {
            List<Pet> pets = petDAO.findByShelterId(shelterId);
            request.setAttribute("pets", pets);
            request.getRequestDispatcher("/shelter/my-pets.jsp").forward(request, response);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while loading shelter's pets", e);
            request.setAttribute("error", "Could not load your pets right now.");
            request.getRequestDispatcher("/shelter/my-pets.jsp").forward(request, response);
        }
    }
}
