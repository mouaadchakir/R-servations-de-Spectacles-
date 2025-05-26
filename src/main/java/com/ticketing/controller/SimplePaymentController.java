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
        // Validation de l'ID de réservation
        if (reservationId == null || reservationId <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "ID de réservation invalide.");
            return "redirect:/my-reservations";
        }
        
        try {
            // Vérification de l'authentification
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Vous devez être connecté pour confirmer un paiement.");
                return "redirect:/login";
            }
            
            // Récupération de la réservation
            Reservation reservation;
            try {
                reservation = reservationService.findById(reservationId);
            } catch (Exception e) {
                // Gestion spécifique pour les réservations non trouvées
                redirectAttributes.addFlashAttribute("errorMessage", "Réservation introuvable : " + e.getMessage());
                return "redirect:/my-reservations";
            }
            
            // Vérification si la réservation est déjà confirmée
            if (reservation.isPaymentConfirmed()) {
                redirectAttributes.addFlashAttribute("infoMessage", "Cette réservation a déjà été confirmée.");
                return "redirect:/my-reservations";
            }
            
            // Vérification que l'utilisateur est bien le propriétaire de la réservation
            User user = (User) auth.getPrincipal();
            if (!reservation.getUser().getId().equals(user.getId())) {
                // Tentative d'accès non autorisé à une réservation
                redirectAttributes.addFlashAttribute("errorMessage", "Vous n'êtes pas autorisé à confirmer cette réservation.");
                return "redirect:/my-reservations";
            }
            
            // Confirmation du paiement
            boolean success = reservationService.confirmPayment(reservationId);
            
            if (success) {
                // Mise à jour réussie
                redirectAttributes.addFlashAttribute("successMessage", "Paiement de " + reservation.getTotalPrice() + " DH confirmé avec succès pour le spectacle '" + reservation.getShow().getTitle() + "' !");
                return "redirect:/my-reservations";
            } else {
                // Erreur lors de la mise à jour
                redirectAttributes.addFlashAttribute("errorMessage", "Erreur technique lors de la confirmation du paiement. Veuillez réessayer.");
                return "redirect:/simple-payment/" + reservationId;
            }
        } catch (Exception e) {
            // Log l'exception pour les administrateurs système (dans une application de production)
            // logger.error("Erreur lors de la confirmation du paiement {}", reservationId, e);
            
            // Message générique pour l'utilisateur
            redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite lors de la confirmation du paiement. Service technique notifié.");
            return "redirect:/my-reservations";
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