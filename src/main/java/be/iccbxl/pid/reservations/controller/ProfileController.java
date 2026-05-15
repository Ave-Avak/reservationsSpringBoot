package be.iccbxl.pid.reservations.controller;

import be.iccbxl.pid.reservations.dto.PasswordChangeDto;
import be.iccbxl.pid.reservations.dto.UserProfileDto;
import be.iccbxl.pid.reservations.model.User;
import be.iccbxl.pid.reservations.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    // =========================================================
    // GET /profile — Affichage du profil
    // =========================================================

    @GetMapping
    public String showProfile(Principal principal, Model model) {
        User user = userService.findByLogin(principal.getName());

        // Pré-remplit le DTO de modification de profil avec les valeurs actuelles
        if (!model.containsAttribute("profileDto")) {
            UserProfileDto profileDto = new UserProfileDto();
            profileDto.setEmail(user.getEmail());
            profileDto.setFirstname(user.getFirstname());
            profileDto.setLastname(user.getLastname());
            model.addAttribute("profileDto", profileDto);
        }

        // DTO vide pour le formulaire de changement de mot de passe
        if (!model.containsAttribute("passwordDto")) {
            model.addAttribute("passwordDto", new PasswordChangeDto());
        }

        model.addAttribute("user", user);
        return "auth/profile";
    }

    // =========================================================
    // POST /profile/update — Mise à jour du profil
    // =========================================================

    @PostMapping("/update")
    public String updateProfile(
            @Valid @ModelAttribute("profileDto") UserProfileDto dto,
            BindingResult result,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            // En cas d'erreur, on garde les valeurs saisies et on retourne au formulaire
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.profileDto", result);
            redirectAttributes.addFlashAttribute("profileDto", dto);
            return "redirect:/profile";
        }

        boolean success = userService.updateProfile(
                principal.getName(),
                dto.getEmail(),
                dto.getFirstname(),
                dto.getLastname()
        );

        if (!success) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Cet email est déjà utilisé par un autre compte.");
        } else {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Profil mis à jour avec succès.");
        }
        return "redirect:/profile";
    }

    // =========================================================
    // POST /profile/password — Changement de mot de passe
    // =========================================================

    @PostMapping("/password")
    public String changePassword(
            @Valid @ModelAttribute("passwordDto") PasswordChangeDto dto,
            BindingResult result,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        // Vérif que les 2 nouveaux mots de passe correspondent
        if (dto.getNewPassword() != null && !dto.getNewPassword().equals(dto.getNewPasswordConfirm())) {
            result.rejectValue("newPasswordConfirm", "mismatch",
                    "Les mots de passe ne correspondent pas.");
        }

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.passwordDto", result);
            redirectAttributes.addFlashAttribute("passwordDto", dto);
            return "redirect:/profile";
        }

        boolean success = userService.changePassword(
                principal.getName(),
                dto.getCurrentPassword(),
                dto.getNewPassword()
        );

        if (!success) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Le mot de passe actuel est incorrect.");
        } else {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Mot de passe modifié avec succès.");
        }
        return "redirect:/profile";
    }

    // =========================================================
    // POST /profile/delete — Suppression définitive du compte
    // =========================================================

    @PostMapping("/delete")
    public String deleteAccount(
        HttpServletRequest request,
        HttpServletResponse response,
        Principal principal,
        RedirectAttributes redirectAttributes) {

    // ⚠️ Sécurité : un admin ne peut PAS supprimer son propre compte via le profil
    // (pour éviter qu'il n'y ait plus aucun admin dans l'application)
    if (request.isUserInRole("ADMIN")) {
        redirectAttributes.addFlashAttribute("errorMessage",
                "Un administrateur ne peut pas supprimer son propre compte. " +
                "Demandez à un autre administrateur de le faire pour vous.");
        return "redirect:/profile";
    }

    String login = principal.getName();

    // 1. Suppression en BDD (cascade auto)
    userService.deleteAccount(login);

    // 2. Déconnexion immédiate
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) {
        new SecurityContextLogoutHandler().logout(request, response, auth);
    }

    redirectAttributes.addFlashAttribute("successMessage",
            "Votre compte a été supprimé. Au revoir.");
    return "redirect:/login";
}
}