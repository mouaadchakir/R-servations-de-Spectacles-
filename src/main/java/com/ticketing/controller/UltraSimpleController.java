package com.ticketing.controller;

import com.ticketing.model.Reservation;
import com.ticketing.model.Seat;
import com.ticketing.model.Show;
import com.ticketing.model.User;
import com.ticketing.service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/ultra-simple")
public class UltraSimpleController {

    @Autowired
    private ShowService showService;

    @GetMapping("/reserve/{showId}")
    public String showStaticReservationPage(@PathVariable String showId, Model model) {
        // Always add the show ID to the model
        model.addAttribute("showId", showId);
        
        try {
            try {
                // Récupérer le spectacle réel depuis la base de données
                Show show = showService.findById(Long.valueOf(showId));
                
                // Utiliser les données réelles du spectacle
                model.addAttribute("show", show);
            } catch (Exception e) {
                // Si le spectacle n'existe pas, créer un mock avec l'ID fourni
                Show mockShow = new Show();
                mockShow.setId(Long.valueOf(showId));
                mockShow.setTitle("Spectacle #" + showId);
                mockShow.setLocation("Salle Principale");
                mockShow.setDate(LocalDateTime.now().plusDays(7));
                mockShow.setPrice(new BigDecimal("100.00")); // Prix par défaut
                
                model.addAttribute("show", mockShow);
                model.addAttribute("warningMessage", "Ce spectacle n'existe pas dans notre base de données, mais vous pouvez continuer avec des données simulées.");
            }
        } catch (Exception e) {
            // Si une erreur se produit, on continue quand même
            model.addAttribute("errorMessage", "Information limitée disponible: " + e.getMessage());
        }
        
        return "reservation/ultra-simple-create";
    }
    
    @PostMapping("/confirm/{showId}")
    public String processStaticReservation(@PathVariable String showId, RedirectAttributes redirectAttributes) {
        try {
            try {
                // Récupérer le spectacle réel depuis la base de données
                Show show = showService.findById(Long.valueOf(showId));
                
                // Créer un code de réservation
                String reservationCode = "T" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                
                // Ajouter les attributs de redirection
                redirectAttributes.addFlashAttribute("reservationCode", reservationCode);
                redirectAttributes.addFlashAttribute("showId", showId);
                redirectAttributes.addFlashAttribute("price", show.getPrice());
                redirectAttributes.addFlashAttribute("showTitle", show.getTitle());
            } catch (Exception e) {
                // Si le spectacle n'est pas trouvé, utiliser des valeurs par défaut
                String reservationCode = "T" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                redirectAttributes.addFlashAttribute("reservationCode", reservationCode);
                redirectAttributes.addFlashAttribute("showId", showId);
                redirectAttributes.addFlashAttribute("price", new BigDecimal("100.00"));
                redirectAttributes.addFlashAttribute("showTitle", "Spectacle #" + showId);
                redirectAttributes.addFlashAttribute("warningMessage", "Ce spectacle n'existe pas dans notre base de données, mais vous pouvez continuer avec des données simulées.");
            }
            
            // Rediriger vers la page de paiement
            return "redirect:/ultra-simple/payment";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur de réservation: " + e.getMessage());
            return "redirect:/ultra-simple/reserve/" + showId;
        }
    }
    
    @GetMapping("/payment")
    public String showStaticPaymentPage(Model model) {
        // Pour s'assurer que le prix est toujours disponible
        if (!model.containsAttribute("price")) {
            // Essayer de récupérer l'ID du spectacle
            if (model.containsAttribute("showId")) {
                try {
                    Long showId = Long.valueOf(model.getAttribute("showId").toString());
                    Show show = showService.findById(showId);
                    model.addAttribute("price", show.getPrice());
                } catch (Exception e) {
                    // Ne rien faire si impossible de récupérer le prix
                }
            }
        }
        return "payment/ultra-simple-checkout";
    }
    
    @PostMapping("/payment/confirm")
    public String confirmStaticPayment(Model model, RedirectAttributes redirectAttributes,
                                     @RequestParam(name = "price", required = false) String priceParam) {
        // Create confirmation code
        String confirmationCode = "C" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Ru00e9cupu00e9rer les attributs du modu00e8le
        Object priceObj = model.getAttribute("price");
        Object showIdObj = model.getAttribute("showId");
        Object showTitleObj = model.getAttribute("showTitle");
        
        // Ru00e9cupu00e9rer le prix du formulaire si disponible, sinon du modu00e8le
        String price = priceParam != null ? priceParam : 
                    (priceObj != null ? priceObj.toString() : null);
        
        String showId = showIdObj != null ? showIdObj.toString() : null;
        String showTitle = showTitleObj != null ? showTitleObj.toString() : null;
        
        // Ajouter le prix comme paramu00e8tre d'URL pour garantir qu'il soit transmis exactement tel quel
        return "redirect:/ultra-simple/confirmation?confirmationCode=" + confirmationCode + 
               (showId != null ? "&showId=" + showId : "") + 
               (price != null ? "&price=" + price : "") + 
               (showTitle != null ? "&showTitle=" + showTitle : "");
    }
    
    @GetMapping("/confirmation")
    public String showStaticConfirmationPage(Model model, @RequestParam(required = false) String confirmationCode,
                                          @RequestParam(required = false) String showId,
                                          @RequestParam(required = false) String price,
                                          @RequestParam(required = false) String showTitle) {
        // Utiliser les paramètres d'URL s'ils sont disponibles
        if (confirmationCode != null) {
            model.addAttribute("confirmationCode", confirmationCode);
        } else if (!model.containsAttribute("confirmationCode")) {
            // Valeur par défaut uniquement si nécessaire
            model.addAttribute("confirmationCode", "DÉMO123456");
        }
        
        // Gérer l'ID du spectacle
        if (showId != null) {
            model.addAttribute("showId", showId);
        }
        
        // Gérer le titre du spectacle
        if (showTitle != null) {
            model.addAttribute("showTitle", showTitle);
        } else if (!model.containsAttribute("showTitle")) {
            model.addAttribute("showTitle", "Spectacle");
        }
        
        // Toujours essayer de ru00e9cupu00e9rer les donnu00e9es ru00e9elles du ticket depuis la base de donnu00e9es
        // Prioritu00e9 1: Si on a un ID de spectacle, ru00e9cupu00e9rer toutes les donnu00e9es directement de la base de donnu00e9es
        if (showId != null) {
            try {
                Long showIdLong = Long.valueOf(showId);
                Show show = showService.findById(showIdLong);
                
                // Ru00e9cupu00e9rer et utiliser toutes les donnu00e9es ru00e9elles du spectacle
                model.addAttribute("show", show);  // Le spectacle complet pour avoir toutes les donnu00e9es
                model.addAttribute("price", show.getPrice().toString());
                model.addAttribute("showTitle", show.getTitle());
                model.addAttribute("location", show.getLocation());
                model.addAttribute("date", show.getDate());
                
                // Ru00e9cupu00e9rer u00e9galement les places disponibles pour information
                try {
                    // Liste des places disponibles
                    model.addAttribute("hasDetails", true);
                } catch (Exception e) {
                    // Si impossible de ru00e9cupu00e9rer les places, ignorer silencieusement
                }
            } catch (Exception e) {
                // En cas d'erreur, utiliser les valeurs de l'URL si disponibles
                if (price != null) {
                    model.addAttribute("price", price);
                } else {
                    model.addAttribute("price", "Non spu00e9cifiu00e9");
                }
            }
        } 
        // Prioritu00e9 2: Si on n'a pas d'ID mais on a un prix dans l'URL, l'utiliser
        else if (price != null) {
            model.addAttribute("price", price);
        }
        // Prioritu00e9 3: Si on n'a aucune information, indiquer clairement que c'est une du00e9mo
        else {
            model.addAttribute("price", "Démo");
        }
        
        return "payment/ultra-simple-confirmation";
    }
}
