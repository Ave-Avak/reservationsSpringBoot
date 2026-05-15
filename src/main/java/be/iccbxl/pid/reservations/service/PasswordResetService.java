package be.iccbxl.pid.reservations.service;

import be.iccbxl.pid.reservations.model.PasswordResetToken;
import be.iccbxl.pid.reservations.model.User;
import be.iccbxl.pid.reservations.repository.PasswordResetTokenRepository;
import be.iccbxl.pid.reservations.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private static final int TOKEN_VALIDITY_HOURS = 1;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Étape 1 : génère un token et envoie l'email de réinitialisation.
     *
     * @param email l'email saisi par l'utilisateur dans le formulaire "mot de passe oublié"
     * @param baseUrl l'URL de base de l'application (ex: http://localhost:8080)
     */
    @Transactional
    public void requestPasswordReset(String email, String baseUrl) {
        // 1. On cherche l'utilisateur par email
        Optional<User> userOpt = userRepository.findByEmail(email);

        // ⚠️ Sécurité : si l'email n'existe pas, on ne le révèle PAS.
        // On log juste l'info et on retourne silencieusement.
        // Sinon, un attaquant pourrait deviner quels emails sont enregistrés.
        if (userOpt.isEmpty()) {
            log.warn("Demande de reset pour un email inexistant : {}", email);
            return;
        }

        User user = userOpt.get();

        // 2. On invalide tous les anciens tokens de cet utilisateur
        tokenRepository.invalidateAllTokensForUser(user);

        // 3. On génère un nouveau token (UUID aléatoire, 36 caractères)
        String tokenValue = UUID.randomUUID().toString();

        PasswordResetToken token = new PasswordResetToken();
        token.setToken(tokenValue);
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(TOKEN_VALIDITY_HOURS));
        tokenRepository.save(token);

        // 4. On construit l'URL de reset et on envoie l'email
        String resetUrl = baseUrl + "/reset-password?token=" + tokenValue;
        emailService.sendPasswordResetEmail(user.getEmail(), user.getFirstname(), resetUrl);

        log.info("Token de reset généré et email envoyé pour {}", user.getEmail());
    }

    /**
     * Étape 2 : vérifie qu'un token est valide (existe, non utilisé, non expiré).
     *
     * @param tokenValue le token reçu via l'URL
     * @return le token si valide, sinon Optional.empty()
     */
    public Optional<PasswordResetToken> validateToken(String tokenValue) {
        return tokenRepository.findByToken(tokenValue)
                .filter(PasswordResetToken::isValid);
    }

    /**
     * Étape 3 : réinitialise le mot de passe à partir d'un token valide.
     *
     * @param tokenValue le token reçu via l'URL
     * @param newPassword le nouveau mot de passe en clair (sera hashé en BCrypt)
     * @return true si succès, false si le token est invalide
     */
    @Transactional
    public boolean resetPassword(String tokenValue, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = validateToken(tokenValue);

        if (tokenOpt.isEmpty()) {
            log.warn("Tentative de reset avec un token invalide : {}", tokenValue);
            return false;
        }

        PasswordResetToken token = tokenOpt.get();
        User user = token.getUser();

        // 1. On hashe le nouveau mot de passe et on met à jour le user
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // 2. On marque le token comme utilisé (un token = un seul usage)
        token.setUsed(true);
        tokenRepository.save(token);

        log.info("Mot de passe réinitialisé pour {}", user.getEmail());
        return true;
    }
}