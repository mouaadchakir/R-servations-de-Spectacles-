package com.ticketing.service;

import com.ticketing.model.Show;
import com.ticketing.model.ShowCategory;

import java.util.List;

public interface ShowService {
    Show createShow(Show show);
    Show updateShow(Show show);
    Show findById(Long id);
    List<Show> findAllShows();
    List<Show> findShowsByCategory(ShowCategory category);
    List<Show> searchShows(String keyword);
    void deleteShow(Long id);
    List<Show> findUpcomingShows();
    List<Show> findUpcomingShowsByCategory(ShowCategory category);
}
