package com.ticketing.dto;

import com.ticketing.model.ShowCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowDto {
    
    private Long id;
    
    @NotEmpty(message = "Le titre est obligatoire")
    private String title;
    
    @NotEmpty(message = "La description est obligatoire")
    private String description;
    
    private MultipartFile imageFile;
    
    private String image;
    
    @NotNull(message = "La date est obligatoire")
    @Future(message = "La date doit être dans le futur")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime date;
    
    @NotEmpty(message = "Le lieu est obligatoire")
    private String location;
    
    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.01", message = "Le prix doit être supérieur à 0")
    private BigDecimal price;
    
    @NotNull(message = "La catégorie est obligatoire")
    private ShowCategory category;
    
    private int rowCount = 10;
    
    private int seatsPerRow = 12;
}
