package com.eventbookingsystem.service;

import java.time.LocalDateTime;
import java.util.UUID;

import com.eventbookingsystem.exception.BookingException;
import com.eventbookingsystem.exception.CapacityExceededException;
import com.eventbookingsystem.exception.EntityNotFoundException;
import com.eventbookingsystem.exception.PaymentFailedException;
import com.eventbookingsystem.model.Booking;
import com.eventbookingsystem.model.Event;
import com.eventbookingsystem.model.PaymentStatus;
import com.eventbookingsystem.model.User;
import com.eventbookingsystem.payment.PaymentGateway;
import com.eventbookingsystem.payment.PaymentResult;
import com.eventbookingsystem.repository.BookingRepository;
import com.eventbookingsystem.repository.EventRepository;
import com.eventbookingsystem.repository.UserRepository;
import com.eventbookingsystem.repository.VenueRepository;

public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final PaymentGateway paymentGateway;

    public BookingService(BookingRepository bookingRepository,
                          UserRepository userRepository,
                          EventRepository eventRepository,
                          VenueRepository venueRepository,
                          PaymentGateway paymentGateway) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
        this.paymentGateway = paymentGateway;
    }

    public Booking createBooking(UUID userId,
                                 UUID eventId,
                                 int numberOfSeats,
                                 String creditCardNumber)
            throws BookingException {

        // 1. Validate inputs
        if (userId == null) {
            throw new BookingException("User ID must not be null");
        }
        if (eventId == null) {
            throw new BookingException("Event ID must not be null");
        }
        if (numberOfSeats <= 0) {
            throw new BookingException("Number of seats must be positive");
        }
        if (creditCardNumber == null || creditCardNumber.isBlank()) {
            throw new BookingException("Credit card number must not be empty");
        }

        // 2. Fetch and verify User exists
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("User with ID " + userId + " not found"));

        // 3. Fetch and verify Event exists and is future-dated
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Event with ID " + eventId + " not found"));

        if (!event.getEventDate().isAfter(LocalDateTime.now())) {
            throw new BookingException("Event must be in the future");
        }

        // 3a. Load venue
        var venue = venueRepository.findById(event.getVenueId())
            .orElseThrow(() -> new EntityNotFoundException("Venue with ID " + event.getVenueId() + " not found."));

        synchronized (event) {
            // 4. Check capacity BEFORE payment
            if (!event.canAccommodateBooking(numberOfSeats)) {
                throw new CapacityExceededException(
                        "Event " + event.getName() + " cannot accommodate " + numberOfSeats +
                        " seats. Available: " + event.getAvailableCapacity());
            }

            // 4b. SIMPLE VENUE CAPACITY GUARD
            int seatsAlreadyBookedAtVenueOnThatDate = bookingRepository.findByVenueId(venue.getVenueId()).stream()
                .filter(b -> eventRepository.findById(b.getEventId())
                        .map(e -> e.getEventDate().toLocalDate().equals(event.getEventDate().toLocalDate()))
                        .orElse(false))
                .mapToInt(Booking::getNumberOfSeats)
                .sum();

            if (seatsAlreadyBookedAtVenueOnThatDate + numberOfSeats > venue.getTotalCapacity()) {
                throw new CapacityExceededException(
                        "Venue " + venue.getName() + " cannot accommodate " + numberOfSeats +
                        " additional seats. Total capacity: " + venue.getTotalCapacity() +
                        ", already booked: " + seatsAlreadyBookedAtVenueOnThatDate);
            }

            // 5. Process payment through gateway
            double amount = calculateAmount(event, numberOfSeats);
            PaymentResult paymentResult = paymentGateway.processPayment(creditCardNumber, amount);
            if (!paymentResult.isSuccess()) {
                throw new PaymentFailedException("Payment failed: " + paymentResult.getFailureReason());
            }

            // 6. Create Booking entity with PAID status and reserve seats
            Booking booking = new Booking(
                    UUID.randomUUID(),
                    user.getUserId(),
                    event.getEventId(),
                    numberOfSeats,
                    null, // seatDetails set by reserveSeats
                    PaymentStatus.PAID,
                    paymentResult.getPaymentId(),
                    LocalDateTime.now(),
                    amount
            );

            event.reserveSeats(booking);
            bookingRepository.save(booking);
            eventRepository.save(event);

            return booking;
        }
    }

    // For now, use a flat price per seat; can be enhanced later.
    private double calculateAmount(Event event, int numberOfSeats) {
        return numberOfSeats * 50.0; // e.g., 50 per seat
    }
}
