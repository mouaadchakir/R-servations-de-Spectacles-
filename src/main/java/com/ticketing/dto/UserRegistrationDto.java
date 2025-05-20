package com.ticketing.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDto {
    
    @NotEmpty(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    private String username;
    
    @NotEmpty(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;
    
    @NotEmpty(message = "La confirmation du mot de passe est obligatoire")
    private String confirmPassword;
    
    @NotEmpty(message = "L'email est obligatoire")
    @Email(message = "Veuillez fournir un email valide")
    private String email;
    
    @NotEmpty(message = "Le prénom est obligatoire")
    private String firstName;
    
    @NotEmpty(message = "Le nom est obligatoire")
    private String lastName;
}
