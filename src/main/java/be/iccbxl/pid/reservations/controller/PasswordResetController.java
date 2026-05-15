package be.iccbxl.pid.reservations.controller;

import be.iccbxl.pid.reservations.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    // =========================================================
    // ÉTAPE 1 — "J'ai oublié mon mot de passe"
    // =========================================================

    /**
     * GET /forgot-password — affiche le formulaire de demande de reset.
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot-password";
    }

    /**
     * POST /forgot-password — reçoit l'email, déclenche l'envoi du mail.
     */
    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam("email") String email,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        // Construit l'URL de base (ex: http://localhost:8080)
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

        // Délègue la logique au service (qui crée le token + envoie l'email)
        passwordResetService.requestPasswordReset(email, baseUrl);

        // ⚠️ Sécurité : on affiche TOUJOURS le même message,
        // que l'email existe ou non (anti-énumération d'emails)
        redirectAttributes.addFlashAttribute("successMessage",
                "Si cet email correspond à un compte, un lien de réinitialisation vient d'être envoyé.");
        return "redirect:/login";
    }

    // =========================================================
    // ÉTAPE 2 — "Cliquer sur le lien dans l'email"
    // =========================================================

    /**
     * GET /reset-password?token=XXX — affiche le formulaire de nouveau mot de passe.
     */
    @GetMapping("/reset-password")
    public String showResetPasswordForm(
            @RequestParam("token") String token,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Vérifie d'abord que le token est valide
        if (passwordResetService.validateToken(token).isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Le lien de réinitialisation est invalide ou a expiré.");
            return "redirect:/login";
        }

        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    /**
     * POST /reset-password — soumission du nouveau mot de passe.
     */
    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("passwordConfirm") String passwordConfirm,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Vérif basique : longueur minimum
        if (password == null || password.length() < 8) {
            model.addAttribute("token", token);
            model.addAttribute("errorMessage",
                    "Le mot de passe doit contenir au moins 8 caractères.");
            return "auth/reset-password";
        }

        // Vérif basique : mots de passe identiques
        if (!password.equals(passwordConfirm)) {
            model.addAttribute("token", token);
            model.addAttribute("errorMessage", "Les mots de passe ne correspondent pas.");
            return "auth/reset-password";
        }

        // Appel du service pour réellement changer le mot de passe
        boolean success = passwordResetService.resetPassword(token, password);

        if (!success) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Le lien de réinitialisation est invalide ou a expiré.");
            return "redirect:/login";
        }

        redirectAttributes.addFlashAttribute("successMessage",
                "Votre mot de passe a été réinitialisé. Vous pouvez maintenant vous connecter.");
        return "redirect:/login";
    }
}