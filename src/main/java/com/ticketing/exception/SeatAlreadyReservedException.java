package com.ticketing.exception;

public class SeatAlreadyReservedException extends RuntimeException {
    
    public SeatAlreadyReservedException(String message) {
        super(message);
    }
    
    public SeatAlreadyReservedException(String seatNumber, Long showId) {
        super(String.format("Le siège %s est déjà réservé pour ce spectacle (ID: %d)", seatNumber, showId));
    }
}
