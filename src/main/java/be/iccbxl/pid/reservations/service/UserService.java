package be.iccbxl.pid.reservations.service;

import be.iccbxl.pid.reservations.model.Role;
import be.iccbxl.pid.reservations.model.User;
import be.iccbxl.pid.reservations.repository.RoleRepository;
import be.iccbxl.pid.reservations.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Inscrit un nouvel utilisateur avec le rôle ROLE_USER par défaut.
     * Le mot de passe est hashé en BCrypt avant insertion.
     */
    @Transactional
    public User register(String login, String email, String firstname, String lastname, String rawPassword) {
        User user = new User();
        user.setLogin(login);
        user.setEmail(email);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setPassword(passwordEncoder.encode(rawPassword)); // 🔒 BCrypt
        user.setEnabled(true);

        Role userRole = roleRepository.findByRole("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Rôle ROLE_USER introuvable en BDD"));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    /**
     * Vérifie si un login est déjà utilisé.
     */
    public boolean isLoginTaken(String login) {
        return userRepository.existsByLogin(login);
    }

    /**
     * Vérifie si un email est déjà utilisé.
     */
    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }

    // =========================================================
    // CH.4.9 — Gestion du profil utilisateur
    // =========================================================

    /**
     * Récupère un utilisateur par son login.
     */
    public User findByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new IllegalStateException("Utilisateur introuvable : " + login));
    }

    /**
     * Met à jour le profil (email, prénom, nom).
     * @return true si succès, false si l'email est déjà pris par un AUTRE utilisateur
     */
    @Transactional
    public boolean updateProfile(String login, String newEmail, String firstname, String lastname) {
        User user = findByLogin(login);

        // Vérif unicité de l'email s'il a changé
        if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            return false;
        }

        user.setEmail(newEmail);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        userRepository.save(user);
        return true;
    }

    /**
     * Change le mot de passe de l'utilisateur connecté.
     * @return true si succès, false si l'ancien mot de passe ne correspond pas
     */
    @Transactional
    public boolean changePassword(String login, String currentPassword, String newPassword) {
        User user = findByLogin(login);

        // Vérif que l'ancien mot de passe est correct
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    /**
     * Supprime définitivement le compte.
     * Les rôles (users_roles) et tokens (password_reset_tokens) sont nettoyés
     * automatiquement grâce à ON DELETE CASCADE.
     */
    @Transactional
    public void deleteAccount(String login) {
        User user = findByLogin(login);
        userRepository.delete(user);
    }
}