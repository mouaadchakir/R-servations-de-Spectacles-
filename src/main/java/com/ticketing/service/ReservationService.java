package com.ticketing.service;

import com.ticketing.model.Reservation;
import com.ticketing.model.Show;
import com.ticketing.model.User;

import java.util.List;

public interface ReservationService {
    Reservation createReservation(Reservation reservation);
    Reservation createReservation(Long userId, Long showId, Long seatId);
    Reservation findById(Long id);
    Reservation findByConfirmationCode(String confirmationCode);
    List<Reservation> findByUser(User user);
    List<Reservation> findByUser(Long userId);
    List<Reservation> findByShow(Show show);
    List<Reservation> findByShow(Long showId);
    boolean confirmPayment(Long reservationId);
    void cancelReservation(Long reservationId);
    List<Reservation> findUserReservationsOrdered(Long userId);
    String generateConfirmationCode();
}
