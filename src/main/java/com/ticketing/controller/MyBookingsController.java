package com.ticketing.controller;

import com.ticketing.model.Reservation;
import com.ticketing.model.User;
import com.ticketing.service.ReservationService;
import com.ticketing.service.UserService;
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
    private final UserService userService;

    @Autowired
    public MyBookingsController(ReservationService reservationService, UserService userService) {
        this.reservationService = reservationService;
        this.userService = userService;
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
            
            // Ajout de détails de débogage
            model.addAttribute("debugAuthType", auth.getClass().getName());
            model.addAttribute("debugPrincipalType", auth.getPrincipal().getClass().getName());
            
            List<Reservation> reservations = new ArrayList<>();
            
            try {
                // Récupération de l'utilisateur en fonction du type d'authentification
                Long userId;
                if (auth.getPrincipal() instanceof User) {
                    User user = (User) auth.getPrincipal();
                    userId = user.getId();
                    model.addAttribute("debugUsername", user.getUsername());
                } else {
                    // Trouver l'utilisateur par son nom d'utilisateur
                    String username = auth.getName();
                    model.addAttribute("debugUsername", username);
                    
                    // Chercher l'utilisateur dans la base de données
                    userId = userService.findByUsername(username).getId();
                }
                
                // Récupérer les réservations de l'utilisateur
                reservations = reservationService.findUserReservationsOrdered(userId);
                model.addAttribute("debugUserId", userId);
                model.addAttribute("debugReservationCount", reservations.size());
            } catch (Exception e) {
                // Si une erreur survient, afficher les détails
                model.addAttribute("errorMessage", "Impossible de charger vos réservations: " + e.getMessage());
                model.addAttribute("debugErrorDetails", e.getClass().getName() + ": " + e.getMessage());
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
