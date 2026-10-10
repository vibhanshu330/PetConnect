package com.petconnect.servlet.adopter;

import com.petconnect.dao.AdoptionApplicationDAO;
import com.petconnect.dao.PetDAO;
import com.petconnect.model.AdoptionApplication;
import com.petconnect.model.AdoptionApplicationView;
import com.petconnect.model.Pet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/adopter/my-applications")
/**
 * Builds the signed-in adopter's application list with related pet details and statuses.
 */
public class MyApplicationsServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(MyApplicationsServlet.class.getName());
    private final AdoptionApplicationDAO applicationDAO = new AdoptionApplicationDAO();
    private final PetDAO petDAO = new PetDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        // Scoped STRICTLY to the logged-in adopter's own userId from the
        // session - never accepted from a request parameter. Accepting
        // an adopterId from the URL/form here would let one adopter view
        // another adopter's applications simply by changing an ID - a
        // textbook IDOR vulnerability, exactly what Phase 5's ownership
        // checks were built to avoid on the shelter side.
        int adopterId = (int) session.getAttribute("userId");

        try {
            List<AdoptionApplication> applications = applicationDAO.findByAdopterId(adopterId);
            List<AdoptionApplicationView> views = new ArrayList<>();

            for (AdoptionApplication app : applications) {
                Pet pet = petDAO.findById(app.getPetId());
                // Defensive null-check only - in practice this should
                // never trigger, because ON DELETE CASCADE on
                // adoption_applications.pet_id (Phase 2 schema) means an
                // application row cannot outlive its pet; if the pet were
                // deleted, this application row would already be gone too.
                String petName = pet != null ? pet.getName() : "(pet no longer listed)";
                String petType = pet != null ? pet.getType() : "-";
                String petBreed = pet != null ? pet.getBreed() : "-";

                views.add(new AdoptionApplicationView(
                        app.getApplicationId(), petName, petType, petBreed,
                        app.getMessage(), app.getAppliedAt(), app.getStatus(), app.getDecidedAt()
                ));
            }

            request.setAttribute("applications", views);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while loading applications", e);
            request.setAttribute("error", "Could not load your applications right now.");
        }

        request.getRequestDispatcher("/adopter/my-applications.jsp").forward(request, response);
    }
}
