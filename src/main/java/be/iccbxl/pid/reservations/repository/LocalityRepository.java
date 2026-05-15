package be.iccbxl.pid.reservations.repository;

import be.iccbxl.pid.reservations.model.Locality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocalityRepository extends JpaRepository<Locality, Long> {

    /**
     * Recherche d'une localité par son code postal exact.
     * Plusieurs résultats possibles si un code postal a plusieurs villes.
     */
    List<Locality> findByPostalCode(String postalCode);

    /**
     * Recherche exacte sur le couple (postal_code, locality).
     */
    Optional<Locality> findByPostalCodeAndLocality(String postalCode, String locality);

    /**
     * Toutes les localités triées par code postal puis ville (utile pour dropdowns).
     */
    List<Locality> findAllByOrderByPostalCodeAscLocalityAsc();
}