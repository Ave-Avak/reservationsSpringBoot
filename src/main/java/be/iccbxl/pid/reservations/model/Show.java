package be.iccbxl.pid.reservations.model;

import com.github.slugify.Slugify;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Year;
import java.util.ArrayList;     
import java.util.List;              
import java.util.HashSet;
import java.util.Set;
/**
 * Représente un spectacle (pièce centrale du projet).
 *
 * Relations :
 *  - ManyToOne vers Location (lieu de création) [bilatérale]
 *  - OneToMany vers Representation (dates de spectacle) [bilatérale]
 *
 * Le slug est généré automatiquement à partir du titre.
 */
@Entity
@Table(name = "shows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Show {

    // Instance statique réutilisée pour tous les slugs
    private static final Slugify SLUGIFY = Slugify.builder().build();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slug", nullable = false, unique = true, length = 60)
    private String slug;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "poster_url", length = 255)
    private String posterUrl;

    @Column(name = "duration")
    private Short duration;

    @Column(name = "created_in", columnDefinition = "YEAR")
    private Year createdIn;

    @Column(name = "bookable", nullable = false)
    private Boolean bookable = false;

    /**
     * Lieu de création du spectacle (ManyToOne bilatérale).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    /**
     * 🆕 Représentations (dates) de ce spectacle.
     * Cascade ALL + orphanRemoval : supprimer un show supprime ses représentations.
     */
    @OneToMany(mappedBy = "show",
               cascade = CascadeType.ALL,
               orphanRemoval = true,
               fetch = FetchType.LAZY)
    private List<Representation> representations = new ArrayList<>();

    /**
     * 🆕 Participations artistes (ArtistType) à ce spectacle.
     * Relation ManyToMany INVERSE de ArtistType.shows.
     */
    @ManyToMany(mappedBy = "shows", fetch = FetchType.LAZY)
    private Set<ArtistType> artistTypes = new HashSet<>();

    // =============================================================
    // CONSTRUCTEUR MÉTIER
    // =============================================================

    public Show(String title, String description, Location location, Short duration, Year createdIn) {
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.createdIn = createdIn;
        this.bookable = false;
        setLocation(location);
        this.slug = SLUGIFY.slugify(title);
    }

    // =============================================================
    // SETTER INTELLIGENT pour Location (synchronisation bilatérale)
    // =============================================================

    public void setLocation(Location newLocation) {
        if (this.location != null) {
            this.location.getShows().remove(this);
        }
        this.location = newLocation;
        if (newLocation != null && !newLocation.getShows().contains(this)) {
            newLocation.getShows().add(this);
        }
    }

    // =============================================================
    // 🆕 MÉTHODES MÉTIER pour Representations (synchronisation bilatérale)
    // =============================================================

    public void addRepresentation(Representation representation) {
        if (!this.representations.contains(representation)) {
            this.representations.add(representation);
            representation.setShow(this);
        }
    }

    public void removeRepresentation(Representation representation) {
        if (this.representations.contains(representation)) {
            this.representations.remove(representation);
        }
    }
    
    // =============================================================
    // 🆕 MÉTHODES MÉTIER pour ArtistTypes (synchronisation bilatérale)
    // =============================================================

    public void addArtistType(ArtistType artistType) {
        if (this.artistTypes.add(artistType)) {
            artistType.getShows().add(this);
        }
    }

    public void removeArtistType(ArtistType artistType) {
        if (this.artistTypes.remove(artistType)) {
            artistType.getShows().remove(this);
        }
    }
    // =============================================================
    // MÉTHODES MÉTIER
    // =============================================================

    /**
     * Met à jour le titre ET régénère le slug.
     */
    public void updateTitle(String title) {
        this.title = title;
        this.slug = SLUGIFY.slugify(title);
    }

    /**
     * Affichage formaté de la durée : "1h30" pour 90 minutes.
     */
    public String getFormattedDuration() {
        if (duration == null) return "N/A";
        int hours = duration / 60;
        int mins = duration % 60;
        if (hours == 0) return mins + " min";
        if (mins == 0) return hours + "h";
        return hours + "h" + String.format("%02d", mins);
    }
}