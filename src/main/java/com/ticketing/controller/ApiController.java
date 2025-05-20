package com.ticketing.controller;

import com.ticketing.dto.ReservationDto;
import com.ticketing.model.Reservation;
import com.ticketing.model.Seat;
import com.ticketing.service.ReservationService;
import com.ticketing.service.SeatService;
import com.ticketing.util.DtoConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final SeatService seatService;
    private final ReservationService reservationService;
    private final DtoConverter dtoConverter;

    /**
     * Get available seats for a show
     */
    @GetMapping("/shows/{showId}/seats")
    public ResponseEntity<Map<String, Object>> getAvailableSeats(@PathVariable Long showId) {
        List<Seat> availableSeats = seatService.findAvailableSeatsByShow(showId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("seats", availableSeats);
        response.put("availableCount", availableSeats.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check if a seat is available
     */
    @GetMapping("/seats/{seatId}/check")
    public ResponseEntity<Map<String, Object>> checkSeatAvailability(@PathVariable Long seatId) {
        Seat seat = seatService.findById(seatId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", seat.getId());
        response.put("seatNumber", seat.getSeatNumber());
        response.put("available", !seat.isReserved());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get user reservations
     */
    @GetMapping("/users/{userId}/reservations")
    public ResponseEntity<List<ReservationDto>> getUserReservations(@PathVariable Long userId) {
        List<Reservation> reservations = reservationService.findByUser(userId);
        
        List<ReservationDto> reservationDtos = reservations.stream()
                .map(dtoConverter::convertToReservationDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(reservationDtos);
    }

    /**
     * Confirm payment webhook (for Stripe integration)
     */
    @PostMapping("/payment/webhook")
    public ResponseEntity<Map<String, Object>> handlePaymentWebhook(@RequestBody Map<String, Object> payload) {
        // Extract payment intent ID and event type from the webhook payload
        String paymentIntentId = (String) ((Map<String, Object>) payload.get("data")).get("id");
        String eventType = (String) payload.get("type");
        
        Map<String, Object> response = new HashMap<>();
        
        // Handle payment success event
        if ("payment_intent.succeeded".equals(eventType)) {
            // Find the reservation associated with this payment
            // This would typically be stored in Stripe metadata
            String reservationId = (String) ((Map<String, Object>) ((Map<String, Object>) payload.get("data")).get("metadata")).get("reservation_id");
            
            if (reservationId != null) {
                // Confirm the reservation payment
                reservationService.confirmPayment(Long.parseLong(reservationId));
                response.put("status", "success");
                response.put("message", "Payment confirmed for reservation: " + reservationId);
            } else {
                response.put("status", "error");
                response.put("message", "No reservation ID found in payment metadata");
            }
        } else {
            response.put("status", "ignored");
            response.put("message", "Event type not handled: " + eventType);
        }
        
        return ResponseEntity.ok(response);
    }
}
