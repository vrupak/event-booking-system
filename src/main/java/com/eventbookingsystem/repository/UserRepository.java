package com.eventbookingsystem.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.eventbookingsystem.model.User;

public interface UserRepository {
    User save(User user);
    
    Optional<User> findById(UUID userId);

    List<User> findAll();
}
