package be.iccbxl.pid.reservations.controller;

import be.iccbxl.pid.reservations.dto.ReservationFormDto;
import be.iccbxl.pid.reservations.model.Representation;
import be.iccbxl.pid.reservations.model.Reservation;
import be.iccbxl.pid.reservations.model.User;
import be.iccbxl.pid.reservations.service.RepresentationService;
import be.iccbxl.pid.reservations.service.ReservationService;
import be.iccbxl.pid.reservations.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * Gère les réservations côté membre.
 *
 * - GET  /reservations/new?representationId=X  : formulaire
 * - POST /reservations                          : création
 */
@Controller
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final RepresentationService representationService;
    private final UserService userService;

    /**
     * Affiche le formulaire de réservation pour une représentation donnée.
     * URL : /reservations/new?representationId=42
     */
    @GetMapping("/new")
    public String newForm(@RequestParam("representationId") Long representationId,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        // Vérifie que la représentation existe
        Representation representation = representationService.findByIdWithDetails(representationId).orElse(null);
        if (representation == null) {
            redirectAttributes.addFlashAttribute("error", "Représentation introuvable.");
            return "redirect:/shows";
        }

        // Vérifie que la représentation n'est pas passée
        if (representation.isPast()) {
            redirectAttributes.addFlashAttribute("error",
                "Cette représentation est passée, vous ne pouvez plus réserver.");
            return "redirect:/shows/" + representation.getShow().getSlug();
        }

        // Vérifie que le show est bookable
        if (!representation.getShow().getBookable()) {
            redirectAttributes.addFlashAttribute("error",
                "Ce spectacle n'est pas disponible à la réservation.");
            return "redirect:/shows/" + representation.getShow().getSlug();
        }

        // Pré-remplit le DTO avec l'id de la représentation
        ReservationFormDto dto = new ReservationFormDto();
        dto.setRepresentationId(representationId);
        dto.setPlaces(1);

        model.addAttribute("reservationDto", dto);
        model.addAttribute("representation", representation);
        return "reservations/new";
    }

    /**
     * Traite la création d'une réservation.
     */
    @PostMapping
    public String create(@Valid @ModelAttribute("reservationDto") ReservationFormDto dto,
                         BindingResult result,
                         Principal principal,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        // Récupère la représentation (utile aussi en cas d'erreur, pour ré-afficher le form)
        Representation representation = representationService.findByIdWithDetails(dto.getRepresentationId()).orElse(null);

        if (representation == null) {
            redirectAttributes.addFlashAttribute("error", "Représentation introuvable.");
            return "redirect:/shows";
        }

        // Re-vérifications de sécurité (le user peut avoir bidouillé le form)
        if (representation.isPast()) {
            redirectAttributes.addFlashAttribute("error",
                "Cette représentation est passée.");
            return "redirect:/shows/" + representation.getShow().getSlug();
        }

        if (!representation.getShow().getBookable()) {
            redirectAttributes.addFlashAttribute("error",
                "Ce spectacle n'est pas disponible à la réservation.");
            return "redirect:/shows/" + representation.getShow().getSlug();
        }

        // Si erreurs de validation → ré-affiche le form avec les erreurs
        if (result.hasErrors()) {
            model.addAttribute("representation", representation);
            return "reservations/new";
        }

        // Récupère le user connecté
        User user = userService.findByLogin(principal.getName());

        // Crée la réservation
        Reservation reservation = reservationService.create(user, representation, dto.getPlaces());

        redirectAttributes.addFlashAttribute("success",
            "Votre réservation de " + reservation.getPlaces() + " place(s) pour \""
            + representation.getShow().getTitle() + "\" a été enregistrée (en attente de confirmation).");

        return "redirect:/profile/reservations";
    }
}