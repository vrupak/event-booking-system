package com.eventbookingsystem.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SectionedSeatedEvent extends Event {
    private Map<String, Section> sections;

    public SectionedSeatedEvent() {
        this.sections = new HashMap<>();
    }

    public SectionedSeatedEvent(UUID eventId, UUID venueId, String name, LocalDateTime eventDate,
                                LocalDateTime createdAt, Map<String, Section> sections) {
        super(eventId, venueId, name, eventDate, createdAt);
        this.sections = sections != null ? sections : new HashMap<>();
    }

    @Override
    public boolean canAccommodateBooking(int seatsRequested) {
        return getAvailableCapacity() >= seatsRequested;
    }

    @Override
    public void reserveSeats(Booking booking) {
        int seatsToReserve = booking.getNumberOfSeats();
        int reserved = 0;
        StringBuilder sectionList = new StringBuilder();

        for (Map.Entry<String, Section> entry : sections.entrySet()) {
            Section section = entry.getValue();
            int availableInSection = section.getAvailableSeats();
            int toTake = Math.min(availableInSection, seatsToReserve - reserved);
            if (toTake > 0) {
                section.setAvailableSeats(section.getAvailableSeats() - toTake);
                reserved += toTake;
                if (reserved > 1) sectionList.append(",");
                sectionList.append(entry.getKey()).append("(").append(toTake).append(")");
                if (reserved >= seatsToReserve) break;
            }
        }

        booking.setSeatDetails("[" + sectionList.toString() + "]");
    }

    @Override
    public int getAvailableCapacity() {
        return sections.values().stream().mapToInt(Section::getAvailableSeats).sum();
    }

    // Getters and setters
    public Map<String, Section> getSections() { return sections; }
    public void setSections(Map<String, Section> sections) { this.sections = sections; }
}
