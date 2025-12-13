package com.eventbookingsystem.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.eventbookingsystem.model.Event;

public interface EventRepository {
    Event save(Event event);

    Optional<Event> findById(UUID eventId);

    List<Event> findAll();

    List<Event> findFutureEvents(LocalDateTime from);
}
