package be.iccbxl.pid.reservations.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Représente une réservation faite par un user pour une representation.
 *
 * Entité-association entre User et Representation, avec en plus :
 *  - places       : quantité de places réservées
 *  - bookingDate  : date de la réservation
 *  - status       : statut (PENDING, CONFIRMED, CANCELLED, REFUNDED)
 *
 * Un même user peut avoir PLUSIEURS reservations pour la même representation
 * (ex : ajouter des places plus tard).
 */
@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User qui a fait la réservation.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Représentation réservée.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "representation_id", nullable = false)
    private Representation representation;

    /**
     * Nombre de places réservées (minimum 1).
     */
    @NotNull
    @Min(value = 1, message = "Au moins 1 place doit être réservée.")
    @Column(name = "places", nullable = false)
    private Integer places = 1;

    /**
     * Date et heure de la réservation.
     */
    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;

    /**
     * Statut de la réservation.
     * EnumType.STRING : stocké en BDD comme "PENDING" plutôt que comme 0/1/2...
     * (plus lisible, robuste si on ajoute des statuts).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReservationStatus status = ReservationStatus.PENDING;

    // =============================================================
    // CONSTRUCTEUR MÉTIER
    // =============================================================

    public Reservation(User user, Representation representation, Integer places) {
        setUser(user);
        setRepresentation(representation);
        this.places = places;
        this.bookingDate = LocalDateTime.now();
        this.status = ReservationStatus.PENDING;
    }

    // =============================================================
    // SETTERS INTELLIGENTS (synchronisation bilatérale)
    // =============================================================

    public void setUser(User newUser) {
        if (this.user != null) {
            this.user.getReservations().remove(this);
        }
        this.user = newUser;
        if (newUser != null && !newUser.getReservations().contains(this)) {
            newUser.getReservations().add(this);
        }
    }

    public void setRepresentation(Representation newRepresentation) {
        if (this.representation != null) {
            this.representation.getReservations().remove(this);
        }
        this.representation = newRepresentation;
        if (newRepresentation != null && !newRepresentation.getReservations().contains(this)) {
            newRepresentation.getReservations().add(this);
        }
    }

    // =============================================================
    // MÉTHODES MÉTIER
    // =============================================================

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }

    public boolean isPending() {
        return status == ReservationStatus.PENDING;
    }

    public boolean isConfirmed() {
        return status == ReservationStatus.CONFIRMED;
    }

    public boolean isCancelled() {
        return status == ReservationStatus.CANCELLED;
    }

    // =============================================================
    // EQUALS / HASHCODE
    // =============================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reservation)) return false;
        Reservation r = (Reservation) o;
        return id != null && id.equals(r.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}