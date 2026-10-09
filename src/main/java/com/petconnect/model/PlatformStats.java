package com.petconnect.model;

/**
 * NOT a database row - this is a Data Transfer Object (DTO) that carries
 * pre-aggregated platform statistics from StatisticsDAO to the admin
 * dashboard view. It exists purely to bundle several related numbers
 * into one object, so AdminDashboardServlet can set a single request
 * attribute instead of eight separate ones.
 */
public class PlatformStats {

    private final int totalUsers;
    private final int totalShelters;
    private final int totalAdopters;
    private final int totalPets;
    private final int pendingPets;
    private final int availablePets;
    private final int adoptedPets;
    private final int totalApplications;

    public PlatformStats(int totalUsers, int totalShelters, int totalAdopters,
                          int totalPets, int pendingPets, int availablePets,
                          int adoptedPets, int totalApplications) {
        this.totalUsers = totalUsers;
        this.totalShelters = totalShelters;
        this.totalAdopters = totalAdopters;
        this.totalPets = totalPets;
        this.pendingPets = pendingPets;
        this.availablePets = availablePets;
        this.adoptedPets = adoptedPets;
        this.totalApplications = totalApplications;
    }

    public int getTotalUsers() { return totalUsers; }
    public int getTotalShelters() { return totalShelters; }
    public int getTotalAdopters() { return totalAdopters; }
    public int getTotalPets() { return totalPets; }
    public int getPendingPets() { return pendingPets; }
    public int getAvailablePets() { return availablePets; }
    public int getAdoptedPets() { return adoptedPets; }
    public int getTotalApplications() { return totalApplications; }
}
