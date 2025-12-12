package com.eventbookingsystem.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Venue {
    private UUID venueId;
    private String name;
    private String location;
    private int totalCapacity;
    private LocalDateTime createdAt;

    public Venue() {}

    public Venue(UUID venueId, String name, String location, int totalCapacity, LocalDateTime createdAt) {
        this.venueId = venueId;
        this.name = name;
        this.location = location;
        this.totalCapacity = totalCapacity;
        this.createdAt = createdAt;
    }

    public UUID getVenueId() { return venueId; }
    public void setVenueId(UUID venueID) { this.venueId = venueID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getCapacity() { return totalCapacity; }
    public void setCapacity(int totalCapacity) { this.totalCapacity = totalCapacity; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
