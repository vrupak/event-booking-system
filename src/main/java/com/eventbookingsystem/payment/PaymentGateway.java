package com.eventbookingsystem.payment;

public interface PaymentGateway {
    PaymentResult processPayment(String creditCardNumber, double amount);
}
