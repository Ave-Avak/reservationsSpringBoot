package be.iccbxl.pid.reservations.repository;

import be.iccbxl.pid.reservations.model.PasswordResetToken;
import be.iccbxl.pid.reservations.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Recherche un token par sa valeur (UUID envoyé dans l'email).
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Invalide tous les tokens actifs d'un utilisateur.
     * Utile quand on génère un nouveau token : on annule les anciens
     * pour éviter d'avoir plusieurs tokens valides en parallèle.
     */
    @Modifying
    @Query("UPDATE PasswordResetToken t SET t.used = true WHERE t.user = :user AND t.used = false")
    void invalidateAllTokensForUser(User user);
}