package be.iccbxl.pid.reservations.repository;

import be.iccbxl.pid.reservations.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {

    /**
     * Recherche par slug (URL friendly).
     * Ex: /shows/ayiti
     */
    Optional<Show> findBySlug(String slug);

    /**
     * Vérifie l'unicité du slug avant insertion.
     */
    boolean existsBySlug(String slug);

    /**
     * Tous les spectacles bookables, triés par titre.
     * Utile pour la page publique catalogue.
     */
    List<Show> findByBookableTrueOrderByTitleAsc();

    /**
     * Tous les spectacles d'un lieu donné.
     */
    List<Show> findByLocationId(Long locationId);

    /**
     * Tous les spectacles triés par titre.
     */
    List<Show> findAllByOrderByTitleAsc();
}