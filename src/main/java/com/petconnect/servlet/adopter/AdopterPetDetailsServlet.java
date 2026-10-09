package com.petconnect.servlet.adopter;

import com.petconnect.dao.AdoptionApplicationDAO;
import com.petconnect.dao.PetDAO;
import com.petconnect.model.Pet;
import com.petconnect.service.CompatibilityScoringService;

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

@WebServlet("/adopter/pet-details")
public class AdopterPetDetailsServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AdopterPetDetailsServlet.class.getName());
    private final PetDAO petDAO = new PetDAO();
    private final AdoptionApplicationDAO applicationDAO = new AdoptionApplicationDAO();
    private final CompatibilityScoringService scoringService = new CompatibilityScoringService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        int adopterId = (int) session.getAttribute("userId");

        int petId;
        try {
            petId = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/adopter/pets");
            return;
        }

        try {
            Pet pet = petDAO.findById(petId);

            // STATUS CHECK: adopters may only view AVAILABLE pets, even
            // if they type/edit a PENDING, REJECTED, or ADOPTED pet's ID
            // directly into the URL. This is what stops someone from
            // browsing a pet that's mid-review or already gone.
            if (pet == null || pet.getStatus() != Pet.Status.AVAILABLE) {
                response.sendRedirect(request.getContextPath() + "/adopter/pets?error=not_available");
                return;
            }

            // Look up whether THIS adopter (from session) has already
            // applied, so the JSP can show "already applied" instead of
            // the apply form - a UX nicety, not itself the security
            // control (ApplyAdoptionServlet re-checks this server-side
            // regardless of what the page displayed).
            boolean alreadyApplied = applicationDAO.findByPetAndAdopter(petId, adopterId) != null;

            request.setAttribute("pet", pet);
            String preferredType = request.getParameter("type");
            String preferredBreed = request.getParameter("breed");
            String preferredLocation = request.getParameter("location");
            Integer maximumAge = null;
            try {
                String age = request.getParameter("maxAge");
                if (age != null && !age.trim().isEmpty()) {
                    int parsed = Integer.parseInt(age.trim());
                    if (parsed >= 0 && parsed <= 40) maximumAge = parsed;
                }
            } catch (NumberFormatException ignored) { }
            request.setAttribute("compatibility", scoringService.score(pet, preferredType,
                    preferredBreed, preferredLocation, maximumAge));
            request.setAttribute("hasPreferences", (preferredType != null && !preferredType.trim().isEmpty())
                    || (preferredBreed != null && !preferredBreed.trim().isEmpty())
                    || (preferredLocation != null && !preferredLocation.trim().isEmpty()) || maximumAge != null);
            request.setAttribute("alreadyApplied", alreadyApplied);
            request.getRequestDispatcher("/adopter/pet-details.jsp").forward(request, response);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while loading pet details", e);
            response.sendRedirect(request.getContextPath() + "/adopter/pets");
        }
    }
}
