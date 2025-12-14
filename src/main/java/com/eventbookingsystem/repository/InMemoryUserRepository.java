package com.eventbookingsystem.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.eventbookingsystem.model.Booking;
import com.eventbookingsystem.model.User;
;

public class InMemoryUserRepository implements UserRepository {
    private final ConcurrentHashMap<UUID, User> users = new ConcurrentHashMap<>();
    private final BookingRepository bookingRepository;  // For NOT IN logic

    public InMemoryUserRepository(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public User save(User user) {
        users.put(user.getUserId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<User> findAll() {
        return users.values().stream().toList();
    }

    // NEW: Advanced query - users NOT IN (users with bookings at venue)
    @Override
    public List<User> findUsersWithoutBookingsInVenue(UUID venueId) {
        var usersWithBookings = bookingRepository.findByVenueId(venueId).stream()
                .map(Booking::getUserId)
                .collect(Collectors.toSet());

        return users.values().stream()
                .filter(u -> !usersWithBookings.contains(u.getUserId()))
                .toList();
    }
}