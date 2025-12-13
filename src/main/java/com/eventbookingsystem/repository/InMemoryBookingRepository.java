package com.eventbookingsystem.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.eventbookingsystem.model.Booking;

public class InMemoryBookingRepository implements BookingRepository {

    private final Map<UUID, Booking> storage = new ConcurrentHashMap<>();

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
}
