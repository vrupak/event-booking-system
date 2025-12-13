package com.eventbookingsystem.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.eventbookingsystem.model.Venue;

public interface VenueRepository {
    Venue save(Venue venue);

    Optional<Venue> findById(UUID venueId);

    List<Venue> findAll();
}
