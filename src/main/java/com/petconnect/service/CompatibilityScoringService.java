package com.petconnect.service;

import com.petconnect.model.CompatibilityScore;
import com.petconnect.model.Pet;

/** Deterministic rule-based scoring; no machine learning or persisted profile data. */
public class CompatibilityScoringService {
    private static final int TYPE_WEIGHT = 40;
    private static final int BREED_WEIGHT = 30;
    private static final int LOCATION_WEIGHT = 20;
    private static final int AGE_WEIGHT = 10;

    public CompatibilityScore score(Pet pet, String preferredType, String preferredBreed,
                                    String preferredLocation, Integer maximumAge) {
        boolean typeOn = present(preferredType);
        boolean breedOn = present(preferredBreed);
        boolean locationOn = present(preferredLocation);
        boolean ageOn = maximumAge != null;
        int possible = (typeOn ? TYPE_WEIGHT : 0) + (breedOn ? BREED_WEIGHT : 0)
                + (locationOn ? LOCATION_WEIGHT : 0) + (ageOn ? AGE_WEIGHT : 0);
        int type = typeOn && contains(pet.getType(), preferredType) ? TYPE_WEIGHT : 0;
        int breed = breedOn && contains(pet.getBreed(), preferredBreed) ? BREED_WEIGHT : 0;
        int location = locationOn && contains(pet.getLocation(), preferredLocation) ? LOCATION_WEIGHT : 0;
        int age = ageOn && pet.getAge() <= maximumAge ? AGE_WEIGHT : 0;
        int earned = type + breed + location + age;
        int percent = possible == 0 ? 0 : (int) Math.round(earned * 100.0 / possible);
        return new CompatibilityScore(percent, type, breed, location, age, typeOn, breedOn,
                locationOn, ageOn);
    }

    private boolean present(String value) { return value != null && !value.trim().isEmpty(); }
    private boolean contains(String value, String preference) {
        return value != null && value.toLowerCase().contains(preference.trim().toLowerCase());
    }
}
