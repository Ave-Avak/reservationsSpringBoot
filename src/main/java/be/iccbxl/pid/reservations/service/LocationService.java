package be.iccbxl.pid.reservations.service;

import be.iccbxl.pid.reservations.model.Location;
import be.iccbxl.pid.reservations.model.Locality;
import be.iccbxl.pid.reservations.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService {

    private final LocationRepository locationRepository;

    public List<Location> findAll() {
        return locationRepository.findAllByOrderByDesignationAsc();
    }

    public Optional<Location> findById(Long id) {
        return locationRepository.findById(id);
    }

    public Optional<Location> findBySlug(String slug) {
        return locationRepository.findBySlug(slug);
    }

    public List<Location> findByLocality(Long localityId) {
        return locationRepository.findByLocalityId(localityId);
    }

    /**
     * Crée une location avec sa locality, sécurité contre doublons de slug.
     */
    @Transactional
    public Location create(String designation, String address, Locality locality) {
        Location location = new Location(designation, address, locality);
        // Vérification d'unicité du slug (cas rare où 2 désignations donnent le même slug)
        if (locationRepository.existsBySlug(location.getSlug())) {
            throw new IllegalArgumentException(
                "Un lieu avec un nom similaire existe déjà (slug : " + location.getSlug() + ")"
            );
        }
        return locationRepository.save(location);
    }

    @Transactional
    public Location save(Location location) {
        return locationRepository.save(location);
    }

    @Transactional
    public void deleteById(Long id) {
        locationRepository.deleteById(id);
    }
}