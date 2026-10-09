package com.petconnect.util;

import com.petconnect.dao.UserDAO;
import com.petconnect.model.User;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Standalone test harness for Phase 3 - verifies DBConnection and UserDAO
 * work correctly. Not part of the web application; run manually via main().
 */
public class TestDBConnection {

    public static void main(String[] args) {

        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("Connected successfully to database: " + conn.getCatalog());
        } catch (SQLException e) {
            System.out.println("Connection FAILED.");
            e.printStackTrace();
            return;
        }

        UserDAO userDAO = new UserDAO();

        try {
            User newUser = new User(
                    "Test User",
                    "testuser@petconnect.com",
                    "TEMP_HASH_FOR_TESTING_ONLY",
                    "9998887777",
                    User.Role.ADOPTER
            );
            int newId = userDAO.createUser(newUser);
            System.out.println("Created user, new ID = " + newId);

            User fetchedById = userDAO.findById(newId);
            System.out.println("findById result: " + fetchedById);

            User fetchedByEmail = userDAO.findByEmail("testuser@petconnect.com");
            System.out.println("findByEmail result: " + fetchedByEmail);

            fetchedById.setName("Test User (Updated)");
            fetchedById.setPhone("9990001111");
            boolean updated = userDAO.updateUser(fetchedById);
            System.out.println("Update successful: " + updated);
            System.out.println("After update: " + userDAO.findById(newId));

            boolean deleted = userDAO.deleteUser(newId);
            System.out.println("Delete successful: " + deleted);
            System.out.println("After delete (expect null): " + userDAO.findById(newId));

        } catch (SQLException e) {
            System.out.println("A database operation failed.");
            e.printStackTrace();
        }
    }
}
