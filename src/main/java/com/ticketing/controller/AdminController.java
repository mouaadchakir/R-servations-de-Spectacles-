package com.ticketing.controller;

import com.ticketing.model.Show;
import com.ticketing.model.ShowCategory;
import com.ticketing.service.SeatService;
import com.ticketing.service.ShowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ShowService showService;
    private final SeatService seatService;

    @GetMapping("/shows")
    public String listShows(Model model) {
        model.addAttribute("shows", showService.findAllShows());
        return "admin/shows";
    }

    @GetMapping("/shows/create")
    public String showCreateForm(Model model) {
        model.addAttribute("show", new Show());
        model.addAttribute("categories", ShowCategory.values());
        return "admin/show-form";
    }

    @PostMapping("/shows/create")
    public String createShow(@Valid @ModelAttribute Show show, 
                            BindingResult result,
                            @RequestParam("imageFile") MultipartFile imageFile,
                            @RequestParam("rowCount") int rowCount,
                            @RequestParam("seatsPerRow") int seatsPerRow,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("categories", ShowCategory.values());
            return "admin/show-form";
        }
        
        // Handle image upload if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Create upload directory if it doesn't exist
                Path uploadDir = Paths.get("src/main/resources/static/uploads");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }
                
                // Create unique filename
                String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                Path filePath = uploadDir.resolve(filename);
                
                // Save file
                Files.copy(imageFile.getInputStream(), filePath);
                
                // Set image path in show object
                show.setImage("/uploads/" + filename);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Failed to upload image: " + e.getMessage());
                model.addAttribute("categories", ShowCategory.values());
                return "admin/show-form";
            }
        }
        
        // Save the show
        Show savedShow = showService.createShow(show);
        
        // Create seats for the show
        seatService.createInitialSeatsForShow(savedShow, rowCount, seatsPerRow);
        
        redirectAttributes.addFlashAttribute("success", "Show created successfully");
        return "redirect:/admin/shows";
    }

    @GetMapping("/shows/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Show show = showService.findById(id);
        model.addAttribute("show", show);
        model.addAttribute("categories", ShowCategory.values());
        return "admin/show-form";
    }

    @PostMapping("/shows/{id}/edit")
    public String updateShow(@PathVariable Long id,
                            @Valid @ModelAttribute Show show,
                            BindingResult result,
                            @RequestParam("imageFile") MultipartFile imageFile,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("categories", ShowCategory.values());
            return "admin/show-form";
        }
        
        // Ensure we're updating the correct show
        show.setId(id);
        
        // Handle image upload if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Create upload directory if it doesn't exist
                Path uploadDir = Paths.get("src/main/resources/static/uploads");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }
                
                // Create unique filename
                String filename = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                Path filePath = uploadDir.resolve(filename);
                
                // Save file
                Files.copy(imageFile.getInputStream(), filePath);
                
                // Set image path in show object
                show.setImage("/uploads/" + filename);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Failed to upload image: " + e.getMessage());
                model.addAttribute("categories", ShowCategory.values());
                return "admin/show-form";
            }
        } else {
            // Keep existing image if no new one is uploaded
            Show existingShow = showService.findById(id);
            show.setImage(existingShow.getImage());
        }
        
        // Update the show
        showService.updateShow(show);
        
        redirectAttributes.addFlashAttribute("success", "Show updated successfully");
        return "redirect:/admin/shows";
    }

    @PostMapping("/shows/{id}/delete")
    public String deleteShow(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            showService.deleteShow(id);
            redirectAttributes.addFlashAttribute("success", "Show deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete show: " + e.getMessage());
        }
        return "redirect:/admin/shows";
    }
}
