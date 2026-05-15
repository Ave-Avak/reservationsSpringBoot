package be.iccbxl.pid.reservations.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import be.iccbxl.pid.reservations.validation.PasswordMatches;

@Data
@NoArgsConstructor
@PasswordMatches
public class UserRegistrationDto {

    @NotBlank(message = "Le nom d'utilisateur est obligatoire.")
    @Size(min = 3, max = 60, message = "Le nom d'utilisateur doit contenir entre 3 et 60 caractères.")
    private String login;

    @NotBlank(message = "L'email est obligatoire.")
    @Email(message = "L'email doit être valide.")
    @Size(max = 120, message = "L'email ne peut pas dépasser 120 caractères.")
    private String email;

    @Size(max = 60, message = "Le prénom ne peut pas dépasser 60 caractères.")
    private String firstname;

    @Size(max = 60, message = "Le nom ne peut pas dépasser 60 caractères.")
    private String lastname;

    @NotBlank(message = "Le mot de passe est obligatoire.")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères.")
    private String password;

    @NotBlank(message = "La confirmation du mot de passe est obligatoire.")
    private String passwordConfirm;
}