package com.ticketing.config;

import com.ticketing.model.User;
import com.ticketing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Classe qui ajoute automatiquement certains attributs au modèle pour toutes les pages
 */
@Configuration
@ControllerAdvice
public class GlobalModelAttributesConfig {

    @Autowired
    private UserService userService;

    /**
     * Ajoute l'attribut de photo de profil pour qu'il soit disponible sur toutes les pages
     */
    @ModelAttribute("profileImage")
    public String getProfileImage() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                User user = userService.findByUsername(auth.getName());
                if (user != null && user.getProfileImagePath() != null && !user.getProfileImagePath().isEmpty()) {
                    return user.getProfileImagePath();
                }
            }
        } catch (Exception e) {
            // En cas d'erreur, ne pas bloquer le rendu de la page
        }
        return null;
    }
    
    /**
     * Ajoute les informations de nom et prénom pour qu'elles soient disponibles sur toutes les pages
     */
    @ModelAttribute("userFullName")
    public String getUserFullName() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                User user = userService.findByUsername(auth.getName());
                if (user != null) {
                    String firstName = user.getFirstName();
                    String lastName = user.getLastName();
                    if (firstName != null && !firstName.isEmpty() && lastName != null && !lastName.isEmpty()) {
                        return firstName + " " + lastName;
                    }
                }
            }
        } catch (Exception e) {
            // En cas d'erreur, ne pas bloquer le rendu de la page
        }
        return null;
    }
}
