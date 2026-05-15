package be.iccbxl.pid.reservations.repository;

import be.iccbxl.pid.reservations.model.Representation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface RepresentationRepository extends JpaRepository<Representation, Long> {

    /**
     * Toutes les représentations d'un spectacle, avec leur location et locality chargées.
     */
    @Query("""
        SELECT r FROM Representation r
        JOIN FETCH r.location loc
        JOIN FETCH loc.locality
        WHERE r.show.id = :showId
        ORDER BY r.schedule ASC
    """)
    List<Representation> findByShowIdOrderByScheduleAsc(@Param("showId") Long showId);

    /**
     * Représentations d'un lieu, triées par date.
     */
    @Query("""
        SELECT r FROM Representation r
        JOIN FETCH r.show
        JOIN FETCH r.location loc
        JOIN FETCH loc.locality
        WHERE r.location.id = :locationId
        ORDER BY r.schedule ASC
    """)
    List<Representation> findByLocationIdOrderByScheduleAsc(@Param("locationId") Long locationId);

    /**
     * Toutes les représentations à venir, triées par date.
     */
    @Query("""
        SELECT r FROM Representation r
        JOIN FETCH r.show
        JOIN FETCH r.location loc
        JOIN FETCH loc.locality
        WHERE r.schedule > :now
        ORDER BY r.schedule ASC
    """)
    List<Representation> findByScheduleAfterOrderByScheduleAsc(@Param("now") LocalDateTime now);

    /**
     * 🎯 LA MÉTHODE QUI POSAIT PROBLÈME :
     * Représentations à venir d'un spectacle spécifique, avec location/locality chargées.
     */
    @Query("""
        SELECT r FROM Representation r
        JOIN FETCH r.location loc
        JOIN FETCH loc.locality
        WHERE r.show.id = :showId
        AND r.schedule > :now
        ORDER BY r.schedule ASC
    """)
    List<Representation> findByShowIdAndScheduleAfterOrderByScheduleAsc(
        @Param("showId") Long showId,
        @Param("now") LocalDateTime now
    );

    /**
     * Toutes les représentations, triées par date.
     */
    @Query("""
        SELECT r FROM Representation r
        JOIN FETCH r.show
        JOIN FETCH r.location loc
        JOIN FETCH loc.locality
        ORDER BY r.schedule ASC
    """)
    List<Representation> findAllByOrderByScheduleAsc();

    /**
     * Récupère une représentation avec show et location chargés (pour le formulaire de réservation).
     */
    @Query("""
        SELECT r FROM Representation r
        JOIN FETCH r.show
        JOIN FETCH r.location loc
        JOIN FETCH loc.locality
        WHERE r.id = :id
    """)
    Optional<Representation> findByIdWithDetails(@Param("id") Long id);
}