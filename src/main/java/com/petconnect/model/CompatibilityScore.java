package com.petconnect.model;

/** Explainable compatibility result for one pet and the adopter's supplied preferences. */
public class CompatibilityScore {
    private final int percentage;
    private final int typePoints;
    private final int breedPoints;
    private final int locationPoints;
    private final int agePoints;
    private final boolean typeConsidered;
    private final boolean breedConsidered;
    private final boolean locationConsidered;
    private final boolean ageConsidered;

    public CompatibilityScore(int percentage, int typePoints, int breedPoints, int locationPoints,
                              int agePoints, boolean typeConsidered, boolean breedConsidered,
                              boolean locationConsidered, boolean ageConsidered) {
        this.percentage = percentage;
        this.typePoints = typePoints;
        this.breedPoints = breedPoints;
        this.locationPoints = locationPoints;
        this.agePoints = agePoints;
        this.typeConsidered = typeConsidered;
        this.breedConsidered = breedConsidered;
        this.locationConsidered = locationConsidered;
        this.ageConsidered = ageConsidered;
    }

    public int getPercentage() { return percentage; }
    public int getTypePoints() { return typePoints; }
    public int getBreedPoints() { return breedPoints; }
    public int getLocationPoints() { return locationPoints; }
    public int getAgePoints() { return agePoints; }
    public boolean isTypeConsidered() { return typeConsidered; }
    public boolean isBreedConsidered() { return breedConsidered; }
    public boolean isLocationConsidered() { return locationConsidered; }
    public boolean isAgeConsidered() { return ageConsidered; }
}
