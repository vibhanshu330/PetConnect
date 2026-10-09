package com.petconnect.model;

import java.sql.Timestamp;

/** Read-only adopter-facing view of a completed adoption. */
public class AdoptionHistoryView {
    private final int petId;
    private final String petName;
    private final String petType;
    private final String petBreed;
    private final String shelterName;
    private final Timestamp adoptedAt;

    public AdoptionHistoryView(int petId, String petName, String petType, String petBreed,
                               String shelterName, Timestamp adoptedAt) {
        this.petId = petId;
        this.petName = petName;
        this.petType = petType;
        this.petBreed = petBreed;
        this.shelterName = shelterName;
        this.adoptedAt = adoptedAt;
    }

    public int getPetId() { return petId; }
    public String getPetName() { return petName; }
    public String getPetType() { return petType; }
    public String getPetBreed() { return petBreed; }
    public String getShelterName() { return shelterName; }
    public Timestamp getAdoptedAt() { return adoptedAt; }
}
