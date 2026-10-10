package com.petconnect.model;

/**
 * Pairs a pet with the compatibility score used to recommend it to an adopter.
 */
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
