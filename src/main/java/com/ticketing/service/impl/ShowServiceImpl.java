package com.ticketing.service.impl;

import com.ticketing.model.Show;
import com.ticketing.model.ShowCategory;
import com.ticketing.repository.ShowRepository;
import com.ticketing.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowServiceImpl implements ShowService {

    private final ShowRepository showRepository;

    @Override
    @Transactional
    public Show createShow(Show show) {
        return showRepository.save(show);
    }

    @Override
    @Transactional
    public Show updateShow(Show show) {
        return showRepository.save(show);
    }

    @Override
    @Transactional(readOnly = true)
    public Show findById(Long id) {
        return showRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Show not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Show> findAllShows() {
        return showRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Show> findShowsByCategory(ShowCategory category) {
        return showRepository.findByCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Show> searchShows(String keyword) {
        return showRepository.searchShows(keyword);
    }

    @Override
    @Transactional
    public void deleteShow(Long id) {
        showRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Show> findUpcomingShows() {
        return showRepository.findByDateAfter(LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Show> findUpcomingShowsByCategory(ShowCategory category) {
        return showRepository.findByCategoryAndDateAfter(category, LocalDateTime.now());
    }
}
