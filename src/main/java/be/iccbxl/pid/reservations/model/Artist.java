package be.iccbxl.pid.reservations.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "artists")
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le prénom ne peut pas être vide.")
    @Size(min = 2, max = 60, message = "Le prénom doit contenir entre 2 et 60 caractères.")
    private String firstname;

    @NotBlank(message = "Le nom ne peut pas être vide.")
    @Size(min = 2, max = 60, message = "Le nom doit contenir entre 2 et 60 caractères.")
    private String lastname;

    @Override
    public String toString() {
        return firstname + " " + lastname;
    }
}