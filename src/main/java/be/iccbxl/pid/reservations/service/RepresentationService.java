package be.iccbxl.pid.reservations.service;

import be.iccbxl.pid.reservations.model.Location;
import be.iccbxl.pid.reservations.model.Representation;
import be.iccbxl.pid.reservations.model.Show;
import be.iccbxl.pid.reservations.repository.RepresentationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RepresentationService {

    private final RepresentationRepository representationRepository;

    public List<Representation> findAll() {
        return representationRepository.findAllByOrderByScheduleAsc();
    }

    public Optional<Representation> findById(Long id) {
        return representationRepository.findById(id);
    }

    /**
     * Représentations d'un spectacle, triées par date.
     */
    public List<Representation> findByShow(Long showId) {
        return representationRepository.findByShowIdOrderByScheduleAsc(showId);
    }

    /**
     * Représentations à venir d'un spectacle (filtre les dates passées).
     */
    public List<Representation> findUpcomingByShow(Long showId) {
        return representationRepository
            .findByShowIdAndScheduleAfterOrderByScheduleAsc(showId, LocalDateTime.now());
    }

    /**
     * Représentations dans un lieu, triées par date.
     */
    public List<Representation> findByLocation(Long locationId) {
        return representationRepository.findByLocationIdOrderByScheduleAsc(locationId);
    }

    /**
     * Toutes les représentations à venir, triées par date.
     */
    public List<Representation> findAllUpcoming() {
        return representationRepository.findByScheduleAfterOrderByScheduleAsc(LocalDateTime.now());
    }

    @Transactional
    public Representation create(Show show, Location location, LocalDateTime schedule) {
        Representation representation = new Representation(show, location, schedule);
        return representationRepository.save(representation);
    }

    @Transactional
    public Representation save(Representation representation) {
        return representationRepository.save(representation);
    }

    @Transactional
    public void deleteById(Long id) {
        representationRepository.deleteById(id);
    }
}