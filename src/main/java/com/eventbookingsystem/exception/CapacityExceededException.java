package com.eventbookingsystem.exception;

public class CapacityExceededException extends BookingException {
    public CapacityExceededException(String message) {
        super(message);
    }
}
