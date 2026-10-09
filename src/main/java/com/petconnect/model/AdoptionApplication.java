package com.petconnect.model;

import java.sql.Timestamp;

/**
 * Represents a single row in the "adoption_applications" table.
 */
public class AdoptionApplication {

    public enum Status { PENDING, APPROVED, REJECTED }

    private int applicationId;
    private int petId;
    private int adopterId;
    private Status status;
    private String message;
    private Timestamp appliedAt;
    private Timestamp decidedAt;

    // Constructor #1: for a NEW application, before it exists in the
    // database. Status is always forced to PENDING here, matching the
    // column's DEFAULT 'PENDING' in MySQL - the same pattern already
    // used by Pet's "new" constructor.
    public AdoptionApplication(int petId, int adopterId, String message) {
        this.petId = petId;
        this.adopterId = adopterId;
        this.message = message;
        this.status = Status.PENDING;
    }

    // Constructor #2: for rebuilding an AdoptionApplication FROM a
    // database row.
    public AdoptionApplication(int applicationId, int petId, int adopterId, Status status,
                                String message, Timestamp appliedAt, Timestamp decidedAt) {
        this.applicationId = applicationId;
        this.petId = petId;
        this.adopterId = adopterId;
        this.status = status;
        this.message = message;
        this.appliedAt = appliedAt;
        this.decidedAt = decidedAt;
    }

    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }

    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }

    public int getAdopterId() { return adopterId; }
    public void setAdopterId(int adopterId) { this.adopterId = adopterId; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getAppliedAt() { return appliedAt; }
    public void setAppliedAt(Timestamp appliedAt) { this.appliedAt = appliedAt; }

    public Timestamp getDecidedAt() { return decidedAt; }
    public void setDecidedAt(Timestamp decidedAt) { this.decidedAt = decidedAt; }

    @Override
    public String toString() {
        return "AdoptionApplication{applicationId=" + applicationId + ", petId=" + petId +
                ", adopterId=" + adopterId + ", status=" + status + '}';
    }
}
