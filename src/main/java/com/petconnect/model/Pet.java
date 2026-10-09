package com.petconnect.model;

import java.sql.Timestamp;

/**
 * Represents a single row in the "pets" table.
 */
public class Pet {

    public enum Gender { MALE, FEMALE }

    public enum Status { PENDING, AVAILABLE, REJECTED, ADOPTED }

    private int petId;
    private int shelterId;
    private String name;
    private String type;
    private String breed;
    private int age;
    private Gender gender;
    private String location;
    private String description;
    private String imagePath;
    private Status status;
    private Timestamp createdAt;

    // Constructor #1: for a NEW pet, before it exists in the database.
    // Status is always forced to PENDING here, matching the column's
    // DEFAULT 'PENDING' in MySQL.
    public Pet(int shelterId, String name, String type, String breed, int age,
               Gender gender, String location, String description, String imagePath) {
        this.shelterId = shelterId;
        this.name = name;
        this.type = type;
        this.breed = breed;
        this.age = age;
        this.gender = gender;
        this.location = location;
        this.description = description;
        this.imagePath = imagePath;
        this.status = Status.PENDING;
    }

    // Constructor #2: for rebuilding a Pet FROM a database row.
    public Pet(int petId, int shelterId, String name, String type, String breed, int age,
               Gender gender, String location, String description, String imagePath,
               Status status, Timestamp createdAt) {
        this.petId = petId;
        this.shelterId = shelterId;
        this.name = name;
        this.type = type;
        this.breed = breed;
        this.age = age;
        this.gender = gender;
        this.location = location;
        this.description = description;
        this.imagePath = imagePath;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }

    public int getShelterId() { return shelterId; }
    public void setShelterId(int shelterId) { this.shelterId = shelterId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Pet{petId=" + petId + ", shelterId=" + shelterId + ", name='" + name +
                "', type='" + type + "', status=" + status + '}';
    }
}
