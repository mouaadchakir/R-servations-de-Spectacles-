package com.ticketing.service.impl;

import com.ticketing.model.Reservation;
import com.ticketing.model.Seat;
import com.ticketing.model.Show;
import com.ticketing.model.User;
import com.ticketing.repository.ReservationRepository;
import com.ticketing.service.ReservationService;
import com.ticketing.service.SeatService;
import com.ticketing.service.ShowService;
import com.ticketing.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final ShowService showService;
    private final SeatService seatService;

    @Override
    @Transactional
    public Reservation createReservation(Reservation reservation) {
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setPaymentConfirmed(false);
        
        // Generate a unique confirmation code
        reservation.setConfirmationCode(generateConfirmationCode());
        
        // Reserve the seat
        seatService.reserveSeat(reservation.getSeat().getId());
        
        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public Reservation createReservation(Long userId, Long showId, Long seatId) {
        User user = userService.findById(userId);
        Show show = showService.findById(showId);
        Seat seat = seatService.findById(seatId);
        
        // Check if seat is available
        if (seat.isReserved()) {
            throw new RuntimeException("Seat is already reserved");
        }
        
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setShow(show);
        reservation.setSeat(seat);
        reservation.setTotalPrice(show.getPrice());
        
        return createReservation(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public Reservation findById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Reservation findByConfirmationCode(String confirmationCode) {
        return reservationRepository.findByConfirmationCode(confirmationCode)
                .orElseThrow(() -> new RuntimeException("Reservation not found with confirmation code: " + confirmationCode));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findByUser(User user) {
        return reservationRepository.findByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findByUser(Long userId) {
        User user = userService.findById(userId);
        return findByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findByShow(Show show) {
        return reservationRepository.findByShow(show);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findByShow(Long showId) {
        Show show = showService.findById(showId);
        return findByShow(show);
    }

    @Override
    @Transactional
    public boolean confirmPayment(Long reservationId) {
        Reservation reservation = findById(reservationId);
        reservation.setPaymentConfirmed(true);
        reservationRepository.save(reservation);
        return true;
    }

    @Override
    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = findById(reservationId);
        
        // Release the seat
        seatService.releaseSeat(reservation.getSeat().getId());
        
        // Delete the reservation
        reservationRepository.deleteById(reservationId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findUserReservationsOrdered(Long userId) {
        User user = userService.findById(userId);
        return reservationRepository.findByUserOrderByReservationDateDesc(user);
    }

    @Override
    public String generateConfirmationCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
