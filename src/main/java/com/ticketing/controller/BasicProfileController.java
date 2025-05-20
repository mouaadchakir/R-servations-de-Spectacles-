package com.ticketing.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ticketing.model.User;
import com.ticketing.model.Reservation;
import com.ticketing.service.UserService;
import com.ticketing.service.ReservationService;

@Controller
@RequestMapping("/my-profile")
public class BasicProfileController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ReservationService reservationService;

    @GetMapping("")
    public String showBasicProfile(Model model) {
        try {
            // Get current authenticated user's username
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            // Add username to model (required minimal info)
            model.addAttribute("username", username);
            
            try {
                // Try to get the user from the database
                User user = userService.findByUsername(username);
                
                if (user != null) {
                    // Add basic user info if available
                    model.addAttribute("email", user.getEmail());
                    
                    // Format registration date
                    if (user.getRegistrationDate() != null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
                        model.addAttribute("joinDate", dateFormat.format(user.getRegistrationDate()));
                    } else {
                        model.addAttribute("joinDate", "Date inconnue");
                    }
                    
                    // Try to get reservation count
                    try {
                        long reservationCount = userService.getUserReservationCount(user.getId());
                        model.addAttribute("reservationCount", reservationCount);
                    } catch (Exception e) {
                        model.addAttribute("reservationCount", 0);
                    }
                    
                    // Try to get simple reservation data (without complex relationships)
                    try {
                        List<Map<String, Object>> simpleReservations = new ArrayList<>();
                        List<Reservation> userReservations = reservationService.findByUser(user);
                        
                        if (userReservations != null && !userReservations.isEmpty()) {
                            for (Reservation res : userReservations) {
                                Map<String, Object> resMap = new HashMap<>();
                                
                                // Only extract simple string/primitive values that won't cause serialization issues
                                if (res.getShow() != null) {
                                    resMap.put("title", res.getShow().getTitle());
                                    resMap.put("date", (res.getShow().getDate() != null) ? 
                                        new SimpleDateFormat("dd/MM/yyyy").format(res.getShow().getDate()) : "Date non spécifiée");
                                } else {
                                    resMap.put("title", "Spectacle inconnu");
                                    resMap.put("date", "Date non spécifiée");
                                }
                                
                                resMap.put("confirmed", res.isPaymentConfirmed());
                                simpleReservations.add(resMap);
                            }
                        }
                        
                        model.addAttribute("reservations", simpleReservations);
                        model.addAttribute("showCount", userReservations.stream()
                            .map(r -> r.getShow() != null ? r.getShow().getTitle() : "")
                            .distinct()
                            .count());
                    } catch (Exception e) {
                        model.addAttribute("reservations", new ArrayList<>());
                        model.addAttribute("showCount", 0);
                    }
                }
            } catch (Exception e) {
                // User lookup failed, but we can still show the page with minimal info
                model.addAttribute("errorMessage", "Impossible de récupérer toutes vos informations. Affichage limité.");
                e.printStackTrace();
            }
            
            // Return our new profile page
            return "user/basic-profile";
            
        } catch (Exception ex) {
            // Critical failure - fallback to absolute minimal display
            model.addAttribute("username", "Utilisateur");
            model.addAttribute("errorMessage", "Une erreur s'est produite lors du chargement de votre profil.");
            ex.printStackTrace();
            return "user/basic-profile";
        }
    }
}
