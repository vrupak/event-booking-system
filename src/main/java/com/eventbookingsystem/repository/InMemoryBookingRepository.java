package com.eventbookingsystem.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.eventbookingsystem.model.Booking;
import com.eventbookingsystem.model.PaymentStatus;

public class InMemoryBookingRepository implements BookingRepository {

    private final Map<UUID, Booking> storage = new ConcurrentHashMap<>();
    private final EventRepository eventRepository;

    public InMemoryBookingRepository(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Booking save(Booking booking) {
        storage.put(booking.getBookingId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(UUID bookingId) {
        return Optional.ofNullable(storage.get(bookingId));
    }

    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Booking> findByUserId(UUID userId) {
        return storage.values().stream()
                .filter(b -> b.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByEventId(UUID eventId) {
        return storage.values().stream()
                .filter(b -> b.getEventId().equals(eventId))
                .collect(Collectors.toList());
    }

    // NEW: Simulate JOIN venue -> events -> bookings
    @Override
    public List<Booking> findByVenueId(UUID venueId) {
        return eventRepository.findAll().stream()
                .filter(e -> e.getVenueId().equals(venueId))
                .flatMap(e -> findByEventId(e.getEventId()).stream())
                .toList();
    }

    // NEW: Advanced query - users with PAID booking at venue -> their bookings
    @Override
    public List<Booking> findBookingsForPaidUsersAtVenue(UUID venueId) {
        // Step 1: Get PAID bookings at venue -> unique user IDs
        var paidUsersAtVenue = findByVenueId(venueId).stream()
                .filter(b -> b.getPaymentStatus() == PaymentStatus.PAID)
                .map(Booking::getUserId)
                .collect(Collectors.toSet());

        // Step 2: Return ALL bookings for those users at venue
        return findByVenueId(venueId).stream()
                .filter(b -> paidUsersAtVenue.contains(b.getUserId()))
                .toList();
    }
}
