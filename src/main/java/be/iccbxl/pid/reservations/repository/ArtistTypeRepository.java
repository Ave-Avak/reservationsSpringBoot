package be.iccbxl.pid.reservations.repository;

import be.iccbxl.pid.reservations.model.ArtistType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistTypeRepository extends JpaRepository<ArtistType, Long> {

    /**
     * Récupère un ArtistType pour un couple (artiste, type) donné.
     */
    Optional<ArtistType> findByArtistIdAndTypeId(Long artistId, Long typeId);

    /**
     * Tous les ArtistType d'un artiste.
     */
    List<ArtistType> findByArtistId(Long artistId);

    /**
     * Tous les ArtistType ayant un type donné.
     */
    List<ArtistType> findByTypeId(Long typeId);
}