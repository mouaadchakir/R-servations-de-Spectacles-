package com.ticketing.controller;

import com.ticketing.model.Reservation;
import com.ticketing.model.Seat;
import com.ticketing.model.Show;
import com.ticketing.model.User;
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

@Controller
@RequestMapping("/simple-reservation")
@RequiredArgsConstructor
public class SimpleReservationController {

    private final ShowService showService;
    private final SeatService seatService;
    private final ReservationService reservationService;

    @GetMapping("/{id}")
    public String showSimpleReservationForm(@PathVariable Long id, Model model) {
        try {
            // Add show ID to the model in case we can't load the full show
            model.addAttribute("showId", id);
            
            try {
                // Try to get the show, but don't fail if we can't
                Show show = showService.findById(id);
                if (show != null) {
                    model.addAttribute("show", show);
                }
            } catch (Exception e) {
                // If we can't load the show, just proceed with the ID
                model.addAttribute("errorMessage", "Impossible de charger les détails du spectacle. Vous pouvez quand même réserver.");
            }
            
            return "reservation/simple-create";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Une erreur s'est produite : " + ex.getMessage());
            return "reservation/simple-create";
        }
    }

    @PostMapping("/{id}/reserve")
    public String processSimpleReservation(@PathVariable Long id,
                                      @RequestParam("seatId") Long seatId,
                                      RedirectAttributes redirectAttributes) {
        try {
            // Get authenticated user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            
            // Create the reservation
            Reservation reservation = reservationService.createReservation(user.getId(), id, seatId);
            
            // Redirect to simple payment page
            return "redirect:/simple-payment/" + reservation.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur de réservation : " + e.getMessage());
            return "redirect:/simple-reservation/" + id;
        }
    }
}
