package com.eventbookingsystem.model;

import java.util.UUID;

public class Seat {
    private String seatNumber;
    private boolean isReserved;
    private UUID bookingId; // nullable

    public Seat() {}

    public Seat(String seatNumber, boolean isReserved, UUID bookingId) {
        this.seatNumber = seatNumber;
        this.isReserved = isReserved;
        this.bookingId = bookingId;
    }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public boolean isReserved() { return isReserved; }
    public void setReserved(boolean reserved) { isReserved = reserved; }

    public UUID getBookingId() { return bookingId; }
    public void setBookingId(UUID bookingId) { this.bookingId = bookingId; }
}
