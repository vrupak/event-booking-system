package com.eventbookingsystem.payment;

import java.util.UUID;

public class MockPaymentGateway implements PaymentGateway {

    @Override
    public PaymentResult processPayment(String creditCardNumber, double amount) {
        // Very simple rule for now:
        // - If card number length is 16 and amount > 0 => success
        // - Otherwise => failure with a message
        if (creditCardNumber == null || creditCardNumber.length() != 16) {
            return PaymentResult.failure("Invalid credit card number");
        }
        if (amount <= 0) {
            return PaymentResult.failure("Amount must be positive");
        }

        // Generate a fake paymentId
        String paymentId = "PAY-" + UUID.randomUUID();
        return PaymentResult.success(paymentId);
    }
}
