package com.eventbookingsystem.payment;

public class PaymentResult {
    private final boolean success;
    private final String paymentId;
    private final String failureReason;

    private PaymentResult(boolean success, String paymentId, String failureReason) {
        this.success = success;
        this.paymentId = paymentId;
        this.failureReason = failureReason;
    }

    public static PaymentResult success(String paymentId) {
        return new PaymentResult(true, paymentId, null);
    }

    public static PaymentResult failure(String failureReason) {
        return new PaymentResult(false, null, failureReason);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getFailureReason() {
        return failureReason;
    }
}
