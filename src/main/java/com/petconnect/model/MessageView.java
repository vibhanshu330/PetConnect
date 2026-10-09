package com.petconnect.model;

import java.sql.Timestamp;

/** Read-only message row enriched for the conversation screen. */
public class MessageView {
    private final int senderId, receiverId, petId;
    private final String senderName, body, petName;
    private final Timestamp sentAt;

    public MessageView(int senderId, int receiverId, int petId, String senderName,
                       String body, String petName, Timestamp sentAt) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.petId = petId;
        this.senderName = senderName;
        this.body = body;
        this.petName = petName;
        this.sentAt = sentAt;
    }
    public int getSenderId() { return senderId; }
    public int getReceiverId() { return receiverId; }
    public int getPetId() { return petId; }
    public String getSenderName() { return senderName; }
    public String getBody() { return body; }
    public String getPetName() { return petName; }
    public Timestamp getSentAt() { return sentAt; }
}
