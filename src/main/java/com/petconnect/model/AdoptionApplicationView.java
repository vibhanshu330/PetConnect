package com.petconnect.model;

import java.sql.Timestamp;

/**
 * NOT a database row - a read-only DTO (same pattern as PlatformStats)
 * combining one AdoptionApplication with the Pet details "My Applications"
 * needs to display (pet name, type, breed). Built by MyApplicationsServlet
 * from AdoptionApplicationDAO.findByAdopterId() plus the EXISTING
 * PetDAO.findById() - reusing existing code rather than writing a JOIN
 * query. Fine at this scale (one adopter's own application list is
 * always small); a JOIN would be the natural next optimization for a
 * larger-scale version.
 */
public class AdoptionApplicationView {

    private final int applicationId;
    private final String petName;
    private final String petType;
    private final String petBreed;
    private final String message;
    private final Timestamp appliedAt;
    private final AdoptionApplication.Status status;
    private final Timestamp decidedAt;

    public AdoptionApplicationView(int applicationId, String petName, String petType, String petBreed,
                                    String message, Timestamp appliedAt,
                                    AdoptionApplication.Status status, Timestamp decidedAt) {
        this.applicationId = applicationId;
        this.petName = petName;
        this.petType = petType;
        this.petBreed = petBreed;
        this.message = message;
        this.appliedAt = appliedAt;
        this.status = status;
        this.decidedAt = decidedAt;
    }

    public int getApplicationId() { return applicationId; }
    public String getPetName() { return petName; }
    public String getPetType() { return petType; }
    public String getPetBreed() { return petBreed; }
    public String getMessage() { return message; }
    public Timestamp getAppliedAt() { return appliedAt; }
    public AdoptionApplication.Status getStatus() { return status; }
    public Timestamp getDecidedAt() { return decidedAt; }
}
