package com.ticketing.service.impl;

import com.ticketing.model.Seat;
import com.ticketing.model.SeatCategory;
import com.ticketing.model.Show;
import com.ticketing.repository.SeatRepository;
import com.ticketing.repository.ShowRepository;
import com.ticketing.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final ShowRepository showRepository;

    @Override
    @Transactional
    public Seat createSeat(Seat seat) {
        return seatRepository.save(seat);
    }

    @Override
    @Transactional
    public Seat updateSeat(Seat seat) {
        return seatRepository.save(seat);
    }

    @Override
    @Transactional(readOnly = true)
    public Seat findById(Long id) {
        return seatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seat not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Seat> findSeatsByShow(Show show) {
        return seatRepository.findByShow(show);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Seat> findSeatsByShow(Long showId) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found with id: " + showId));
        return findSeatsByShow(show);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Seat> findAvailableSeatsByShow(Show show) {
        return seatRepository.findByShowAndIsReserved(show, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Seat> findAvailableSeatsByShow(Long showId) {
        return seatRepository.findByShowIdAndIsReserved(showId, false);
    }

    @Override
    @Transactional
    public boolean reserveSeat(Long seatId) {
        Seat seat = findById(seatId);
        if (!seat.isReserved()) {
            seat.setReserved(true);
            seatRepository.save(seat);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean releaseSeat(Long seatId) {
        Seat seat = findById(seatId);
        if (seat.isReserved()) {
            seat.setReserved(false);
            seatRepository.save(seat);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void createInitialSeatsForShow(Show show, int numberOfRows, int seatsPerRow) {
        List<Seat> seats = new ArrayList<>();
        
        // Define row labels (A, B, C, ...)
        String[] rows = new String[numberOfRows];
        for (int i = 0; i < numberOfRows; i++) {
            rows[i] = String.valueOf((char)('A' + i));
        }
        
        // Create seats for each row
        for (int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
            String row = rows[rowIndex];
            SeatCategory category;
            
            // First 2 rows are VIP, next 3 are PREMIUM, rest are STANDARD
            if (rowIndex < 2) {
                category = SeatCategory.VIP;
            } else if (rowIndex < 5) {
                category = SeatCategory.PREMIUM;
            } else {
                category = SeatCategory.STANDARD;
            }
            
            for (int seatNum = 1; seatNum <= seatsPerRow; seatNum++) {
                Seat seat = new Seat();
                seat.setShow(show);
                seat.setRow(row);
                seat.setNumber(seatNum);
                seat.setSeatNumber(row + "-" + seatNum);
                seat.setCategory(category);
                seat.setReserved(false);
                seats.add(seat);
            }
        }
        
        // Save all seats
        seatRepository.saveAll(seats);
    }
    
    @Override
    @Transactional
    public void deleteAllSeatsByShowId(Long showId) {
        seatRepository.deleteByShowId(showId);
    }
}
