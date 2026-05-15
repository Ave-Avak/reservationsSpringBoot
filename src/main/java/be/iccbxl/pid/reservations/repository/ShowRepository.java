package be.iccbxl.pid.reservations.repository;

import be.iccbxl.pid.reservations.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {

    /**
     * Recherche par slug (URL friendly).
     * 🆕 JOIN FETCH pour éviter LazyInit dans le template détail.
     */
    @Query("""
        SELECT s FROM Show s
        JOIN FETCH s.location loc
        JOIN FETCH loc.locality
        WHERE s.slug = :slug
    """)
    Optional<Show> findBySlug(@Param("slug") String slug);

    boolean existsBySlug(String slug);

    /**
     * Spectacles bookables avec leur lieu chargé (catalogue public).
     */
    @Query("""
        SELECT s FROM Show s
        JOIN FETCH s.location loc
        JOIN FETCH loc.locality
        WHERE s.bookable = true
        ORDER BY s.title ASC
    """)
    List<Show> findByBookableTrueOrderByTitleAsc();

    List<Show> findByLocationId(Long locationId);

    /**
     * Tous les shows (admin) avec leur lieu chargé.
     */
    @Query("""
        SELECT s FROM Show s
        JOIN FETCH s.location loc
        JOIN FETCH loc.locality
        ORDER BY s.title ASC
    """)
    List<Show> findAllByOrderByTitleAsc();
}