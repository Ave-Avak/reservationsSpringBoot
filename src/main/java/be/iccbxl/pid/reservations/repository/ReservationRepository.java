package be.iccbxl.pid.reservations.repository;

import be.iccbxl.pid.reservations.model.Reservation;
import be.iccbxl.pid.reservations.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Toutes les réservations d'un user, triées par date desc.
     * 🆕 JOIN FETCH : on charge en une seule requête la representation,
     * son show et sa location pour éviter LazyInitializationException
     * dans les templates Thymeleaf.
     */
    @Query("""
        SELECT r FROM Reservation r
        JOIN FETCH r.representation rep
        JOIN FETCH rep.show
        JOIN FETCH rep.location
        WHERE r.user.id = :userId
        ORDER BY r.bookingDate DESC
    """)
    List<Reservation> findByUserIdOrderByBookingDateDesc(@Param("userId") Long userId);

    /**
     * Toutes les réservations pour une representation donnée.
     */
    List<Reservation> findByRepresentationIdOrderByBookingDateAsc(Long representationId);

    /**
     * Réservations d'un user avec un statut donné.
     */
    List<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status);

    /**
     * Compte le nombre total de places réservées (hors annulées).
     */
    @Query("""
        SELECT COALESCE(SUM(r.places), 0) FROM Reservation r
        WHERE r.representation.id = :representationId
        AND r.status <> be.iccbxl.pid.reservations.model.ReservationStatus.CANCELLED
    """)
    Integer countTotalReservedPlaces(@Param("representationId") Long representationId);
}