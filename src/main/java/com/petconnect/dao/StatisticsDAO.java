package com.petconnect.dao;

import com.petconnect.model.PlatformStats;
import com.petconnect.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object dedicated to platform-wide statistics. Unlike
 * UserDAO/PetDAO, this doesn't map to one table - it runs SQL
 * aggregation queries (GROUP BY, COUNT) across users, pets, and
 * adoption_applications and bundles the results into a PlatformStats DTO.
 *
 * Kept separate from UserDAO/PetDAO deliberately: those two answer
 * "give me a User/Pet row," this one answers "give me a number about
 * many rows at once" - a different kind of question, so a different class.
 */
public class StatisticsDAO {

    public PlatformStats getPlatformStats() throws SQLException {

        int totalUsers = 0;
        int totalShelters = 0;
        int totalAdopters = 0;
        int totalPets = 0;
        int pendingPets = 0;
        int availablePets = 0;
        int adoptedPets = 0;
        int totalApplications = 0;

        // One Connection reused for all three queries below, rather than
        // opening/closing a fresh connection per query - a small but real
        // efficiency improvement over calling DBConnection.getConnection()
        // three separate times, since dashboard stats are typically loaded
        // together on a single page view.
        try (Connection conn = DBConnection.getConnection()) {

            // ---- Query 1: users grouped by role ----
            // GROUP BY collapses all rows sharing a role into one row per
            // role, with COUNT(*) counting how many rows fell into each
            // group - one query instead of three separate
            // "SELECT COUNT(*) WHERE role = ..." calls.
            String userSql = "SELECT role, COUNT(*) AS total FROM users GROUP BY role";
            try (PreparedStatement stmt = conn.prepareStatement(userSql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String role = rs.getString("role");
                    int count = rs.getInt("total");
                    totalUsers += count; // running sum across all roles
                    if ("SHELTER".equals(role)) {
                        totalShelters = count;
                    } else if ("ADOPTER".equals(role)) {
                        totalAdopters = count;
                    }
                    // ADMIN count is folded into totalUsers but not
                    // surfaced separately - not part of the required stats.
                }
            }

            // ---- Query 2: pets grouped by status ----
            String petSql = "SELECT status, COUNT(*) AS total FROM pets GROUP BY status";
            try (PreparedStatement stmt = conn.prepareStatement(petSql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String status = rs.getString("status");
                    int count = rs.getInt("total");
                    totalPets += count;
                    switch (status) {
                        case "PENDING":
                            pendingPets = count;
                            break;
                        case "AVAILABLE":
                            availablePets = count;
                            break;
                        case "ADOPTED":
                            adoptedPets = count;
                            break;
                        default:
                            // REJECTED still counted in totalPets above,
                            // just not broken out as its own required stat.
                            break;
                    }
                }
            }

            // ---- Query 3: total adoption applications ----
            String appSql = "SELECT COUNT(*) AS total FROM adoption_applications";
            try (PreparedStatement stmt = conn.prepareStatement(appSql);
                 ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    totalApplications = rs.getInt("total");
                }
            }
        }

        return new PlatformStats(totalUsers, totalShelters, totalAdopters,
                totalPets, pendingPets, availablePets, adoptedPets, totalApplications);
    }
}
