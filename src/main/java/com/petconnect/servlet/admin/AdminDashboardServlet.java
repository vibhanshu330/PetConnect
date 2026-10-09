package com.petconnect.servlet.admin;

import com.petconnect.dao.StatisticsDAO;
import com.petconnect.model.PlatformStats;

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
 * IMPORTANT - URL pattern design note:
 *
 * This is mapped to the EXACT same URL, "/admin/dashboard.jsp", that
 * LoginServlet already redirects ADMIN users to (see Phase 4), and that
 * the navbar link in admin/pending-pets.jsp already points at.
 *
 * Per the Servlet specification, an application-defined servlet mapping
 * to an exact path takes precedence over the container's own JSP-serving
 * mechanism for that same path - even if a physical .jsp file with that
 * exact name exists on disk. So requests to "/admin/dashboard.jsp" now
 * arrive HERE first, instead of being handed straight to the JSP engine.
 *
 * This means neither LoginServlet nor any existing link/redirect needed
 * to change to add live statistics to the dashboard - this servlet loads
 * the data, then forwards to a DIFFERENTLY NAMED physical file,
 * "dashboard-view.jsp", for rendering. (Forwarding back to
 * "/admin/dashboard.jsp" itself would loop forever, since that URL is
 * now claimed by this servlet, not the JSP engine.)
 */
@WebServlet("/admin/dashboard.jsp")
public class AdminDashboardServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AdminDashboardServlet.class.getName());
    private final StatisticsDAO statisticsDAO = new StatisticsDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            PlatformStats stats = statisticsDAO.getPlatformStats();
            request.setAttribute("stats", stats);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while loading platform statistics", e);
            request.setAttribute("error", "Could not load platform statistics right now.");
        }

        request.getRequestDispatcher("/admin/dashboard-view.jsp").forward(request, response);
    }
}
