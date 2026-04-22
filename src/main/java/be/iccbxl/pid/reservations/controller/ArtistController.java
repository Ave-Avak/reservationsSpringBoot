package be.iccbxl.pid.reservations.controller;

import be.iccbxl.pid.reservations.model.Artist;
import be.iccbxl.pid.reservations.service.ArtistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ArtistController {

    @Autowired
    private ArtistService artistService;

    // Liste tous les artistes
    @GetMapping("/artists")
    public String index(Model model) {
        List<Artist> artists = artistService.getAllArtists();
        model.addAttribute("artists", artists);
        model.addAttribute("title", "Liste des artistes");
        return "artist/index";
    }

    // Affiche un artiste
    @GetMapping("/artists/{id}")
    public String show(@PathVariable Long id, Model model) {
        Artist artist = artistService.getArtist(id)
            .orElseThrow(() -> new RuntimeException("Artiste non trouvé"));
        model.addAttribute("artist", artist);
        model.addAttribute("title", artist.toString());
        return "artist/show";
    }

    // Formulaire de création
    @GetMapping("/artists/create")
    public String create(Model model) {
        if (!model.containsAttribute("artist")) {
            model.addAttribute("artist", new Artist());
        }
        model.addAttribute("title", "Nouvel artiste");
        return "artist/create";
    }

    // Traitement de la création
    @PostMapping("/artists/create")
    public String store(@Valid @ModelAttribute Artist artist,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Erreur lors de la création.");
            return "artist/create";
        }
        artistService.addArtist(artist);
        redirectAttrs.addFlashAttribute("successMessage",
            "Artiste créé avec succès !");
        return "redirect:/artists/" + artist.getId();
    }

    // Formulaire de modification
    @GetMapping("/artists/{id}/edit")
    public String edit(@PathVariable Long id, Model model,
                       HttpServletRequest request) {
        Artist artist = artistService.getArtist(id)
            .orElseThrow(() -> new RuntimeException("Artiste non trouvé"));
        model.addAttribute("artist", artist);
        model.addAttribute("title", "Modifier " + artist.toString());
        String referrer = request.getHeader("Referer");
        model.addAttribute("back", referrer != null ? referrer 
            : "/artists/" + id);
        return "artist/edit";
    }

    // Traitement de la modification
    @PutMapping("/artists/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute Artist artist,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Erreur lors de la modification.");
            return "artist/edit";
        }
        artistService.updateArtist(id, artist);
        redirectAttrs.addFlashAttribute("successMessage",
            "Artiste modifié avec succès !");
        return "redirect:/artists/" + id;
    }

    // Suppression
    @DeleteMapping("/artists/{id}")
    public String delete(@PathVariable Long id,
                         RedirectAttributes redirectAttrs) {
        artistService.getArtist(id).ifPresentOrElse(
            artist -> {
                artistService.deleteArtist(id);
                redirectAttrs.addFlashAttribute("successMessage",
                    "Artiste supprimé avec succès !");
            },
            () -> redirectAttrs.addFlashAttribute("errorMessage",
                "Artiste introuvable !")
        );
        return "redirect:/artists";
    }
}