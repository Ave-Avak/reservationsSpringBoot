package be.iccbxl.pid.reservations.repository;

import be.iccbxl.pid.reservations.model.Reservation;
import be.iccbxl.pid.reservations.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Toutes les réservations d'un user, triées par date de réservation décroissante.
     */
    List<Reservation> findByUserIdOrderByBookingDateDesc(Long userId);

    /**
     * Toutes les réservations pour une representation donnée.
     */
    List<Reservation> findByRepresentationIdOrderByBookingDateAsc(Long representationId);

    /**
     * Réservations d'un user avec un statut donné.
     */
    List<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status);

    /**
     * Compte le nombre total de places réservées (hors annulées) pour une representation.
     * Utile pour vérifier la disponibilité.
     */
    @org.springframework.data.jpa.repository.Query(
        "SELECT COALESCE(SUM(r.places), 0) FROM Reservation r " +
        "WHERE r.representation.id = :representationId " +
        "AND r.status <> 'CANCELLED'"
    )
    Integer countTotalReservedPlaces(@org.springframework.data.repository.query.Param("representationId") Long representationId);
}