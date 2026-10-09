package com.petconnect.servlet.shelter;

import com.petconnect.dao.PetDAO;
import com.petconnect.model.Pet;
import com.petconnect.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/shelter/edit-pet")
@MultipartConfig(
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 10
)
public class EditPetServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(EditPetServlet.class.getName());
    private final PetDAO petDAO = new PetDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        int shelterId = (int) session.getAttribute("userId");

        int petId;
        try {
            petId = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/shelter/my-pets");
            return;
        }

        try {
            Pet pet = petDAO.findById(petId);

            if (pet == null || pet.getShelterId() != shelterId) {
                response.sendRedirect(request.getContextPath() + "/shelter/my-pets?error=not_found");
                return;
            }

            request.setAttribute("pet", pet);
            request.getRequestDispatcher("/shelter/edit-pet.jsp").forward(request, response);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while loading pet for edit", e);
            response.sendRedirect(request.getContextPath() + "/shelter/my-pets");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        int shelterId = (int) session.getAttribute("userId");

        int petId;
        try {
            petId = Integer.parseInt(request.getParameter("petId"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/shelter/my-pets");
            return;
        }

        String name = request.getParameter("name");
        String type = request.getParameter("type");
        String breed = request.getParameter("breed");
        String age = request.getParameter("age");
        String gender = request.getParameter("gender");
        String location = request.getParameter("location");
        String description = request.getParameter("description");

        try {
            Pet existingPet = petDAO.findById(petId);

            // OWNERSHIP CHECK again on the POST - the GET check only
            // protected the page load; a tampered form or a direct POST
            // (e.g. via curl) could still target a different petId.
            if (existingPet == null || existingPet.getShelterId() != shelterId) {
                response.sendRedirect(request.getContextPath() + "/shelter/my-pets?error=not_found");
                return;
            }

            if (!ValidationUtil.isNonEmpty(name) || !ValidationUtil.isNonEmpty(type)
                    || !ValidationUtil.isNonEmpty(location)) {
                forwardWithError(request, response, existingPet, "Name, type, and location are required.");
                return;
            }
            if (!ValidationUtil.isValidAge(age)) {
                forwardWithError(request, response, existingPet, "Age must be a whole number between 0 and 40.");
                return;
            }
            if (!ValidationUtil.isValidGender(gender)) {
                forwardWithError(request, response, existingPet, "Please select a valid gender.");
                return;
            }

            String imagePath = saveUploadedImageIfPresent(request, existingPet.getImagePath());

            existingPet.setName(name.trim());
            existingPet.setType(type.trim());
            existingPet.setBreed(breed == null ? null : breed.trim());
            existingPet.setAge(Integer.parseInt(age.trim()));
            existingPet.setGender(Pet.Gender.valueOf(gender));
            existingPet.setLocation(location.trim());
            existingPet.setDescription(description);
            existingPet.setImagePath(imagePath);

            petDAO.updatePet(existingPet);

            response.sendRedirect(request.getContextPath() + "/shelter/my-pets?updated=true");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while updating pet", e);
            response.sendRedirect(request.getContextPath() + "/shelter/my-pets");
        }
    }

    private String saveUploadedImageIfPresent(HttpServletRequest request, String existingImagePath)
            throws IOException, ServletException {

        Part filePart = request.getPart("image");
        if (filePart == null || filePart.getSize() == 0) {
            return existingImagePath;
        }

        String originalFileName = extractFileName(filePart);
        String extension = originalFileName.contains(".")
                ? originalFileName.substring(originalFileName.lastIndexOf('.'))
                : "";
        String uniqueFileName = UUID.randomUUID() + extension;

        String uploadDirPath = getServletContext().getRealPath("/uploads/pets/");
        Path uploadDir = Paths.get(uploadDirPath);
        Files.createDirectories(uploadDir);

        Path targetFile = uploadDir.resolve(uniqueFileName);
        try (InputStream input = filePart.getInputStream()) {
            Files.copy(input, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }

        return "uploads/pets/" + uniqueFileName;
    }

    private String extractFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        for (String token : contentDisposition.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return "upload";
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response,
                                   Pet pet, String errorMessage) throws ServletException, IOException {
        request.setAttribute("error", errorMessage);
        request.setAttribute("pet", pet);
        request.getRequestDispatcher("/shelter/edit-pet.jsp").forward(request, response);
    }
}
