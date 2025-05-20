package com.ticketing.util;

import com.ticketing.dto.ReservationDto;
import com.ticketing.dto.ShowDto;
import com.ticketing.dto.UserRegistrationDto;
import com.ticketing.model.Reservation;
import com.ticketing.model.Show;
import com.ticketing.model.User;
import com.ticketing.model.UserRole;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class DtoConverter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Convert UserRegistrationDto to User entity
     */
    public User convertToUser(UserRegistrationDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword()); // Will be encoded in service
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setRole(UserRole.ROLE_USER); // Default role for registration
        user.setActive(true);
        return user;
    }

    /**
     * Convert ShowDto to Show entity
     */
    public Show convertToShow(ShowDto dto) {
        Show show = new Show();
        show.setId(dto.getId());
        show.setTitle(dto.getTitle());
        show.setDescription(dto.getDescription());
        show.setImage(dto.getImage());
        show.setDate(dto.getDate());
        show.setLocation(dto.getLocation());
        show.setPrice(dto.getPrice());
        show.setCategory(dto.getCategory());
        return show;
    }

    /**
     * Convert Show entity to ShowDto
     */
    public ShowDto convertToShowDto(Show show) {
        ShowDto dto = new ShowDto();
        dto.setId(show.getId());
        dto.setTitle(show.getTitle());
        dto.setDescription(show.getDescription());
        dto.setImage(show.getImage());
        dto.setDate(show.getDate());
        dto.setLocation(show.getLocation());
        dto.setPrice(show.getPrice());
        dto.setCategory(show.getCategory());
        return dto;
    }

    /**
     * Convert Reservation to ReservationDto
     */
    public ReservationDto convertToReservationDto(Reservation reservation) {
        ReservationDto dto = new ReservationDto();
        dto.setId(reservation.getId());
        dto.setUserId(reservation.getUser().getId());
        dto.setShowId(reservation.getShow().getId());
        dto.setSeatId(reservation.getSeat().getId());
        dto.setSeatNumber(reservation.getSeat().getSeatNumber());
        dto.setShowTitle(reservation.getShow().getTitle());
        dto.setShowDate(reservation.getShow().getDate().format(DATE_FORMATTER));
        dto.setShowLocation(reservation.getShow().getLocation());
        dto.setTotalPrice(reservation.getTotalPrice().toString());
        return dto;
    }
}
