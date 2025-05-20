package com.ticketing.controller;

import com.ticketing.model.Show;
import com.ticketing.model.ShowCategory;
import com.ticketing.service.SeatService;
import com.ticketing.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;
    private final SeatService seatService;

    @GetMapping
    public String listShows(Model model) {
        List<Show> shows = showService.findUpcomingShows();
        model.addAttribute("shows", shows);
        model.addAttribute("categories", ShowCategory.values());
        return "show/list";
    }

    @GetMapping("/{id}")
    public String showDetails(@PathVariable Long id, Model model) {
        Show show = showService.findById(id);
        model.addAttribute("show", show);
        
        // Get available seats for the show
        model.addAttribute("availableSeats", seatService.findAvailableSeatsByShow(show));
        
        return "show/details";
    }

    @GetMapping("/search")
    public String searchShows(@RequestParam String keyword, Model model) {
        List<Show> shows = showService.searchShows(keyword);
        model.addAttribute("shows", shows);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categories", ShowCategory.values());
        return "show/list";
    }

    @GetMapping("/category/{category}")
    public String showsByCategory(@PathVariable String category, Model model) {
        try {
            ShowCategory showCategory = ShowCategory.valueOf(category.toUpperCase());
            List<Show> shows = showService.findUpcomingShowsByCategory(showCategory);
            model.addAttribute("shows", shows);
            model.addAttribute("currentCategory", showCategory);
            model.addAttribute("categories", ShowCategory.values());
            return "show/list";
        } catch (IllegalArgumentException e) {
            // If category doesn't exist, show all shows
            return "redirect:/shows";
        }
    }
}
