package com.eventbookingsystem.exception;

public class PaymentFailedException extends BookingException {
    public PaymentFailedException(String message) {
        super(message);
    }
}
