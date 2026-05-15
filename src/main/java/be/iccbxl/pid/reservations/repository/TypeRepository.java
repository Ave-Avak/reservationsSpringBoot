package be.iccbxl.pid.reservations.repository;

import be.iccbxl.pid.reservations.model.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeRepository extends JpaRepository<Type, Long> {

    /**
     * Recherche d'un type par son libellé exact.
     * Utile pour vérifier l'existence avant insertion.
     */
    Optional<Type> findByType(String type);

    /**
     * Vérifie si un type avec ce libellé existe déjà.
     */
    boolean existsByType(String type);
}
