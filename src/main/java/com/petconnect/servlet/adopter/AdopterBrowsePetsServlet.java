package com.petconnect.servlet.adopter;

import com.petconnect.dao.PetDAO;
import com.petconnect.model.Pet;
import com.petconnect.model.PetRecommendation;
import com.petconnect.service.CompatibilityScoringService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Role authorization for everything under /adopter/* is already handled
 * by AuthFilter (Phase 4) before this servlet ever runs - no role check
 * is duplicated here, consistent with how the Phase 5/6 shelter and
 * admin servlets rely on the same filter.
 */
@WebServlet("/adopter/pets")
public class AdopterBrowsePetsServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AdopterBrowsePetsServlet.class.getName());
    private final PetDAO petDAO = new PetDAO();
    private final CompatibilityScoringService scoringService = new CompatibilityScoringService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("q");
        String type = request.getParameter("type");
        String breed = request.getParameter("breed");
        String location = request.getParameter("location");
        Integer maximumAge = null;
        String agePreference = request.getParameter("maxAge");
        if (agePreference != null && !agePreference.trim().isEmpty()) {
            try {
                int parsedAge = Integer.parseInt(agePreference.trim());
                if (parsedAge >= 0 && parsedAge <= 40) maximumAge = parsedAge;
                else request.setAttribute("ageError", "Choose an age from 0 to 40.");
            } catch (NumberFormatException e) {
                request.setAttribute("ageError", "Choose an age from 0 to 40.");
            }
        }

        // Echo the submitted values back as request attributes so the
        // search/filter form shows what was searched for, instead of
        // resetting blank after every submission - the "preserve
        // selected filter values" requirement.
        request.setAttribute("q", keyword);
        request.setAttribute("type", type);
        request.setAttribute("breed", breed);
        request.setAttribute("location", location);
        request.setAttribute("maxAge", maximumAge == null ? "" : maximumAge);

        try {
            List<Pet> pets = petDAO.searchAvailablePets(keyword, type, breed, location);
            request.setAttribute("pets", pets);
            List<PetRecommendation> recommendations = new ArrayList<>();
            for (Pet pet : pets) {
                recommendations.add(new PetRecommendation(pet,
                        scoringService.score(pet, type, breed, location, maximumAge)));
            }
            recommendations.sort(Comparator.comparingInt(
                    (PetRecommendation item) -> item.getCompatibility().getPercentage()).reversed());
            request.setAttribute("recommendations", recommendations);
            request.setAttribute("hasPreferences", (type != null && !type.trim().isEmpty())
                    || (breed != null && !breed.trim().isEmpty())
                    || (location != null && !location.trim().isEmpty()) || maximumAge != null);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while browsing pets", e);
            request.setAttribute("error", "Could not load pets right now.");
        }

        request.getRequestDispatcher("/adopter/browse-pets.jsp").forward(request, response);
    }
}
