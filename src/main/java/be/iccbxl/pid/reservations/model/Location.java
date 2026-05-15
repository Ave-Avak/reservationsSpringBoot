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
 *  - OneToMany vers Representation (un lieu accueille plusieurs dates de spectacle)
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

    // Instance statique réutilisée pour tous les slugs
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
     * Spectacles créés dans ce lieu.
     */
    @OneToMany(mappedBy = "location",
               cascade = {CascadeType.PERSIST},
               fetch = FetchType.LAZY)
    private List<Show> shows = new ArrayList<>();

    /**
     * 🆕 Représentations programmées dans ce lieu.
     */
    @OneToMany(mappedBy = "location",
               cascade = {CascadeType.PERSIST},
               fetch = FetchType.LAZY)
    private List<Representation> representations = new ArrayList<>();

    // =============================================================
    // CONSTRUCTEURS MÉTIER
    // =============================================================

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
    // MÉTHODES MÉTIER pour Shows (synchronisation bilatérale)
    // =============================================================

    public void addShow(Show show) {
        if (!this.shows.contains(show)) {
            this.shows.add(show);
            show.setLocation(this);
        }
    }

    public void removeShow(Show show) {
        if (this.shows.contains(show)) {
            this.shows.remove(show);
        }
    }

    // =============================================================
    // 🆕 MÉTHODES MÉTIER pour Representations (synchronisation bilatérale)
    // =============================================================

    public void addRepresentation(Representation representation) {
        if (!this.representations.contains(representation)) {
            this.representations.add(representation);
            representation.setLocation(this);
        }
    }

    public void removeRepresentation(Representation representation) {
        if (this.representations.contains(representation)) {
            this.representations.remove(representation);
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