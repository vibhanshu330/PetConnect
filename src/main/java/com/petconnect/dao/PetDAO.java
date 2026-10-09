package com.petconnect.dao;

import com.petconnect.model.Pet;
import com.petconnect.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the "pets" table.
 */
public class PetDAO {

    public int createPet(Pet pet) throws SQLException {
        String sql = "INSERT INTO pets (shelter_id, name, type, breed, age, gender, " +
                     "location, description, image_path, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, pet.getShelterId());
            stmt.setString(2, pet.getName());
            stmt.setString(3, pet.getType());
            stmt.setString(4, pet.getBreed());
            stmt.setInt(5, pet.getAge());
            stmt.setString(6, pet.getGender().name());
            stmt.setString(7, pet.getLocation());
            stmt.setString(8, pet.getDescription());
            stmt.setString(9, pet.getImagePath());
            stmt.setString(10, pet.getStatus().name());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Creating pet failed, no rows affected.");
            }

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                throw new SQLException("Creating pet failed, no ID obtained.");
            }
        }
    }

    public Pet findById(int petId) throws SQLException {
        String sql = "SELECT * FROM pets WHERE pet_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, petId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /** All pets belonging to one shelter - used by "My Pets" page. */
    public List<Pet> findByShelterId(int shelterId) throws SQLException {
        String sql = "SELECT * FROM pets WHERE shelter_id = ? ORDER BY created_at DESC";
        List<Pet> pets = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, shelterId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pets.add(mapRow(rs));
                }
            }
        }
        return pets;
    }

    /** All pets with a given status - used by the admin "pending review" page. */
    public List<Pet> findByStatus(Pet.Status status) throws SQLException {
        String sql = "SELECT * FROM pets WHERE status = ? ORDER BY created_at ASC";
        List<Pet> pets = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pets.add(mapRow(rs));
                }
            }
        }
        return pets;
    }

    /**
     * Full update of a pet's editable details. Deliberately does NOT touch
     * "status" - status changes go through updateStatus() only.
     */
    public boolean updatePet(Pet pet) throws SQLException {
        String sql = "UPDATE pets SET name = ?, type = ?, breed = ?, age = ?, gender = ?, " +
                     "location = ?, description = ?, image_path = ? WHERE pet_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pet.getName());
            stmt.setString(2, pet.getType());
            stmt.setString(3, pet.getBreed());
            stmt.setInt(4, pet.getAge());
            stmt.setString(5, pet.getGender().name());
            stmt.setString(6, pet.getLocation());
            stmt.setString(7, pet.getDescription());
            stmt.setString(8, pet.getImagePath());
            stmt.setInt(9, pet.getPetId());

            return stmt.executeUpdate() > 0;
        }
    }

    /** Changes ONLY the status column - used for admin approve/reject. */
    public boolean updateStatus(int petId, Pet.Status newStatus) throws SQLException {
        String sql = "UPDATE pets SET status = ? WHERE pet_id = ? AND status = 'PENDING'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus.name());
            stmt.setInt(2, petId);

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deletePet(int petId) throws SQLException {
        String sql = "DELETE FROM pets WHERE pet_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, petId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * All pets on the platform, regardless of shelter or status - used
     * by the admin "Pet Management" page. Phase 6 addition; every
     * existing method above is unchanged.
     */
    public List<Pet> findAll() throws SQLException {
        String sql = "SELECT * FROM pets ORDER BY created_at DESC";
        List<Pet> pets = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                pets.add(mapRow(rs));
            }
        }
        return pets;
    }

    /**
     * Searches AVAILABLE pets only, for the adopter-facing browse page.
     * All four parameters are optional (null/blank = "don't filter on
     * this") and combine with AND - Phase 7 requirement: "Type=Dog AND
     * Breed=Beagle AND Location=Delhi" should narrow the results, not
     * just match any one of them.
     *
     * IMPORTANT ON SAFETY: the SQL text itself is built dynamically
     * (which clauses get appended depends on which filters were
     * supplied), but every VALUE a user typed always goes through a "?"
     * placeholder bound via PreparedStatement, never concatenated
     * directly into the query string. Only the STRUCTURE of the query
     * is dynamic; the DATA never is - that distinction is what keeps
     * this safe from SQL injection despite being a "dynamic" query.
     */
    public List<Pet> searchAvailablePets(String keyword, String type, String breed, String location)
            throws SQLException {

        StringBuilder sql = new StringBuilder("SELECT * FROM pets WHERE status = 'AVAILABLE'");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            // One search box matching across several columns at once -
            // an OR group, itself AND-ed with everything else.
            sql.append(" AND (name LIKE ? OR type LIKE ? OR breed LIKE ? OR location LIKE ?)");
            String likeKeyword = "%" + keyword.trim() + "%";
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);
        }
        if (type != null && !type.trim().isEmpty()) {
            sql.append(" AND type LIKE ?");
            params.add("%" + type.trim() + "%");
        }
        if (breed != null && !breed.trim().isEmpty()) {
            sql.append(" AND breed LIKE ?");
            params.add("%" + breed.trim() + "%");
        }
        if (location != null && !location.trim().isEmpty()) {
            sql.append(" AND location LIKE ?");
            params.add("%" + location.trim() + "%");
        }
        sql.append(" ORDER BY created_at DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                List<Pet> pets = new ArrayList<>();
                while (rs.next()) {
                    pets.add(mapRow(rs));
                }
                return pets;
            }
        }
    }

    private Pet mapRow(ResultSet rs) throws SQLException {
        return new Pet(
                rs.getInt("pet_id"),
                rs.getInt("shelter_id"),
                rs.getString("name"),
                rs.getString("type"),
                rs.getString("breed"),
                rs.getInt("age"),
                Pet.Gender.valueOf(rs.getString("gender")),
                rs.getString("location"),
                rs.getString("description"),
                rs.getString("image_path"),
                Pet.Status.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at")
        );
    }
}
