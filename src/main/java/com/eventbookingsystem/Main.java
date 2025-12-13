package com.eventbookingsystem;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.eventbookingsystem.model.Booking;
import com.eventbookingsystem.model.Event;
import com.eventbookingsystem.model.FullySeatedEvent;
import com.eventbookingsystem.model.Seat;
import com.eventbookingsystem.model.User;
import com.eventbookingsystem.model.Venue;
import com.eventbookingsystem.payment.MockPaymentGateway;
import com.eventbookingsystem.payment.PaymentGateway;
import com.eventbookingsystem.payment.PaymentResult;
import com.eventbookingsystem.repository.BookingRepository;
import com.eventbookingsystem.repository.EventRepository;
import com.eventbookingsystem.repository.InMemoryBookingRepository;
import com.eventbookingsystem.repository.InMemoryEventRepository;
import com.eventbookingsystem.repository.InMemoryUserRepository;
import com.eventbookingsystem.repository.InMemoryVenueRepository;
import com.eventbookingsystem.repository.UserRepository;
import com.eventbookingsystem.repository.VenueRepository;

public class Main {
    public static void main(String[] args) {
        // ==== 1. Set up repositories ====
        UserRepository userRepository = new InMemoryUserRepository();
        EventRepository eventRepository = new InMemoryEventRepository();
        VenueRepository venueRepository = new InMemoryVenueRepository();
        BookingRepository bookingRepository = new InMemoryBookingRepository();

        // ==== 2. Create and save a user and venue ====
        User user = new User(
                UUID.randomUUID(),
                "Test User",
                "test@example.com",
                LocalDateTime.now()
        );
        userRepository.save(user);

        Venue venue = new Venue(
                UUID.randomUUID(),
                "Test Venue",
                "Test City",
                100,
                LocalDateTime.now()
        );
        venueRepository.save(venue);

        // ==== 3. Create and save a fully seated event with 3 seats ====
        Map<String, Seat> seats = new HashMap<>();
        seats.put("A1", new Seat("A1", false, null));
        seats.put("A2", new Seat("A2", false, null));
        seats.put("A3", new Seat("A3", false, null));

        FullySeatedEvent event = new FullySeatedEvent();
        event.setEventId(UUID.randomUUID());
        event.setVenueId(venue.getVenueId());
        event.setName("Test Concert");
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setCreatedAt(LocalDateTime.now());
        event.setSeats(seats);

        eventRepository.save(event);

        // Fetch back from repo to confirm wiring
        User loadedUser = userRepository.findById(user.getUserId()).orElseThrow();
        Event loadedEvent = eventRepository.findById(event.getEventId()).orElseThrow();

        System.out.println("=== Repository test ===");
        System.out.println("Loaded user: " + loadedUser.getName() + ", " + loadedUser.getEmail());
        System.out.println("Loaded event: " + loadedEvent.getName());

        // ==== 4. Create a booking request for 2 seats ====
        Booking booking = new Booking();
        booking.setBookingId(UUID.randomUUID());
        booking.setUserId(user.getUserId());
        booking.setEventId(event.getEventId());
        booking.setNumberOfSeats(2);
        booking.setBookingDate(LocalDateTime.now());
        booking.setTotalAmount(100.0);

        System.out.println("\n=== Domain model test ===");
        System.out.println("Available before: " + event.getAvailableCapacity());
        System.out.println("Can accommodate 2? " + event.canAccommodateBooking(2));

        event.reserveSeats(booking);

        System.out.println("Available after: " + event.getAvailableCapacity());
        System.out.println("Seat details: " + booking.getSeatDetails());

        // Save booking to repository
        bookingRepository.save(booking);
        System.out.println("Total bookings for user: "
                + bookingRepository.findByUserId(user.getUserId()).size());

        // ==== 5. Test the payment gateway with the booking ====
        PaymentGateway paymentGateway = new MockPaymentGateway();

        String creditCardNumber = "1234567812345678"; // 16 digits
        PaymentResult result = paymentGateway.processPayment(creditCardNumber, booking.getTotalAmount());

        System.out.println("\n=== Payment test ===");
        System.out.println("Payment success? " + result.isSuccess());
        System.out.println("Payment ID: " + result.getPaymentId());
        System.out.println("Failure reason: " + result.getFailureReason());

        // ==== 6. Try a failing payment too (invalid card) ====
        PaymentResult badResult = paymentGateway.processPayment("123", booking.getTotalAmount());

        System.out.println("\n=== Payment failure test ===");
        System.out.println("Payment success? " + badResult.isSuccess());
        System.out.println("Payment ID: " + badResult.getPaymentId());
        System.out.println("Failure reason: " + badResult.getFailureReason());
    }
}
