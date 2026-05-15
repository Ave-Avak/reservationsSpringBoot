package be.iccbxl.pid.reservations.repository;

import be.iccbxl.pid.reservations.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    /**
     * Recherche par slug (URL friendly).
     * Ex: /locations/espace-delvaux
     */
    Optional<Location> findBySlug(String slug);

    /**
     * Vérifie l'unicité du slug avant insertion.
     */
    boolean existsBySlug(String slug);

    /**
     * Toutes les locations d'une locality donnée.
     */
    List<Location> findByLocalityId(Long localityId);

    /**
     * Toutes les locations triées par désignation.
     */
    List<Location> findAllByOrderByDesignationAsc();
}