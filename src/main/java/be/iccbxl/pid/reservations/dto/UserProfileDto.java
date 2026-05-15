package be.iccbxl.pid.reservations.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserProfileDto {

    @NotBlank(message = "L'email est obligatoire.")
    @Email(message = "L'email doit être valide.")
    @Size(max = 120, message = "L'email ne peut pas dépasser 120 caractères.")
    private String email;

    @Size(max = 60, message = "Le prénom ne peut pas dépasser 60 caractères.")
    private String firstname;

    @Size(max = 60, message = "Le nom ne peut pas dépasser 60 caractères.")
    private String lastname;
}