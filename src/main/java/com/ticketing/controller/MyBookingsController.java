package com.ticketing.controller;

import com.ticketing.model.Reservation;
import com.ticketing.model.User;
import com.ticketing.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/my-reservations")
public class MyBookingsController {

    private final ReservationService reservationService;

    @Autowired
    public MyBookingsController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("")
    public String showMyReservations(Model model, RedirectAttributes redirectAttributes) {
        try {
            // Get the authenticated user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Vous devez être connecté pour voir vos réservations.");
                return "redirect:/login";
            }
            
            User user = (User) auth.getPrincipal();
            List<Reservation> reservations = new ArrayList<>();
            
            try {
                // Try to get user's reservations from service
                reservations = reservationService.findByUser(user.getId());
            } catch (Exception e) {
                // If there's an error, just show an empty list
                model.addAttribute("errorMessage", "Impossible de charger vos réservations. Veuillez réessayer plus tard.");
            }
            
            model.addAttribute("reservations", reservations);
            return "ticket/my-reservations";
        } catch (Exception e) {
            // Fallback to static display in case of any error
            model.addAttribute("errorMessage", "Une erreur est survenue. Affichage en mode limité.");
            return "ticket/static-my-reservations";
        }
    }
}
