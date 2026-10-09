package com.petconnect.model;

public class PetRecommendation {
    private final Pet pet;
    private final CompatibilityScore compatibility;

    public PetRecommendation(Pet pet, CompatibilityScore compatibility) {
        this.pet = pet;
        this.compatibility = compatibility;
    }

    public Pet getPet() { return pet; }
    public CompatibilityScore getCompatibility() { return compatibility; }
}
