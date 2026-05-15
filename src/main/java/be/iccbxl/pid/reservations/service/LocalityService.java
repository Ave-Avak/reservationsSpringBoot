package be.iccbxl.pid.reservations.service;

import be.iccbxl.pid.reservations.model.Locality;
import be.iccbxl.pid.reservations.repository.LocalityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocalityService {

    private final LocalityRepository localityRepository;

    /**
     * Toutes les localités, triées par code postal.
     */
    public List<Locality> findAll() {
        return localityRepository.findAllByOrderByPostalCodeAscLocalityAsc();
    }

    public Optional<Locality> findById(Long id) {
        return localityRepository.findById(id);
    }

    public Optional<Locality> findByPostalCodeAndLocality(String postalCode, String locality) {
        return localityRepository.findByPostalCodeAndLocality(postalCode, locality);
    }

    /**
     * Crée la localité si elle n'existe pas, sinon renvoie l'existante.
     * Évite les doublons.
     */
    @Transactional
    public Locality findOrCreate(String postalCode, String locality) {
        return localityRepository
                .findByPostalCodeAndLocality(postalCode, locality)
                .orElseGet(() -> localityRepository.save(new Locality(postalCode, locality)));
    }
}