package com.petconnect.servlet.adopter;

import com.petconnect.dao.AdoptionApplicationDAO;
import com.petconnect.dao.PetDAO;
import com.petconnect.model.AdoptionApplication;
import com.petconnect.model.Pet;
import com.petconnect.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * POST-only, same reasoning as the Phase 5/6 delete servlets: a
 * GET-triggered submission would be vulnerable to link prefetching or
 * accidental resubmission.
 */
@WebServlet("/adopter/apply")
public class ApplyAdoptionServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ApplyAdoptionServlet.class.getName());
    private final PetDAO petDAO = new PetDAO();
    private final AdoptionApplicationDAO applicationDAO = new AdoptionApplicationDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        // adopterId ALWAYS comes from the session, never from a hidden
        // form field or request parameter - a tampered form could
        // otherwise submit an application on behalf of any user ID.
        int adopterId = (int) session.getAttribute("userId");

        int petId;
        try {
            petId = Integer.parseInt(request.getParameter("petId"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/adopter/pets");
            return;
        }

        String message = request.getParameter("message");

        if (!ValidationUtil.isNonEmpty(message)) {
            response.sendRedirect(request.getContextPath() +
                    "/adopter/pet-details?id=" + petId + "&error=message_required");
            return;
        }

        try {
            // SERVER-SIDE RE-CHECK: the pet must still be AVAILABLE right
            // now, at submission time - never trust that the page the
            // adopter is looking at is still accurate. Between page load
            // and form submit, a shelter could have edited it, an admin
            // could have removed it, or another adopter's application
            // could have already been approved (pet -> ADOPTED).
            Pet pet = petDAO.findById(petId);
            if (pet == null || pet.getStatus() != Pet.Status.AVAILABLE) {
                response.sendRedirect(request.getContextPath() + "/adopter/pets?error=pet_unavailable");
                return;
            }

            // DUPLICATE CHECK #1 (application-level, checked BEFORE the
            // insert): the normal-case path, gives a clean friendly
            // message without ever touching the database's error path.
            if (applicationDAO.findByPetAndAdopter(petId, adopterId) != null) {
                response.sendRedirect(request.getContextPath() +
                        "/adopter/pet-details?id=" + petId + "&error=already_applied");
                return;
            }

            AdoptionApplication application = new AdoptionApplication(petId, adopterId, message.trim());
            applicationDAO.createApplication(application);

            response.sendRedirect(request.getContextPath() + "/adopter/my-applications?applied=true");

        } catch (SQLIntegrityConstraintViolationException e) {
            // DUPLICATE CHECK #2 (database-level safety net): if two
            // requests for the same (pet_id, adopter_id) pair arrive at
            // nearly the same instant - e.g. a double-click, or two
            // browser tabs - both could pass the check-then-act logic
            // above before either has actually inserted (a classic
            // TOCTOU race condition). The UNIQUE(pet_id, adopter_id)
            // constraint from the Phase 2 schema is the real,
            // unbypassable guarantee; this catch block only translates
            // that database-level rejection into the same friendly
            // message instead of a raw error page. The constraint itself
            // is never modified or removed.
            LOGGER.log(Level.WARNING, "Duplicate application insert blocked by UNIQUE constraint", e);
            response.sendRedirect(request.getContextPath() +
                    "/adopter/pet-details?id=" + petId + "&error=already_applied");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while submitting application", e);
            response.sendRedirect(request.getContextPath() +
                    "/adopter/pet-details?id=" + petId + "&error=submit_failed");
        }
    }
}
