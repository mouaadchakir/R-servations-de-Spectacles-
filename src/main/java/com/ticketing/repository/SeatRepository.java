package com.ticketing.repository;

import com.ticketing.model.Seat;
import com.ticketing.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByShow(Show show);
    List<Seat> findByShowAndIsReserved(Show show, boolean isReserved);
    List<Seat> findByShowIdAndIsReserved(Long showId, boolean isReserved);
    
    void deleteByShowId(Long showId);
    List<Seat> findByShowId(Long showId);
}
