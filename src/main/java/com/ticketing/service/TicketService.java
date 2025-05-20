package com.ticketing.service;

import com.ticketing.model.Reservation;

import java.io.ByteArrayOutputStream;

public interface TicketService {
    /**
     * Generate a QR code as byte array for a given reservation
     */
    byte[] generateQRCode(Reservation reservation);
    
    /**
     * Generate a PDF ticket for a given reservation
     */
    ByteArrayOutputStream generateTicketPDF(Reservation reservation);
    
    /**
     * Generate a sample demo ticket (for testing purposes)
     */
    ByteArrayOutputStream generateDemoTicket();
    
    /**
     * Get the base64 encoded QR code image for a reservation
     */
    String getQRCodeBase64(Reservation reservation);
}
