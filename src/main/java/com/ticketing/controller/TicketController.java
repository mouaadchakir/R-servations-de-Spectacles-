package com.ticketing.controller;

import com.ticketing.model.Reservation;
import com.ticketing.model.Show;
import com.ticketing.model.User;
import com.ticketing.service.ReservationService;
import com.ticketing.service.ShowService;
import com.ticketing.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class TicketController {

    private final ReservationService reservationService;
    private final TicketService ticketService;
    private final ShowService showService;

    @GetMapping("/my-bookings")
    public String listUserBookings(Model model) {
        // Get authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        
        // Get user's reservations
        List<Reservation> reservations = reservationService.findUserReservationsOrdered(user.getId());
        model.addAttribute("reservations", reservations);
        
        return "ticket/my-bookings";
    }

    @GetMapping("/tickets/{reservationId}")
    public String showTicket(@PathVariable Long reservationId, Model model) {
        Reservation reservation = reservationService.findById(reservationId);
        
        // Check if this reservation belongs to the authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        
        if (!reservation.getUser().getId().equals(user.getId())) {
            return "redirect:/my-bookings";
        }
        
        // Check if payment is confirmed
        if (!reservation.isPaymentConfirmed()) {
            return "redirect:/payment/" + reservationId;
        }
        
        // Add reservation to model
        model.addAttribute("reservation", reservation);
        
        // Generate QR code and add to model
        model.addAttribute("qrCode", ticketService.getQRCodeBase64(reservation));
        
        return "ticket/view";
    }

    @GetMapping("/tickets/{reservationId}/download")
    public ResponseEntity<byte[]> downloadTicket(@PathVariable Long reservationId) {
        Reservation reservation = reservationService.findById(reservationId);
        
        // Check if this reservation belongs to the authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        
        if (!reservation.getUser().getId().equals(user.getId()) || !reservation.isPaymentConfirmed()) {
            return ResponseEntity.badRequest().build();
        }
        
        // Generate PDF ticket
        ByteArrayOutputStream pdfStream = ticketService.generateTicketPDF(reservation);
        
        // Prepare response
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "ticket_" + reservation.getConfirmationCode() + ".pdf");
        
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfStream.toByteArray());
    }

    @GetMapping("/generate-tickets")
    public ResponseEntity<byte[]> generateDemoTicket() {
        // Generate demo ticket
        ByteArrayOutputStream pdfStream = ticketService.generateDemoTicket();
        
        // Prepare response
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "demo_ticket.pdf");
        
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfStream.toByteArray());
    }
}
