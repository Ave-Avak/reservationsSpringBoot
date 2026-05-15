package be.iccbxl.pid.reservations.service;

import be.iccbxl.pid.reservations.model.Representation;
import be.iccbxl.pid.reservations.model.Reservation;
import be.iccbxl.pid.reservations.model.User;
import be.iccbxl.pid.reservations.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    /**
     * Réservations d'un user (les plus récentes en premier).
     */
    public List<Reservation> findByUser(Long userId) {
        return reservationRepository.findByUserIdOrderByBookingDateDesc(userId);
    }

    /**
     * Réservations pour une représentation.
     */
    public List<Reservation> findByRepresentation(Long representationId) {
        return reservationRepository.findByRepresentationIdOrderByBookingDateAsc(representationId);
    }

    /**
     * Nombre total de places réservées (hors annulées) pour une representation.
     */
    public int countReservedPlaces(Long representationId) {
        Integer count = reservationRepository.countTotalReservedPlaces(representationId);
        return count != null ? count : 0;
    }

    /**
     * Crée une nouvelle réservation (statut PENDING par défaut).
     */
    @Transactional
    public Reservation create(User user, Representation representation, Integer places) {
        if (places == null || places < 1) {
            throw new IllegalArgumentException("Le nombre de places doit être au moins 1.");
        }
        Reservation reservation = new Reservation(user, representation, places);
        return reservationRepository.save(reservation);
    }

    /**
     * Confirme une réservation (ex: après paiement).
     */
    @Transactional
    public Reservation confirm(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable : " + reservationId));
        reservation.confirm();
        return reservationRepository.save(reservation);
    }

    /**
     * Annule une réservation.
     */
    @Transactional
    public Reservation cancel(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable : " + reservationId));
        reservation.cancel();
        return reservationRepository.save(reservation);
    }

    @Transactional
    public void deleteById(Long id) {
        reservationRepository.deleteById(id);
    }
}