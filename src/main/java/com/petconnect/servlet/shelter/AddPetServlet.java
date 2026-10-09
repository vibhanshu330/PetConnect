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

/**
 * Handles adding a new pet listing. @MultipartConfig is required for
 * request.getPart(...) to work with the uploaded image file.
 */
@WebServlet("/shelter/add-pet")
@MultipartConfig(
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 10
)
public class AddPetServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AddPetServlet.class.getName());
    private final PetDAO petDAO = new PetDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/shelter/add-pet.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        int shelterId = (int) session.getAttribute("userId");
        // Owner is always read from the session, never from a form field.

        String name = request.getParameter("name");
        String type = request.getParameter("type");
        String breed = request.getParameter("breed");
        String age = request.getParameter("age");
        String gender = request.getParameter("gender");
        String location = request.getParameter("location");
        String description = request.getParameter("description");

        request.setAttribute("name", name);
        request.setAttribute("type", type);
        request.setAttribute("breed", breed);
        request.setAttribute("age", age);
        request.setAttribute("gender", gender);
        request.setAttribute("location", location);
        request.setAttribute("description", description);

        if (!ValidationUtil.isNonEmpty(name) || !ValidationUtil.isNonEmpty(type)
                || !ValidationUtil.isNonEmpty(location)) {
            forwardWithError(request, response, "Name, type, and location are required.");
            return;
        }
        if (!ValidationUtil.isValidAge(age)) {
            forwardWithError(request, response, "Age must be a whole number between 0 and 40.");
            return;
        }
        if (!ValidationUtil.isValidGender(gender)) {
            forwardWithError(request, response, "Please select a valid gender.");
            return;
        }

        try {
            String imagePath = saveUploadedImage(request);

            Pet pet = new Pet(
                    shelterId,
                    name.trim(),
                    type.trim(),
                    breed == null ? null : breed.trim(),
                    Integer.parseInt(age.trim()),
                    Pet.Gender.valueOf(gender),
                    location.trim(),
                    description,
                    imagePath
            );

            petDAO.createPet(pet);

            response.sendRedirect(request.getContextPath() + "/shelter/my-pets?added=true");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while adding pet", e);
            forwardWithError(request, response, "Something went wrong. Please try again.");
        }
    }

    private String saveUploadedImage(HttpServletRequest request) throws IOException, ServletException {
        Part filePart = request.getPart("image");

        if (filePart == null || filePart.getSize() == 0) {
            return null;
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
                                   String errorMessage) throws ServletException, IOException {
        request.setAttribute("error", errorMessage);
        request.getRequestDispatcher("/shelter/add-pet.jsp").forward(request, response);
    }
}
