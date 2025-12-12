package com.eventbookingsystem.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FullySeatedEvent extends Event {
    private Map<String, Seat> seats;  // seatNumber -> Seat

    public FullySeatedEvent () {
        this.seats = new HashMap<>();
    }

    public FullySeatedEvent (UUID eventId, UUID venueId, String name, LocalDateTime eventDate, LocalDateTime createdAt, Map<String, Seat> seats) {
        super(eventId, venueId, name, eventDate, createdAt);
        this.seats = seats != null ? seats : new HashMap<>();
    }

    @Override
    public boolean canAccommodateBooking(int seatsRequested) {
        return getAvailableCapacity() >= seatsRequested;
    }

    @Override
    public void reserveSeats(Booking booking) {
        int seatsToReserve = booking.getNumberOfSeats();
        int reserved = 0;
        StringBuilder seatList = new StringBuilder();

        for (Map.Entry<String, Seat> entry : seats.entrySet()) {
            Seat seat = entry.getValue();
            if (!seat.isReserved()) {
                seat.setReserved(true);
                seat.setBookingId(booking.getBookingId());
                reserved++;
                if (reserved > 1) seatList.append(",");
                seatList.append(entry.getKey());
                if (reserved == seatsToReserve) break;
            }
        }

        booking.setSeatDetails("[" + seatList.toString() + "]");
    }

    @Override
    public int getAvailableCapacity() {
        return (int) seats.values().stream().filter(s -> !s.isReserved()).count();
    }

    // Getters and setters
    public Map<String, Seat> getSeats() { return seats; }
    public void setSeats(Map<String, Seat> seats) { this.seats = seats; }
}
