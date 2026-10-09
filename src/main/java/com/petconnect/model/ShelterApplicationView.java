package com.petconnect.model;

import java.sql.Timestamp;

/** Read-only application details shown to the shelter that owns the pet. */
public class ShelterApplicationView {
    private final int applicationId;
    private final int petId;
    private final String petName;
    private final int adopterId;
    private final String adopterName;
    private final String adopterEmail;
    private final String adopterPhone;
    private final String message;
    private final AdoptionApplication.Status status;
    private final Timestamp appliedAt;
    private final Timestamp decidedAt;

    public ShelterApplicationView(int applicationId, int petId, String petName,
                                  int adopterId, String adopterName, String adopterEmail,
                                  String adopterPhone, String message,
                                  AdoptionApplication.Status status,
                                  Timestamp appliedAt, Timestamp decidedAt) {
        this.applicationId = applicationId;
        this.petId = petId;
        this.petName = petName;
        this.adopterId = adopterId;
        this.adopterName = adopterName;
        this.adopterEmail = adopterEmail;
        this.adopterPhone = adopterPhone;
        this.message = message;
        this.status = status;
        this.appliedAt = appliedAt;
        this.decidedAt = decidedAt;
    }

    public int getApplicationId() { return applicationId; }
    public int getPetId() { return petId; }
    public String getPetName() { return petName; }
    public int getAdopterId() { return adopterId; }
    public String getAdopterName() { return adopterName; }
    public String getAdopterEmail() { return adopterEmail; }
    public String getAdopterPhone() { return adopterPhone; }
    public String getMessage() { return message; }
    public AdoptionApplication.Status getStatus() { return status; }
    public Timestamp getAppliedAt() { return appliedAt; }
    public Timestamp getDecidedAt() { return decidedAt; }
}
