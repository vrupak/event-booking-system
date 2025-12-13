package com.eventbookingsystem.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.eventbookingsystem.model.Event;

public class InMemoryEventRepository implements EventRepository {

    private final Map<UUID, Event> storage = new ConcurrentHashMap<>();

    @Override
    public Event save(Event event) {
        storage.put(event.getEventId(), event);
        return event;
    }

    @Override
    public Optional<Event> findById(UUID eventId) {
        return Optional.ofNullable(storage.get(eventId));
    }

    @Override
    public List<Event> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Event> findFutureEvents(LocalDateTime from) {
        List<Event> result = new ArrayList<>();
        for (Event event : storage.values()) {
            if (event.getEventDate().isAfter(from)) {
                result.add(event);
            }
        }
        return result;
    }
}
