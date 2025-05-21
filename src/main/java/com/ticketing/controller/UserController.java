package com.ticketing.controller;

import com.ticketing.model.User;
import com.ticketing.service.FileStorageService;
import com.ticketing.service.ReservationService;
import com.ticketing.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ReservationService reservationService;
    private final FileStorageService fileStorageService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "user/register";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("user") User user, 
                                     BindingResult result, 
                                     RedirectAttributes redirectAttributes) {
        // Check if username exists
        if (userService.existsByUsername(user.getUsername())) {
            result.rejectValue("username", "error.user", "Username already exists");
        }

        // Check if email exists
        if (userService.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "error.user", "Email already exists");
        }

        if (result.hasErrors()) {
            return "user/register";
        }

        userService.registerUser(user);
        redirectAttributes.addFlashAttribute("success", "Registration successful! You can now log in.");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "user/login";
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        try {
            // Get current authenticated user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            // Add username to model (minimal required info)
            model.addAttribute("username", username);
            
            try {
                // Get user info safely, one attribute at a time
                User user = userService.findByUsername(username);
                
                // Add individual attributes to the model instead of the whole user object
                model.addAttribute("email", user.getEmail());
                model.addAttribute("bio", user.getBio());
                model.addAttribute("firstName", user.getFirstName());
                model.addAttribute("lastName", user.getLastName());
                
                // Add profile image path if it exists
                if (user.getProfileImagePath() != null && !user.getProfileImagePath().isEmpty()) {
                    model.addAttribute("profileImage", user.getProfileImagePath());
                }
                
                // Format the date safely
                if (user.getRegistrationDate() != null) {
                    model.addAttribute("registrationDate", 
                        user.getRegistrationDate().format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy")));
                } else {
                    model.addAttribute("registrationDate", "Non disponible");
                }
            } catch (Exception e) {
                // La recherche d'utilisateur a échoué, mais nous pouvons toujours afficher la page
                // avec des informations minimales
                model.addAttribute("errorMessage", "Impossible de récupérer toutes vos informations.");
                e.printStackTrace();
            }
            
            // Retourner notre nouvelle page de profil simplifiée
            return "user/ultra-simple-profile";
            
        } catch (Exception ex) {
            // Échec critique - revenir à un affichage minimal absolu
            model.addAttribute("username", "Utilisateur");
            model.addAttribute("errorMessage", "Une erreur s'est produite lors du chargement de votre profil.");
            ex.printStackTrace();
            return "user/ultra-simple-profile";
        }
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("email") String email,
                               @RequestParam(value = "firstName", required = false) String firstName,
                               @RequestParam(value = "lastName", required = false) String lastName,
                               @RequestParam(value = "bio", required = false) String bio,
                               @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                               RedirectAttributes redirectAttributes) {
        try {
            // Get current authenticated user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findByUsername(auth.getName());
            
            // Check for email uniqueness (if changed)
            if (!currentUser.getEmail().equals(email) && userService.existsByEmail(email)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Cet email est déjà utilisé par un autre compte");
                return "redirect:/profile";
            }
            
            // Update the user's information
            currentUser.setEmail(email);
            currentUser.setBio(bio);
            
            // Update firstName and lastName if provided
            if (firstName != null && !firstName.trim().isEmpty()) {
                currentUser.setFirstName(firstName);
            }
            
            if (lastName != null && !lastName.trim().isEmpty()) {
                currentUser.setLastName(lastName);
            }
            
            // Process the profile image if provided
            if (profileImage != null && !profileImage.isEmpty()) {
                try {
                    // Delete old image if it exists
                    if (currentUser.getProfileImagePath() != null && !currentUser.getProfileImagePath().isEmpty()) {
                        fileStorageService.deleteFile(currentUser.getProfileImagePath());
                    }
                    
                    // Save the new image
                    String imagePath = fileStorageService.storeFile(profileImage, "profiles");
                    currentUser.setProfileImagePath(imagePath);
                } catch (IOException e) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Impossible de télécharger l'image: " + e.getMessage());
                    return "redirect:/profile";
                }
            }
            
            // Save the updated user
            userService.updateUser(currentUser);
            
            redirectAttributes.addFlashAttribute("successMessage", "Profil mis à jour avec succès");
            return "redirect:/profile";
        } catch (Exception ex) {
            // Log any errors
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Une erreur s'est produite lors de la mise à jour de votre profil: " + ex.getMessage());
            return "redirect:/profile";
        }
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        return "user/change-password";
    }

    @PostMapping("/change-password")
    public String processChangePassword(String currentPassword, String newPassword, String confirmPassword,
                                       RedirectAttributes redirectAttributes) {
        // Get current authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        
        // Validate password match
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Les nouveaux mots de passe ne correspondent pas");
            return "redirect:/change-password";
        }
        
        // Attempt to change password
        boolean changed = userService.changePassword(user.getId(), currentPassword, newPassword);
        if (!changed) {
            redirectAttributes.addFlashAttribute("error", "Le mot de passe actuel est incorrect");
            return "redirect:/change-password";
        }
        
        redirectAttributes.addFlashAttribute("success", "Mot de passe modifié avec succès");
        return "redirect:/profile";
    }
}
