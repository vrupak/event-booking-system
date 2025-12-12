package com.eventbookingsystem.exception;

public class EntityNotFoundException extends BookingException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
