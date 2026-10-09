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
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/admin/reject-pet")
public class RejectPetServlet extends HttpServlet {

    private static final Logger LOGGER =
            Logger.getLogger(RejectPetServlet.class.getName());
    private final PetDAO petDAO = new PetDAO();

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int petId = Integer.parseInt(request.getParameter("petId"));
            boolean updated =
                    petDAO.updateStatus(petId, Pet.Status.REJECTED);

            String result = updated
                    ? "?rejected=true"
                    : "?error=true";

            response.sendRedirect(
                    request.getContextPath() + "/admin/pending-pets"
                            + result);
        } catch (NumberFormatException | SQLException e) {
            LOGGER.log(Level.SEVERE, "Error rejecting pet", e);
            response.sendRedirect(
                    request.getContextPath()
                            + "/admin/pending-pets?error=true");
        }
    }
}
