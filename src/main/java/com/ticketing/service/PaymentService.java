package com.ticketing.service;

import com.ticketing.model.Reservation;

import java.util.Map;

public interface PaymentService {
    /**
     * Create a payment intent with Stripe
     * @param reservation Reservation to create payment for
     * @return Map containing client secret and payment information
     */
    Map<String, Object> createPaymentIntent(Reservation reservation);
    
    /**
     * Process payment success
     * @param paymentIntentId The Stripe payment intent ID
     * @param reservationId The reservation ID
     * @return True if payment was successful
     */
    boolean processPaymentSuccess(String paymentIntentId, Long reservationId);
    
    /**
     * Get public key for Stripe
     * @return Stripe public key
     */
    String getStripePublicKey();
}
