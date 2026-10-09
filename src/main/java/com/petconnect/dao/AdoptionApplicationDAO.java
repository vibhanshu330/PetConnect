package com.petconnect.dao;

import com.petconnect.model.AdoptionApplication;
import com.petconnect.model.ShelterApplicationView;
import com.petconnect.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the "adoption_applications" table. Same shape
 * as UserDAO/PetDAO: try-with-resources everywhere, PreparedStatement
 * everywhere, SQLException propagated to the caller rather than
 * swallowed here.
 */
public class AdoptionApplicationDAO {

    public int createApplication(AdoptionApplication application) throws SQLException {
        String sql = "INSERT INTO adoption_applications (pet_id, adopter_id, status, message) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, application.getPetId());
            stmt.setInt(2, application.getAdopterId());
            stmt.setString(3, application.getStatus().name());
            stmt.setString(4, application.getMessage());

            // NOTE: if two requests for the same (pet_id, adopter_id) pair
            // race each other, MySQL's UNIQUE(pet_id, adopter_id)
            // constraint (Phase 2 schema) rejects the second executeUpdate()
            // with a SQLIntegrityConstraintViolationException - this method
            // does not catch it here, so it propagates to the caller
            // (ApplyAdoptionServlet), which is where the friendly
            // "already applied" message gets shown.
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Creating application failed, no rows affected.");
            }

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                throw new SQLException("Creating application failed, no ID obtained.");
            }
        }
    }

    /**
     * Looks up whether this exact adopter has already applied for this
     * exact pet. Used both to show "already applied" in the UI and as
     * the pre-insert duplicate check in ApplyAdoptionServlet. Returns
     * null if no such application exists.
     */
    public AdoptionApplication findByPetAndAdopter(int petId, int adopterId) throws SQLException {
        String sql = "SELECT * FROM adoption_applications WHERE pet_id = ? AND adopter_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, petId);
            stmt.setInt(2, adopterId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /**
     * All applications submitted by one adopter, newest first - used by
     * "My Applications". Scoped by adopterId, which the caller must have
     * taken from the session, never from a request parameter.
     */
    public List<AdoptionApplication> findByAdopterId(int adopterId) throws SQLException {
        String sql = "SELECT * FROM adoption_applications WHERE adopter_id = ? ORDER BY applied_at DESC";
        List<AdoptionApplication> applications = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, adopterId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    applications.add(mapRow(rs));
                }
            }
        }
        return applications;
    }

    /** Applications for pets owned by this shelter, with adopter contact details. */
    public List<ShelterApplicationView> findByShelterId(int shelterId) throws SQLException {
        String sql = "SELECT a.application_id, a.pet_id, p.name AS pet_name, " +
                "a.adopter_id, u.name AS adopter_name, u.email AS adopter_email, " +
                "u.phone AS adopter_phone, a.message, a.status, a.applied_at, a.decided_at " +
                "FROM adoption_applications a " +
                "JOIN pets p ON p.pet_id = a.pet_id " +
                "JOIN users u ON u.user_id = a.adopter_id " +
                "WHERE p.shelter_id = ? ORDER BY a.applied_at DESC";
        List<ShelterApplicationView> applications = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, shelterId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    applications.add(new ShelterApplicationView(
                            rs.getInt("application_id"), rs.getInt("pet_id"), rs.getString("pet_name"),
                            rs.getInt("adopter_id"), rs.getString("adopter_name"),
                            rs.getString("adopter_email"), rs.getString("adopter_phone"),
                            rs.getString("message"), AdoptionApplication.Status.valueOf(rs.getString("status")),
                            rs.getTimestamp("applied_at"), rs.getTimestamp("decided_at")));
                }
            }
        }
        return applications;
    }

    /** Finds one application only when its pet belongs to the supplied shelter. */
    public ShelterApplicationView findByIdAndShelterId(int applicationId, int shelterId) throws SQLException {
        String sql = "SELECT a.application_id, a.pet_id, p.name AS pet_name, " +
                "a.adopter_id, u.name AS adopter_name, u.email AS adopter_email, " +
                "u.phone AS adopter_phone, a.message, a.status, a.applied_at, a.decided_at " +
                "FROM adoption_applications a JOIN pets p ON p.pet_id = a.pet_id " +
                "JOIN users u ON u.user_id = a.adopter_id " +
                "WHERE a.application_id = ? AND p.shelter_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, applicationId);
            stmt.setInt(2, shelterId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;
                return new ShelterApplicationView(rs.getInt("application_id"), rs.getInt("pet_id"),
                        rs.getString("pet_name"), rs.getInt("adopter_id"), rs.getString("adopter_name"),
                        rs.getString("adopter_email"), rs.getString("adopter_phone"), rs.getString("message"),
                        AdoptionApplication.Status.valueOf(rs.getString("status")),
                        rs.getTimestamp("applied_at"), rs.getTimestamp("decided_at"));
            }
        }
    }

    /**
     * Approves only a pending application for an available pet owned by shelterId.
     * The pet row is locked first; every related write commits or rolls back together.
     */
    public boolean approveApplication(int applicationId, int shelterId) throws SQLException {
        String lockSql = "SELECT a.pet_id, a.adopter_id FROM adoption_applications a " +
                "JOIN pets p ON p.pet_id = a.pet_id WHERE a.application_id = ? " +
                "AND p.shelter_id = ? AND a.status = 'PENDING' AND p.status = 'AVAILABLE' FOR UPDATE";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int petId;
                int adopterId;
                try (PreparedStatement lock = conn.prepareStatement(lockSql)) {
                    lock.setInt(1, applicationId);
                    lock.setInt(2, shelterId);
                    try (ResultSet rs = lock.executeQuery()) {
                        if (!rs.next()) { conn.rollback(); return false; }
                        petId = rs.getInt("pet_id");
                        adopterId = rs.getInt("adopter_id");
                    }
                }

                try (PreparedStatement updatePet = conn.prepareStatement(
                        "UPDATE pets SET status = 'ADOPTED' WHERE pet_id = ? AND shelter_id = ? AND status = 'AVAILABLE'")) {
                    updatePet.setInt(1, petId);
                    updatePet.setInt(2, shelterId);
                    if (updatePet.executeUpdate() != 1) { conn.rollback(); return false; }
                }
                try (PreparedStatement updateApplication = conn.prepareStatement(
                        "UPDATE adoption_applications SET status = 'APPROVED', decided_at = CURRENT_TIMESTAMP " +
                                "WHERE application_id = ? AND status = 'PENDING'")) {
                    updateApplication.setInt(1, applicationId);
                    if (updateApplication.executeUpdate() != 1) throw new SQLException("Application decision conflicted.");
                }
                try (PreparedStatement history = conn.prepareStatement(
                        "INSERT INTO adoption_history (pet_id, adopter_id, shelter_id) VALUES (?, ?, ?)")) {
                    history.setInt(1, petId);
                    history.setInt(2, adopterId);
                    history.setInt(3, shelterId);
                    history.executeUpdate();
                }
                try (PreparedStatement rejectOthers = conn.prepareStatement(
                        "UPDATE adoption_applications SET status = 'REJECTED', decided_at = CURRENT_TIMESTAMP " +
                                "WHERE pet_id = ? AND application_id <> ? AND status = 'PENDING'")) {
                    rejectOthers.setInt(1, petId);
                    rejectOthers.setInt(2, applicationId);
                    rejectOthers.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                try { conn.rollback(); } catch (SQLException rollbackError) { e.addSuppressed(rollbackError); }
                throw e;
            } finally {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) { }
            }
        }
    }

    /** Rejects a pending application only if its pet is owned by this shelter. */
    public boolean rejectApplication(int applicationId, int shelterId) throws SQLException {
        String sql = "UPDATE adoption_applications a JOIN pets p ON p.pet_id = a.pet_id " +
                "SET a.status = 'REJECTED', a.decided_at = CURRENT_TIMESTAMP " +
                "WHERE a.application_id = ? AND p.shelter_id = ? AND a.status = 'PENDING'";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, applicationId);
            stmt.setInt(2, shelterId);
            return stmt.executeUpdate() == 1;
        }
    }

    private AdoptionApplication mapRow(ResultSet rs) throws SQLException {
        return new AdoptionApplication(
                rs.getInt("application_id"),
                rs.getInt("pet_id"),
                rs.getInt("adopter_id"),
                AdoptionApplication.Status.valueOf(rs.getString("status")),
                rs.getString("message"),
                rs.getTimestamp("applied_at"),
                rs.getTimestamp("decided_at")
        );
    }
}
