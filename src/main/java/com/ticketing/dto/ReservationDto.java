package com.ticketing.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {
    
    private Long id;
    
    @NotNull(message = "L'utilisateur est obligatoire")
    private Long userId;
    
    @NotNull(message = "Le spectacle est obligatoire")
    private Long showId;
    
    @NotNull(message = "Le siège est obligatoire")
    private Long seatId;
    
    private String seatNumber;
    
    private String showTitle;
    
    private String showDate;
    
    private String showLocation;
    
    private String totalPrice;
}
