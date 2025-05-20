package com.ticketing.config;

import com.ticketing.model.*;
import com.ticketing.service.SeatService;
import com.ticketing.service.ShowService;
import com.ticketing.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserService userService;
    private final ShowService showService;
    private final SeatService seatService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Initialize sample data for development environment
     * This will only run when the "dev" profile is active
     */
    @Bean
    @Profile("dev")
    public CommandLineRunner initDevData() {
        return args -> {
            // Create admin user
            if (!userService.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword("admin123"); // Will be encoded in the service
                admin.setEmail("admin@example.com");
                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setRole(UserRole.ROLE_ADMIN);
                admin.setActive(true);
                admin.setRegistrationDate(LocalDateTime.now());
                userService.registerUser(admin);
                System.out.println("Admin user created: admin / admin123");
            }

            // Create regular user
            if (!userService.existsByUsername("user")) {
                User user = new User();
                user.setUsername("user");
                user.setPassword("user123"); // Will be encoded in the service
                user.setEmail("user@example.com");
                user.setFirstName("Regular");
                user.setLastName("User");
                user.setRole(UserRole.ROLE_USER);
                user.setActive(true);
                user.setRegistrationDate(LocalDateTime.now());
                userService.registerUser(user);
                System.out.println("Regular user created: user / user123");
            }

            // Create sample shows
            createSampleShows();
        };
    }

    private void createSampleShows() {
        // Sample Show 1: Concert
        if (showService.findAllShows().isEmpty()) {
            Show concert = new Show();
            concert.setTitle("Concert de Jazz");
            concert.setDescription("Un magnifique concert de jazz avec les meilleurs musiciens de la région.");
            concert.setImage("/images/jazz-concert.jpg");
            concert.setDate(LocalDateTime.now().plusDays(15));
            concert.setLocation("Salle Pleyel, Paris");
            concert.setPrice(new BigDecimal("45.00"));
            concert.setCategory(ShowCategory.CONCERT);
            Show savedConcert = showService.createShow(concert);
            seatService.createInitialSeatsForShow(savedConcert, 10, 12);

            // Sample Show 2: Theatre
            Show theatre = new Show();
            theatre.setTitle("Hamlet - William Shakespeare");
            theatre.setDescription("La célèbre pièce de Shakespeare interprétée par la compagnie théâtrale 'Les Lumières'.");
            theatre.setImage("/images/hamlet.jpg");
            theatre.setDate(LocalDateTime.now().plusDays(7));
            theatre.setLocation("Théâtre National, Lyon");
            theatre.setPrice(new BigDecimal("35.50"));
            theatre.setCategory(ShowCategory.THEATRE);
            Show savedTheatre = showService.createShow(theatre);
            seatService.createInitialSeatsForShow(savedTheatre, 8, 10);

            // Sample Show 3: Dance
            Show dance = new Show();
            dance.setTitle("Le Lac des Cygnes - Ballet");
            dance.setDescription("Le ballet classique présenté par le Ballet National de Russie.");
            dance.setImage("/images/swan-lake.jpg");
            dance.setDate(LocalDateTime.now().plusDays(30));
            dance.setLocation("Opéra Garnier, Paris");
            dance.setPrice(new BigDecimal("65.00"));
            dance.setCategory(ShowCategory.DANCE);
            Show savedDance = showService.createShow(dance);
            seatService.createInitialSeatsForShow(savedDance, 12, 15);

            // Sample Show 4: Comedy
            Show comedy = new Show();
            comedy.setTitle("One Man Show - Humour et Société");
            comedy.setDescription("Un spectacle hilarant qui décrypte notre société avec humour et cynisme.");
            comedy.setImage("/images/comedy-show.jpg");
            comedy.setDate(LocalDateTime.now().plusDays(5));
            comedy.setLocation("Le Point Virgule, Paris");
            comedy.setPrice(new BigDecimal("25.00"));
            comedy.setCategory(ShowCategory.COMEDY);
            Show savedComedy = showService.createShow(comedy);
            seatService.createInitialSeatsForShow(savedComedy, 6, 8);

            System.out.println("Sample shows created.");
        }
    }
}
