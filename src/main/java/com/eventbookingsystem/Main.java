package com.eventbookingsystem;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.eventbookingsystem.model.Booking;
import com.eventbookingsystem.model.FullySeatedEvent;
import com.eventbookingsystem.model.Seat;
import com.eventbookingsystem.payment.MockPaymentGateway;
import com.eventbookingsystem.payment.PaymentGateway;
import com.eventbookingsystem.payment.PaymentResult;

public class Main {
    public static void main(String[] args) {
        // ---- 1. Set up a fully seated event with 3 seats ----
        Map<String, Seat> seats = new HashMap<>();
        seats.put("A1", new Seat("A1", false, null));
        seats.put("A2", new Seat("A2", false, null));
        seats.put("A3", new Seat("A3", false, null));

        FullySeatedEvent event = new FullySeatedEvent();
        event.setEventId(UUID.randomUUID());
        event.setVenueId(UUID.randomUUID());
        event.setName("Test Concert");
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setCreatedAt(LocalDateTime.now());
        event.setSeats(seats);

        // ---- 2. Create a booking request for 2 seats ----
        Booking booking = new Booking();
        booking.setBookingId(UUID.randomUUID());
        booking.setUserId(UUID.randomUUID());
        booking.setEventId(event.getEventId());
        booking.setNumberOfSeats(2);
        booking.setBookingDate(LocalDateTime.now());
        booking.setTotalAmount(100.0);

        System.out.println("=== Domain model test ===");
        System.out.println("Available before: " + event.getAvailableCapacity());
        System.out.println("Can accommodate 2? " + event.canAccommodateBooking(2));

        event.reserveSeats(booking);

        System.out.println("Available after: " + event.getAvailableCapacity());
        System.out.println("Seat details: " + booking.getSeatDetails());

        // ---- 3. Test the payment gateway with the booking ----
        PaymentGateway paymentGateway = new MockPaymentGateway();

        String creditCardNumber = "1234567812345678"; // 16 digits
        PaymentResult result = paymentGateway.processPayment(creditCardNumber, booking.getTotalAmount());

        System.out.println("\n=== Payment test ===");
        System.out.println("Payment success? " + result.isSuccess());
        System.out.println("Payment ID: " + result.getPaymentId());
        System.out.println("Failure reason: " + result.getFailureReason());

        // ---- 4. Try a failing payment too (invalid card) ----
        PaymentResult badResult = paymentGateway.processPayment("123", booking.getTotalAmount());

        System.out.println("\n=== Payment failure test ===");
        System.out.println("Payment success? " + badResult.isSuccess());
        System.out.println("Payment ID: " + badResult.getPaymentId());
        System.out.println("Failure reason: " + badResult.getFailureReason());
    }
}
