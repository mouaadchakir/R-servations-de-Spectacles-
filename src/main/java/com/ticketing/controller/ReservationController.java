package com.ticketing.controller;

import com.ticketing.model.Reservation;
import com.ticketing.model.Seat;
import com.ticketing.model.Show;
import com.ticketing.model.User;
import com.ticketing.service.PaymentService;
import com.ticketing.service.ReservationService;
import com.ticketing.service.SeatService;
import com.ticketing.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ReservationController {

    private final ShowService showService;
    private final SeatService seatService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;

    @GetMapping("/shows/{id}/reserve")
    public String showReservationForm(@PathVariable Long id, Model model) {
        Show show = showService.findById(id);
        model.addAttribute("show", show);
        model.addAttribute("availableSeats", seatService.findAvailableSeatsByShow(show));
        return "reservation/create";
    }

    @PostMapping("/shows/{id}/reserve")
    public String processReservation(@PathVariable Long id,
                                   @RequestParam("seatId") Long seatId,
                                   RedirectAttributes redirectAttributes) {
        try {
            // Get authenticated user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            
            // Create the reservation
            Reservation reservation = reservationService.createReservation(user.getId(), id, seatId);
            
            // Redirect to payment page
            return "redirect:/payment/" + reservation.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to reserve seat: " + e.getMessage());
            return "redirect:/shows/" + id;
        }
    }

    @GetMapping("/payment/{reservationId}")
    public String showPaymentPage(@PathVariable Long reservationId, Model model) {
        Reservation reservation = reservationService.findById(reservationId);
        
        // Check if this reservation belongs to the authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        
        if (!reservation.getUser().getId().equals(user.getId())) {
            return "redirect:/my-bookings";
        }
        
        // Check if already paid
        if (reservation.isPaymentConfirmed()) {
            return "redirect:/tickets/" + reservationId;
        }
        
        // Create payment intent with Stripe
        Map<String, Object> paymentData = paymentService.createPaymentIntent(reservation);
        
        model.addAttribute("reservation", reservation);
        model.addAttribute("clientSecret", paymentData.get("clientSecret"));
        model.addAttribute("amount", paymentData.get("amount"));
        model.addAttribute("stripePublicKey", paymentService.getStripePublicKey());
        
        return "payment/checkout";
    }

    @PostMapping("/payment/{reservationId}")
    @ResponseBody
    public Map<String, Object> processPayment(@PathVariable Long reservationId,
                                            @RequestParam("paymentIntentId") String paymentIntentId) {
        boolean success = paymentService.processPaymentSuccess(paymentIntentId, reservationId);
        return Map.of("success", success);
    }

    @GetMapping("/payment/success")
    public String paymentSuccess(@RequestParam("reservation_id") Long reservationId, 
                               RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("success", "Payment successful! Your ticket is ready.");
        return "redirect:/tickets/" + reservationId;
    }

    @GetMapping("/payment/error")
    public String paymentError(@RequestParam("reservation_id") Long reservationId, 
                             RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Payment failed. Please try again.");
        return "redirect:/payment/" + reservationId;
    }
}
