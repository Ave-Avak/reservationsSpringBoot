package be.iccbxl.pid.reservations.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO du formulaire de réservation.
 * Validation des champs avant d'aller toucher l'entité.
 */
@Data
@NoArgsConstructor
public class ReservationFormDto {

    @NotNull(message = "L'identifiant de la représentation est obligatoire.")
    private Long representationId;

    @NotNull(message = "Le nombre de places est obligatoire.")
    @Min(value = 1, message = "Au moins 1 place doit être réservée.")
    @Max(value = 10, message = "Maximum 10 places par réservation.")
    private Integer places = 1;
}