package com.eventbookingsystem.model;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Event {
    private UUID eventId;
    private UUID venueId;
    private String name;
    private LocalDateTime eventDate;
    private LocalDateTime createdAt;

    public Event() {}

    public Event(UUID eventId, UUID venueId, String name, LocalDateTime eventDate, LocalDateTime createdAt) {
        this.eventId = eventId;
        this.venueId = venueId;
        this.name = name;
        this.eventDate = eventDate;
        this.createdAt = createdAt;
    }

    public UUID getEventId() { return eventId; }
    public void setEventId(UUID eventId) { this.eventId = eventId; }

    public UUID getVenueId() { return venueId; }
    public void setVenueId(UUID venueId) { this.venueId = venueId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Abstract methods that each event type must implement
    public abstract boolean canAccommodateBooking(int seats);
    public abstract void reserveSeats(Booking booking);
    public abstract int getAvailableCapacity();
}
