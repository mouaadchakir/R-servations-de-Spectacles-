package com.ticketing.service;

import com.ticketing.model.Seat;
import com.ticketing.model.Show;

import java.util.List;

public interface SeatService {
    Seat createSeat(Seat seat);
    Seat updateSeat(Seat seat);
    Seat findById(Long id);
    List<Seat> findSeatsByShow(Show show);
    List<Seat> findSeatsByShow(Long showId);
    List<Seat> findAvailableSeatsByShow(Show show);
    List<Seat> findAvailableSeatsByShow(Long showId);
    boolean reserveSeat(Long seatId);
    boolean releaseSeat(Long seatId);
    void createInitialSeatsForShow(Show show, int numberOfRows, int seatsPerRow);
}
