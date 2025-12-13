package com.eventbookingsystem.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.eventbookingsystem.model.Venue;

public class InMemoryVenueRepository implements VenueRepository {

    private final Map<UUID, Venue> storage = new ConcurrentHashMap<>();

    @Override
    public Venue save(Venue venue) {
        storage.put(venue.getVenueId(), venue);
        return venue;
    }

    @Override
    public Optional<Venue> findById(UUID venueId) {
        return Optional.ofNullable(storage.get(venueId));
    }

    @Override
    public List<Venue> findAll() {
        return new ArrayList<>(storage.values());
    }
}
