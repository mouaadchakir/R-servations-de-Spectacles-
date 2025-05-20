package com.ticketing.controller;

import com.ticketing.model.Reservation;
import com.ticketing.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/reservations")
@RequiredArgsConstructor
public class AdminReservationController {

    private final ReservationService reservationService;

    @GetMapping("")
    public String listAllReservations(Model model) {
        try {
            // Récupérer toutes les réservations depuis la base de données
            List<Reservation> allReservations = new ArrayList<>();
            
            try {
                // Essayer de récupérer les réservations de tous les spectacles
                allReservations = reservationService.findAll();
            } catch (Exception e) {
                model.addAttribute("errorMessage", "Impossible de charger toutes les réservations: " + e.getMessage());
            }
            
            model.addAttribute("reservations", allReservations);
            return "admin/reservations";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Une erreur est survenue lors du chargement des réservations: " + e.getMessage());
            return "admin/reservations";
        }
    }

    @GetMapping("/{id}")
    public String viewReservationDetails(@PathVariable Long id, Model model) {
        try {
            Reservation reservation = reservationService.findById(id);
            model.addAttribute("reservation", reservation);
            return "admin/reservation-details";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Impossible de trouver la réservation: " + e.getMessage());
            return "admin/reservations";
        }
    }

    @PostMapping("/{id}/confirm")
    public String confirmReservationPayment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            boolean success = reservationService.confirmPayment(id);
            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "Paiement confirmé avec succès");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Impossible de confirmer le paiement");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la confirmation: " + e.getMessage());
        }
        return "redirect:/admin/reservations/" + id;
    }

    @PostMapping("/{id}/cancel")
    public String cancelReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservationService.cancelReservation(id);
            redirectAttributes.addFlashAttribute("successMessage", "Réservation annulée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'annulation: " + e.getMessage());
        }
        return "redirect:/admin/reservations";
    }
}
