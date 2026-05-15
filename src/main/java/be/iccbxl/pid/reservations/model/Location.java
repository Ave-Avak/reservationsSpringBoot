package be.iccbxl.pid.reservations.model;

import com.github.slugify.Slugify;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un lieu de spectacle (théâtre, salle, etc.).
 *
 * Relations :
 *  - ManyToOne vers Locality (une location appartient à une locality)
 *  - OneToMany vers Show     (un lieu peut héberger plusieurs spectacles)
 *
 * Le slug est généré automatiquement à partir de la désignation.
 */
@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    // Instance statique réutilisée pour tous les slugs (créée une seule fois)
    private static final Slugify SLUGIFY = Slugify.builder().build();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slug", nullable = false, unique = true, length = 60)
    private String slug;

    @Column(name = "designation", nullable = false, length = 60)
    private String designation;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "website", length = 255)
    private String website;

    @Column(name = "phone", length = 30)
    private String phone;

    /**
     * Relation ManyToOne : cette location appartient à une locality.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locality_id", nullable = false)
    private Locality locality;

    /**
     * 🆕 Spectacles créés dans ce lieu.
     * Relation OneToMany bilatérale inverse de Show.location.
     */
    @OneToMany(mappedBy = "location",
               cascade = {CascadeType.PERSIST},
               fetch = FetchType.LAZY)
    private List<Show> shows = new ArrayList<>();

    // =============================================================
    // CONSTRUCTEURS MÉTIER
    // =============================================================

    /**
     * Constructeur principal : crée une location avec génération automatique du slug.
     */
    public Location(String designation, String address, Locality locality) {
        this.designation = designation;
        this.address = address;
        setLocality(locality);
        this.slug = generateSlug(designation);
    }

    // =============================================================
    // SETTER INTELLIGENT pour Locality (synchronisation bilatérale)
    // =============================================================

    public void setLocality(Locality newLocality) {
        if (this.locality != null) {
            this.locality.getLocations().remove(this);
        }
        this.locality = newLocality;
        if (newLocality != null && !newLocality.getLocations().contains(this)) {
            newLocality.getLocations().add(this);
        }
    }

    // =============================================================
    // 🆕 MÉTHODES MÉTIER pour Shows (synchronisation bilatérale)
    // =============================================================

    /**
     * Ajoute un show à ce lieu.
     */
    public void addShow(Show show) {
        if (!this.shows.contains(show)) {
            this.shows.add(show);
            show.setLocation(this);
        }
    }

    /**
     * Retire un show de ce lieu.
     */
    public void removeShow(Show show) {
        if (this.shows.contains(show)) {
            this.shows.remove(show);
            // On ne fait PAS show.setLocation(null) : la FK est NOT NULL
        }
    }

    // =============================================================
    // GÉNÉRATION DU SLUG
    // =============================================================

    /**
     * Met à jour la désignation ET régénère le slug automatiquement.
     */
    public void updateDesignation(String designation) {
        this.designation = designation;
        this.slug = generateSlug(designation);
    }

    /**
     * Convertit une désignation en slug URL-friendly.
     * "Espace Delvaux / La Vénerie" → "espace-delvaux-la-venerie"
     */
    private static String generateSlug(String input) {
        return SLUGIFY.slugify(input);
    }
}