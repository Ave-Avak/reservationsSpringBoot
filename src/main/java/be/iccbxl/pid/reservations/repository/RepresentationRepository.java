package be.iccbxl.pid.reservations.repository;

import be.iccbxl.pid.reservations.model.Representation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RepresentationRepository extends JpaRepository<Representation, Long> {

    /**
     * Toutes les représentations d'un spectacle, triées par date.
     */
    List<Representation> findByShowIdOrderByScheduleAsc(Long showId);

    /**
     * Toutes les représentations dans un lieu, triées par date.
     */
    List<Representation> findByLocationIdOrderByScheduleAsc(Long locationId);

    /**
     * Toutes les représentations à venir (date > maintenant), triées par date.
     */
    List<Representation> findByScheduleAfterOrderByScheduleAsc(LocalDateTime now);

    /**
     * Représentations à venir d'un spectacle spécifique.
     */
    List<Representation> findByShowIdAndScheduleAfterOrderByScheduleAsc(Long showId, LocalDateTime now);

    /**
     * Toutes les représentations, triées par date.
     */
    List<Representation> findAllByOrderByScheduleAsc();
}