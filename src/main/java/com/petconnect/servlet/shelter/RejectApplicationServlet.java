package com.petconnect.servlet.shelter;

import com.petconnect.dao.AdoptionApplicationDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/shelter/reject-application")
public class RejectApplicationServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(RejectApplicationServlet.class.getName());
    private final AdoptionApplicationDAO applicationDAO = new AdoptionApplicationDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int shelterId = (int) request.getSession(false).getAttribute("userId");
        int applicationId = parseId(request.getParameter("applicationId"));
        String result = "invalid";
        if (applicationId > 0) {
            try {
                result = applicationDAO.rejectApplication(applicationId, shelterId) ? "rejected" : "unavailable";
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Could not reject application " + applicationId, e);
                result = "error";
            }
        }
        response.sendRedirect(request.getContextPath() + "/shelter/applications?result=" + result);
    }

    private int parseId(String value) {
        try { return Integer.parseInt(value); } catch (RuntimeException e) { return -1; }
    }
}
