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
        // Création de l'entité User
        User user = new User();
        user.setLogin(login);
        user.setEmail(email);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setPassword(passwordEncoder.encode(rawPassword)); // 🔒 BCrypt
        user.setEnabled(true);

        // Attribution du rôle ROLE_USER (créé en BDD au Ch.4.2)
        Role userRole = roleRepository.findByRole("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Rôle ROLE_USER introuvable en BDD"));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        // Sauvegarde en BDD
        return userRepository.save(user);
    }

    /**
     * Vérifie si un login est déjà utilisé (pour validation côté inscription).
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
}