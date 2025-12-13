package com.eventbookingsystem.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.eventbookingsystem.model.Booking;

public interface BookingRepository {
    Booking save(Booking booking);

    Optional<Booking> findById(UUID bookingId);

    List<Booking> findAll();

    List<Booking> findByUserId(UUID userId);

    List<Booking> findByEventId(UUID eventId);
}
