package com.petconnect.dao;

import com.petconnect.model.AdoptionHistoryView;
import com.petconnect.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Read-only queries for completed adoption history. */
public class AdoptionHistoryDAO {
    public List<AdoptionHistoryView> findByAdopterId(int adopterId) throws SQLException {
        String sql = "SELECT h.pet_id, p.name AS pet_name, p.type AS pet_type, " +
                "p.breed AS pet_breed, s.name AS shelter_name, h.adopted_at " +
                "FROM adoption_history h " +
                "JOIN pets p ON p.pet_id = h.pet_id " +
                "JOIN users s ON s.user_id = h.shelter_id " +
                "WHERE h.adopter_id = ? ORDER BY h.adopted_at DESC";
        List<AdoptionHistoryView> history = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, adopterId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    history.add(new AdoptionHistoryView(rs.getInt("pet_id"), rs.getString("pet_name"),
                            rs.getString("pet_type"), rs.getString("pet_breed"),
                            rs.getString("shelter_name"), rs.getTimestamp("adopted_at")));
                }
            }
        }
        return history;
    }
}
