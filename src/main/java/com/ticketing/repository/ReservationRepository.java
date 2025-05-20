package com.ticketing.repository;

import com.ticketing.model.Reservation;
import com.ticketing.model.Show;
import com.ticketing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
    List<Reservation> findByShow(Show show);
    Optional<Reservation> findByConfirmationCode(String confirmationCode);
    List<Reservation> findByUserOrderByReservationDateDesc(User user);
    List<Reservation> findByPaymentConfirmed(boolean paymentConfirmed);
}
