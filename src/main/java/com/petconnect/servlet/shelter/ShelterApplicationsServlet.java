package com.petconnect.servlet.shelter;

import com.petconnect.dao.AdoptionApplicationDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/shelter/applications")
public class ShelterApplicationsServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ShelterApplicationsServlet.class.getName());
    private final AdoptionApplicationDAO applicationDAO = new AdoptionApplicationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int shelterId = (int) request.getSession(false).getAttribute("userId");
        try {
            request.setAttribute("applications", applicationDAO.findByShelterId(shelterId));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not load shelter applications", e);
            request.setAttribute("error", "Could not load applications right now.");
        }
        request.getRequestDispatcher("/shelter/applications.jsp").forward(request, response);
    }
}
