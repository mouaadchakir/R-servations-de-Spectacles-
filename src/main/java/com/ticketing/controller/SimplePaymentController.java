package com.ticketing.controller;

import com.ticketing.model.Reservation;
import com.ticketing.model.User;
import com.ticketing.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/simple-payment")
@RequiredArgsConstructor
public class SimplePaymentController {

    private final ReservationService reservationService;

    @GetMapping("/{reservationId}")
    public String showSimplePaymentPage(@PathVariable Long reservationId, Model model) {
        try {
            // Get the reservation
            Reservation reservation = reservationService.findById(reservationId);
            
            // Check if this reservation belongs to the authenticated user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            
            if (!reservation.getUser().getId().equals(user.getId())) {
                return "redirect:/";
            }
            
            // Check if already paid
            if (reservation.isPaymentConfirmed()) {
                model.addAttribute("successMessage", "Cette réservation a déjà été confirmée.");
                return "payment/simple-confirmation";
            }
            
            // Add reservation details to model
            model.addAttribute("reservation", reservation);
            model.addAttribute("show", reservation.getShow());
            model.addAttribute("seat", reservation.getSeat());
            model.addAttribute("amount", reservation.getTotalPrice());
            
            return "payment/simple-checkout";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors du chargement de la réservation : " + e.getMessage());
            return "payment/simple-checkout";
        }
    }
    
    @PostMapping("/{reservationId}/confirm")
    public String confirmPayment(@PathVariable Long reservationId, RedirectAttributes redirectAttributes) {
        try {
            // Get the reservation
            Reservation reservation = reservationService.findById(reservationId);
            
            // Check if this reservation belongs to the authenticated user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            
            if (!reservation.getUser().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Vous n'êtes pas autorisé à confirmer cette réservation.");
                return "redirect:/";
            }
            
            // Confirm the payment without Stripe
            boolean success = reservationService.confirmPayment(reservationId);
            
            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "Paiement confirmé avec succès !");
                return "redirect:/simple-payment/" + reservationId + "/success";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la confirmation du paiement.");
                return "redirect:/simple-payment/" + reservationId;
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la confirmation : " + e.getMessage());
            return "redirect:/simple-payment/" + reservationId;
        }
    }
    
    @GetMapping("/{reservationId}/success")
    public String showSuccessPage(@PathVariable Long reservationId, Model model) {
        try {
            // Get the reservation
            Reservation reservation = reservationService.findById(reservationId);
            
            // Add reservation details to model
            model.addAttribute("reservation", reservation);
            model.addAttribute("show", reservation.getShow());
            model.addAttribute("seat", reservation.getSeat());
            
            return "payment/simple-confirmation";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors du chargement de la confirmation : " + e.getMessage());
            return "payment/simple-confirmation";
        }
    }
}
