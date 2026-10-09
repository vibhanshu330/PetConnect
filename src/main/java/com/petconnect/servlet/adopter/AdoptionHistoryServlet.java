package com.petconnect.servlet.adopter;

import com.petconnect.dao.AdoptionHistoryDAO;
import com.petconnect.model.AdoptionHistoryView;

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

@WebServlet("/adopter/history")
public class AdoptionHistoryServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AdoptionHistoryServlet.class.getName());
    private final AdoptionHistoryDAO historyDAO = new AdoptionHistoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        int adopterId = (int) session.getAttribute("userId");
        try {
            List<AdoptionHistoryView> history = historyDAO.findByAdopterId(adopterId);
            request.setAttribute("history", history);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while loading adoption history", e);
            request.setAttribute("error", "Could not load your adoption history right now.");
        }
        request.getRequestDispatcher("/adopter/history.jsp").forward(request, response);
    }
}
