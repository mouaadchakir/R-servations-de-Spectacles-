package com.ticketing.controller;

import com.ticketing.model.Reservation;
import com.ticketing.model.Seat;
import com.ticketing.model.Show;
import com.ticketing.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/ultra-simple")
public class UltraSimpleController {

    @GetMapping("/reserve/{showId}")
    public String showStaticReservationPage(@PathVariable String showId, Model model) {
        // Always add the show ID to the model
        model.addAttribute("showId", showId);
        
        try {
            // Create a mock show directly (no database access)
            Show mockShow = new Show();
            mockShow.setId(Long.valueOf(showId));
            mockShow.setTitle("Spectacle #" + showId);
            mockShow.setLocation("Salle Principale");
            mockShow.setDate(LocalDateTime.now().plusDays(7));
            mockShow.setPrice(new BigDecimal("50.00"));
            
            model.addAttribute("show", mockShow);
        } catch (Exception e) {
            // Si une erreur se produit, on continue quand même
            model.addAttribute("errorMessage", "Information limitée disponible");
        }
        
        return "reservation/ultra-simple-create";
    }
    
    @PostMapping("/confirm/{showId}")
    public String processStaticReservation(@PathVariable String showId, RedirectAttributes redirectAttributes) {
        try {
            // Create a simulated reservation code
            String reservationCode = "T" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            
            // Add it to redirect attributes
            redirectAttributes.addFlashAttribute("reservationCode", reservationCode);
            redirectAttributes.addFlashAttribute("showId", showId);
            redirectAttributes.addFlashAttribute("price", new BigDecimal("50.00"));
            
            // Redirect to payment confirmation page
            return "redirect:/ultra-simple/payment";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur de réservation: " + e.getMessage());
            return "redirect:/ultra-simple/reserve/" + showId;
        }
    }
    
    @GetMapping("/payment")
    public String showStaticPaymentPage(Model model) {
        // This page will use the flash attributes passed from the reserve method
        return "payment/ultra-simple-checkout";
    }
    
    @PostMapping("/payment/confirm")
    public String confirmStaticPayment(RedirectAttributes redirectAttributes) {
        // Create confirmation code
        String confirmationCode = "C" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        redirectAttributes.addFlashAttribute("confirmationCode", confirmationCode);
        
        return "redirect:/ultra-simple/confirmation";
    }
    
    @GetMapping("/confirmation")
    public String showStaticConfirmationPage(Model model) {
        // This will use the flash attributes
        if (!model.containsAttribute("confirmationCode")) {
            model.addAttribute("confirmationCode", "DÉMO123456");
        }
        
        return "payment/ultra-simple-confirmation";
    }
}
