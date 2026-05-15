package be.iccbxl.pid.reservations.model;

import com.github.slugify.Slugify;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Représente un spectacle (pièce centrale du projet).
 *
 * Un spectacle a un lieu de création (Location, ManyToOne bilatéral).
 * Le slug est généré automatiquement à partir du titre.
 *
 * À venir : relations ManyToMany vers Artists (via ArtistType) et OneToMany vers Representations.
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

    /**
     * Durée en minutes.
     */
    @Column(name = "duration")
    private Integer duration;

    /**
     * Année de création de l'œuvre (1901-2155 en MariaDB).
     */
    @Column(name = "created_in")
    private Integer createdIn;

    /**
     * Indique si le spectacle est disponible à la réservation.
     */
    @Column(name = "bookable", nullable = false)
    private Boolean bookable = false;

    /**
     * Lieu de création du spectacle.
     * Relation ManyToOne bilatérale avec Location.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    // =============================================================
    // CONSTRUCTEUR MÉTIER
    // =============================================================

    /**
     * Crée un spectacle avec génération automatique du slug.
     */
    public Show(String title, String description, Location location, Integer duration, Integer createdIn) {
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.createdIn = createdIn;
        this.bookable = false;  // pas bookable par défaut, sécurité
        setLocation(location);
        this.slug = SLUGIFY.slugify(title);
    }

    // =============================================================
    // SETTER INTELLIGENT (synchronisation bilatérale)
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