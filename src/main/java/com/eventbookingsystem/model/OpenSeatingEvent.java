package com.eventbookingsystem.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class OpenSeatingEvent extends Event {
    private int totalCapacity;
    private int bookedCount;

    public OpenSeatingEvent() {}

    public OpenSeatingEvent(UUID eventId, UUID venueId, String name, LocalDateTime eventDate, LocalDateTime createdAt, int totalCapacity, int bookedCount) {
        super(eventId, venueId, name, eventDate, createdAt);
        this.totalCapacity = totalCapacity;
        this.bookedCount = bookedCount;
    }

    @Override
    public boolean canAccommodateBooking(int seatsRequested) {
        return getAvailableCapacity() >= seatsRequested;
    }

    @Override
    public void reserveSeats(Booking booking) {
        bookedCount += booking.getNumberOfSeats();
        booking.setSeatDetails("open_seating");
    }

    @Override
    public int getAvailableCapacity() {
        return totalCapacity - bookedCount;
    }

    public int getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(int totalCapacity) { this.totalCapacity = totalCapacity; }

    public int getBookedCount() { return bookedCount; }
    public void setBookedCount(int bookedCount) { this.bookedCount = bookedCount; }
}
