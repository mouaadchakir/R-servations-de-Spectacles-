package com.ticketing.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.ticketing.model.Reservation;
import com.ticketing.service.PaymentService;
import com.ticketing.service.ReservationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Value("${stripe.api.key.secret}")
    private String stripeSecretKey;

    @Value("${stripe.api.key.public}")
    private String stripePublicKey;

    private final ReservationService reservationService;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Override
    public Map<String, Object> createPaymentIntent(Reservation reservation) {
        try {
            // Convert price to cents (Stripe requires amounts in cents)
            long amount = reservation.getTotalPrice().movePointRight(2).longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setCurrency("eur")
                    .setAmount(amount)
                    .setDescription("Reservation for " + reservation.getShow().getTitle())
                    .putMetadata("reservation_id", reservation.getId().toString())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            Map<String, Object> response = new HashMap<>();
            response.put("clientSecret", paymentIntent.getClientSecret());
            response.put("amount", amount);
            response.put("id", paymentIntent.getId());
            response.put("currency", "eur");

            return response;
        } catch (StripeException e) {
            throw new RuntimeException("Error creating payment intent", e);
        }
    }

    @Override
    public boolean processPaymentSuccess(String paymentIntentId, Long reservationId) {
        try {
            // Verify payment intent status with Stripe
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            if ("succeeded".equals(paymentIntent.getStatus())) {
                // Confirm the reservation payment
                return reservationService.confirmPayment(reservationId);
            }
            return false;
        } catch (StripeException e) {
            throw new RuntimeException("Error processing payment success", e);
        }
    }

    @Override
    public String getStripePublicKey() {
        return stripePublicKey;
    }
}
