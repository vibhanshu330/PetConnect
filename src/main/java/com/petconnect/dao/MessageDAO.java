package com.petconnect.dao;

import com.petconnect.model.MessageView;
import com.petconnect.util.DBConnection;

import java.sql.*;
import java.util.*;

/** JDBC access for the existing messages table. */
public class MessageDAO {
    public List<MessageView> findMessages(int userId) throws SQLException {
        String sql = "SELECT m.sender_id,m.receiver_id,m.pet_id,m.body,m.sent_at,u.name AS sender_name,p.name AS pet_name " +
                "FROM messages m JOIN users u ON u.user_id=m.sender_id LEFT JOIN pets p ON p.pet_id=m.pet_id " +
                "WHERE m.sender_id=? OR m.receiver_id=? ORDER BY m.sent_at,m.message_id";
        List<MessageView> rows = new ArrayList<>();
        try (Connection c=DBConnection.getConnection(); PreparedStatement s=c.prepareStatement(sql)) {
            s.setInt(1,userId); s.setInt(2,userId);
            try (ResultSet r=s.executeQuery()) { while(r.next()) rows.add(map(r)); }
        }
        return rows;
    }

    public List<MessageView> findConversation(int userId, int peerId, int petId) throws SQLException {
        String sql = "SELECT m.sender_id,m.receiver_id,m.pet_id,m.body,m.sent_at,u.name AS sender_name,p.name AS pet_name " +
                "FROM messages m JOIN users u ON u.user_id=m.sender_id LEFT JOIN pets p ON p.pet_id=m.pet_id " +
                "WHERE m.pet_id=? AND ((m.sender_id=? AND m.receiver_id=?) OR (m.sender_id=? AND m.receiver_id=?)) " +
                "ORDER BY m.sent_at,m.message_id";
        List<MessageView> rows = new ArrayList<>();
        try (Connection c=DBConnection.getConnection(); PreparedStatement s=c.prepareStatement(sql)) {
            s.setInt(1,petId); s.setInt(2,userId); s.setInt(3,peerId); s.setInt(4,peerId); s.setInt(5,userId);
            try (ResultSet r=s.executeQuery()) { while(r.next()) rows.add(map(r)); }
        }
        return rows;
    }

    /** Confirms that a conversation participant is allowed to contact this pet's shelter. */
    public boolean canStartAdopterConversation(int adopterId, int petId) throws SQLException {
        String sql = "SELECT p.shelter_id FROM pets p WHERE p.pet_id=? AND EXISTS " +
                "(SELECT 1 FROM users u WHERE u.user_id=p.shelter_id AND u.role='SHELTER') AND " +
                "(p.status='AVAILABLE' OR EXISTS (SELECT 1 FROM adoption_applications a WHERE a.pet_id=p.pet_id AND a.adopter_id=?))";
        try (Connection c=DBConnection.getConnection(); PreparedStatement s=c.prepareStatement(sql)) {
            s.setInt(1,petId); s.setInt(2,adopterId);
            try(ResultSet r=s.executeQuery()) { return r.next(); }
        }
    }

    public int shelterForPet(int petId) throws SQLException {
        String sql="SELECT p.shelter_id FROM pets p JOIN users u ON u.user_id=p.shelter_id AND u.role='SHELTER' WHERE p.pet_id=?";
        try(Connection c=DBConnection.getConnection(); PreparedStatement s=c.prepareStatement(sql)) {
            s.setInt(1,petId); try(ResultSet r=s.executeQuery()) { return r.next()?r.getInt(1):-1; }
        }
    }

    public boolean isAdopter(int userId) throws SQLException {
        try(Connection c=DBConnection.getConnection(); PreparedStatement s=c.prepareStatement("SELECT 1 FROM users WHERE user_id=? AND role='ADOPTER'")) {
            s.setInt(1,userId); try(ResultSet r=s.executeQuery()) { return r.next(); }
        }
    }

    public boolean hasConversation(int firstId, int secondId, int petId) throws SQLException {
        String sql="SELECT 1 FROM messages WHERE pet_id=? AND ((sender_id=? AND receiver_id=?) OR (sender_id=? AND receiver_id=?)) LIMIT 1";
        try(Connection c=DBConnection.getConnection(); PreparedStatement s=c.prepareStatement(sql)) {
            s.setInt(1,petId); s.setInt(2,firstId); s.setInt(3,secondId); s.setInt(4,secondId); s.setInt(5,firstId);
            try(ResultSet r=s.executeQuery()) { return r.next(); }
        }
    }

    public boolean hasApplication(int adopterId, int petId) throws SQLException {
        String sql="SELECT 1 FROM adoption_applications WHERE adopter_id=? AND pet_id=? LIMIT 1";
        try(Connection c=DBConnection.getConnection(); PreparedStatement s=c.prepareStatement(sql)) {
            s.setInt(1,adopterId); s.setInt(2,petId); try(ResultSet r=s.executeQuery()) { return r.next(); }
        }
    }

    public void send(int senderId, int receiverId, int petId, String body) throws SQLException {
        String sql="INSERT INTO messages(sender_id,receiver_id,pet_id,body) VALUES(?,?,?,?)";
        try(Connection c=DBConnection.getConnection(); PreparedStatement s=c.prepareStatement(sql)) {
            s.setInt(1,senderId); s.setInt(2,receiverId); s.setInt(3,petId); s.setString(4,body); s.executeUpdate();
        }
    }

    private MessageView map(ResultSet r) throws SQLException {
        int petId=r.getInt("pet_id"); if(r.wasNull()) petId=0;
        return new MessageView(r.getInt("sender_id"),r.getInt("receiver_id"),petId,
                r.getString("sender_name"),r.getString("body"),r.getString("pet_name"),r.getTimestamp("sent_at"));
    }
}
