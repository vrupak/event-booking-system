package com.eventbookingsystem.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Booking {
    private UUID bookingId;
    private UUID userId;
    private UUID eventId;
    private int numberOfSeats;
    private String seatDetails;                 // JSON string: example: "[\"A1\",\"A2\"]" or section info
    private PaymentStatus paymentStatus;
    private String paymentId;                   // ID from Payment Gateway
    private LocalDateTime bookingDate;
    private double totalAmount;

    public Booking() {}

    public Booking(UUID bookingId, UUID userId, UUID eventId, int numberOfSeats,
                   String seatDetails, PaymentStatus paymentStatus, String paymentId,
                   LocalDateTime bookingDate, double totalAmount) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.eventId = eventId;
        this.numberOfSeats = numberOfSeats;
        this.seatDetails = seatDetails;
        this.paymentStatus = paymentStatus;
        this.paymentId = paymentId;
        this.bookingDate = bookingDate;
        this.totalAmount = totalAmount;
    }

    public UUID getBookingId() { return bookingId; }
    public void setBookingId(UUID bookingId) { this.bookingId = bookingId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getEventId() { return eventId; }
    public void setEventId(UUID eventId) { this.eventId = eventId; }

    public int getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(int numberOfSeats) { this.numberOfSeats = numberOfSeats; }

    public String getSeatDetails() { return seatDetails; }
    public void setSeatDetails(String seatDetails) { this.seatDetails = seatDetails; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}
