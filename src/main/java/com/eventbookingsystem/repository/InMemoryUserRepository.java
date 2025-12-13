package com.eventbookingsystem.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.eventbookingsystem.model.User;
;

public class InMemoryUserRepository implements UserRepository {
    private final Map<UUID, User> storage = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        storage.put(user.getUserId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return Optional.ofNullable(storage.get(userId));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(storage.values());
    }

}
