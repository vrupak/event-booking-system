package com.eventbookingsystem;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.eventbookingsystem.exception.BookingException;
import com.eventbookingsystem.model.Booking;
import com.eventbookingsystem.model.FullySeatedEvent;
import com.eventbookingsystem.model.Seat;
import com.eventbookingsystem.model.User;
import com.eventbookingsystem.model.Venue;
import com.eventbookingsystem.payment.MockPaymentGateway;
import com.eventbookingsystem.payment.PaymentGateway;
import com.eventbookingsystem.repository.BookingRepository;
import com.eventbookingsystem.repository.EventRepository;
import com.eventbookingsystem.repository.InMemoryBookingRepository;
import com.eventbookingsystem.repository.InMemoryEventRepository;
import com.eventbookingsystem.repository.InMemoryUserRepository;
import com.eventbookingsystem.repository.InMemoryVenueRepository;
import com.eventbookingsystem.repository.UserRepository;
import com.eventbookingsystem.repository.VenueRepository;
import com.eventbookingsystem.service.BookingService;

public class Main {
    public static void main(String[] args) {
        // ==== 1. Set up repositories and payment gateway ====
        UserRepository userRepository = new InMemoryUserRepository();
        EventRepository eventRepository = new InMemoryEventRepository();
        VenueRepository venueRepository = new InMemoryVenueRepository();
        BookingRepository bookingRepository = new InMemoryBookingRepository();
        PaymentGateway paymentGateway = new MockPaymentGateway();

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

        System.out.println("=== Repository sanity check ===");
        System.out.println("Users: " + userRepository.findAll().size());
        System.out.println("Events: " + eventRepository.findAll().size());

        // ==== 4. Wire BookingService ====
        BookingService bookingService = new BookingService(
                bookingRepository,
                userRepository,
                eventRepository,
                venueRepository,
                paymentGateway
        );

        // ==== 5. Call createBooking ====
        try {
            System.out.println("\n=== BookingService test (happy path) ===");
            Booking booking = bookingService.createBooking(
                    user.getUserId(),
                    event.getEventId(),
                    2,
                    "1234567812345678" // valid mock card
            );

            System.out.println("Booking created with ID: " + booking.getBookingId());
            System.out.println("Payment status: " + booking.getPaymentStatus());
            System.out.println("Payment ID: " + booking.getPaymentId());
            System.out.println("Seat details: " + booking.getSeatDetails());
            System.out.println("Total amount: " + booking.getTotalAmount());
            System.out.println("Bookings for user: "
                    + bookingRepository.findByUserId(user.getUserId()).size());
            System.out.println("Available capacity after booking: "
                    + eventRepository.findById(event.getEventId()).orElseThrow().getAvailableCapacity());

        } catch (BookingException e) {
            System.out.println("Booking failed: " + e.getMessage());
        }

        // ==== 6. Try a failing booking (capacity exceeded) ====
        try {
            System.out.println("\n=== BookingService test (capacity exceeded) ===");
            bookingService.createBooking(
                    user.getUserId(),
                    event.getEventId(),
                    10,                     // more than remaining seats
                    "1234567812345678"
            );
        } catch (BookingException e) {
            System.out.println("Expected failure: " + e.getClass().getSimpleName()
                    + " - " + e.getMessage());
        }
    }
}
