package be.iccbxl.pid.reservations.service;

import be.iccbxl.pid.reservations.model.Location;
import be.iccbxl.pid.reservations.model.Show;
import be.iccbxl.pid.reservations.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShowService {

    private final ShowRepository showRepository;

    /**
     * Tous les spectacles (admin).
     */
    public List<Show> findAll() {
        return showRepository.findAllByOrderByTitleAsc();
    }

    /**
     * Catalogue public : uniquement les spectacles bookables.
     */
    public List<Show> findAllBookable() {
        return showRepository.findByBookableTrueOrderByTitleAsc();
    }

    public Optional<Show> findById(Long id) {
        return showRepository.findById(id);
    }

    public Optional<Show> findBySlug(String slug) {
        return showRepository.findBySlug(slug);
    }

    public List<Show> findByLocation(Long locationId) {
        return showRepository.findByLocationId(locationId);
    }

    /**
     * Crée un nouveau spectacle. Vérification d'unicité du slug.
     */
    @Transactional
    public Show create(String title, String description, Location location, Short duration, Year createdIn) {
        Show show = new Show(title, description, location, duration, createdIn);
        if (showRepository.existsBySlug(show.getSlug())) {
            throw new IllegalArgumentException(
                "Un spectacle avec un titre similaire existe déjà (slug : " + show.getSlug() + ")"
            );
        }
        return showRepository.save(show);
    }

    @Transactional
    public Show save(Show show) {
        return showRepository.save(show);
    }

    @Transactional
    public void deleteById(Long id) {
        showRepository.deleteById(id);
    }
}