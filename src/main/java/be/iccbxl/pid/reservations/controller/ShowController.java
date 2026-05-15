package be.iccbxl.pid.reservations.controller;

import be.iccbxl.pid.reservations.model.Representation;
import be.iccbxl.pid.reservations.model.Show;
import be.iccbxl.pid.reservations.service.RepresentationService;
import be.iccbxl.pid.reservations.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Catalogue public des spectacles.
 *
 * - GET /shows         : liste de tous les spectacles bookables
 * - GET /shows/{slug}  : détail d'un spectacle + représentations à venir
 */
@Controller
@RequestMapping("/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;
    private final RepresentationService representationService;

    /**
     * Liste des spectacles disponibles (bookable=true).
     * Accessible à tous (même non-connectés).
     */
    @GetMapping
    public String list(Model model) {
        List<Show> shows = showService.findAllBookable();
        model.addAttribute("shows", shows);
        model.addAttribute("title", "Catalogue des spectacles");
        return "shows/list";
    }

    /**
     * Détail d'un spectacle avec ses représentations à venir.
     */
    @GetMapping("/{slug}")
    public Object detail(@PathVariable String slug, Model model) {
        Show show = showService.findBySlug(slug).orElse(null);

        if (show == null) {
            // Spectacle introuvable → redirection vers le catalogue
            return "redirect:/shows";
        }

        // Représentations à venir uniquement (filtre des dates passées)
        List<Representation> upcoming = representationService.findUpcomingByShow(show.getId());

        model.addAttribute("show", show);
        model.addAttribute("upcomingRepresentations", upcoming);
        return "shows/detail";
    }
}