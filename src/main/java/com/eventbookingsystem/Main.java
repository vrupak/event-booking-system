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
        // 1. Wire repositories with proper dependencies (no circular deps)
        EventRepository eventRepository = new InMemoryEventRepository();
        BookingRepository bookingRepository = new InMemoryBookingRepository(eventRepository);
        UserRepository userRepository = new InMemoryUserRepository(bookingRepository);
        VenueRepository venueRepository = new InMemoryVenueRepository();
        PaymentGateway paymentGateway = new MockPaymentGateway();

        // 2. Create and save a user and venue
        User user = new User(UUID.randomUUID(), "Test User", "test@example.com", LocalDateTime.now());
        userRepository.save(user);
        
        Venue venue = new Venue(UUID.randomUUID(), "Test Venue", "Test City", 100, LocalDateTime.now());
        venueRepository.save(venue);

        // 3. Create and save a fully seated event with 3 seats
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

        // 4. Wire BookingService
        BookingService bookingService = new BookingService(bookingRepository, userRepository, 
                                                         eventRepository, venueRepository, paymentGateway);

        // 5. Test happy path booking
        try {
            System.out.println("\n=== BookingService test - happy path ===");
            Booking booking = bookingService.createBooking(user.getUserId(), event.getEventId(), 2, "1234567812345678");
            System.out.println("✓ Booking created: " + booking.getBookingId());
            System.out.println("  Payment status: " + booking.getPaymentStatus());
            System.out.println("  Payment ID: " + booking.getPaymentId());
            System.out.println("  Seat details: " + booking.getSeatDetails());
            System.out.println("  Total amount: $" + booking.getTotalAmount());
        } catch (BookingException e) {
            System.out.println("✗ Booking failed: " + e.getMessage());
        }

        // 5b. Optional: test venue total capacity guard
        try {
            System.out.println("\n=== BookingService test - venue capacity guard ===");
            // Artificially set a very low venue capacity to trigger guard
            Venue updated = new Venue(venue.getVenueId(), venue.getName(), venue.getLocation(), 2, venue.getCreatedAt());
            venueRepository.save(updated);

            bookingService.createBooking(user.getUserId(), event.getEventId(), 1, "1234567812345678");
        } catch (BookingException e) {
            System.out.println("✓ Expected venue capacity failure: " 
                    + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // 6. Test capacity exceeded
        try {
            System.out.println("\n=== BookingService test - capacity exceeded ===");
            bookingService.createBooking(user.getUserId(), event.getEventId(), 10, "1234567812345678");
        } catch (BookingException e) {
            System.out.println("✓ Expected failure: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // 7. NEW: Advanced repository queries demo
        System.out.println("\n=== ADVANCED REPOSITORY QUERIES ===");
        
        // Create second user for testing
        User user2 = new User(UUID.randomUUID(), "User2", "user2@example.com", LocalDateTime.now());
        userRepository.save(user2);
        
        // Create PAID booking for user1 (already done above)
        // Try FAILED booking for user2 (won't create booking record)
        try {
            bookingService.createBooking(user2.getUserId(), event.getEventId(), 1, "invalid-card");
        } catch (BookingException e) {
            System.out.println("  User2 payment failed (expected)");
        }

        // Test basic queries
        System.out.println("Bookings for user1: " + bookingRepository.findByUserId(user.getUserId()).size());
        System.out.println("Bookings by venue: " + bookingRepository.findByVenueId(venue.getVenueId()).size());

        // Test advanced queries
        System.out.println("Bookings for paid users at venue: " + 
                          bookingRepository.findBookingsForPaidUsersAtVenue(venue.getVenueId()).size());
        System.out.println("Users without bookings at venue: " + 
                          userRepository.findUsersWithoutBookingsInVenue(venue.getVenueId()).size());

        // 8. Test future events
        System.out.println("\n=== FUTURE EVENTS CATALOG ===");
        System.out.println("Future events: " + eventRepository.findFutureEvents(LocalDateTime.now()).size());

        System.out.println("\n✓ All tests completed successfully!");
    }
}
