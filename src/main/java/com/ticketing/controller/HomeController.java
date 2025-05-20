package com.ticketing.controller;

import com.ticketing.model.ShowCategory;
import com.ticketing.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ShowService showService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("upcomingShows", showService.findUpcomingShows());
        model.addAttribute("categories", ShowCategory.values());
        return "home";
    }
}
