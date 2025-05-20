package com.ticketing.repository;

import com.ticketing.model.Show;
import com.ticketing.model.ShowCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    List<Show> findByCategory(ShowCategory category);
    
    List<Show> findByDateAfter(LocalDateTime date);
    
    @Query("SELECT s FROM Show s WHERE s.title LIKE %?1% OR s.description LIKE %?1% OR s.location LIKE %?1%")
    List<Show> searchShows(String keyword);
    
    List<Show> findByCategoryAndDateAfter(ShowCategory category, LocalDateTime date);
}
