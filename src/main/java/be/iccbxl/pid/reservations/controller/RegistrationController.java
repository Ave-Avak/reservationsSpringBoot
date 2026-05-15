package be.iccbxl.pid.reservations.controller;

import be.iccbxl.pid.reservations.dto.UserRegistrationDto;
import be.iccbxl.pid.reservations.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    /**
     * GET /register — affiche le formulaire d'inscription.
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // On envoie un DTO vide pour pré-remplir le formulaire
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserRegistrationDto());
        }
        return "auth/register";
    }

    /**
     * POST /register — traite la soumission du formulaire.
     */
    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("user") UserRegistrationDto dto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        // 1. Vérification d'unicité du login
        if (userService.isLoginTaken(dto.getLogin())) {
            result.addError(new FieldError("user", "login",
                    "Ce nom d'utilisateur est déjà pris."));
        }

        // 2. Vérification d'unicité de l'email
        if (userService.isEmailTaken(dto.getEmail())) {
            result.addError(new FieldError("user", "email",
                    "Cet email est déjà utilisé."));
        }

        // 3. Si des erreurs (validation OU unicité), on retourne au formulaire
        if (result.hasErrors()) {
            return "auth/register";
        }

        // 4. Tout est OK : on crée le user
        userService.register(
                dto.getLogin(),
                dto.getEmail(),
                dto.getFirstname(),
                dto.getLastname(),
                dto.getPassword()
        );

        // 5. Redirection vers /login avec un message flash
        redirectAttributes.addFlashAttribute("successMessage",
                "Inscription réussie ! Vous pouvez maintenant vous connecter.");
        return "redirect:/login";
    }
}