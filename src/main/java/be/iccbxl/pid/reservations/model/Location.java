package be.iccbxl.pid.reservations.model;

import com.github.slugify.Slugify;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Représente un lieu de spectacle (théâtre, salle, etc.).
 *
 * Relation : ManyToOne vers Locality (une location appartient à une locality).
 * Le slug est généré automatiquement à partir de la désignation.
 */
@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location {

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
     * FetchType.LAZY = on ne charge la locality que si on la demande
     * (évite des SELECT inutiles à chaque chargement de location).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locality_id", nullable = false)
    private Locality locality;

    // =============================================================
    // CONSTRUCTEURS MÉTIER
    // =============================================================

    /**
     * Constructeur principal : crée une location avec génération automatique du slug.
     */
    public Location(String designation, String address, Locality locality) {
        this.designation = designation;
        this.address = address;
        setLocality(locality);  // utilise le setter intelligent (synchronise les 2 côtés)
        this.slug = generateSlug(designation);
    }

    // =============================================================
    // SETTER INTELLIGENT (synchronisation bilatérale)
    // =============================================================

    /**
     * Setter intelligent : met à jour les DEUX côtés de la relation.
     * - retire cette location de l'ancienne locality (si elle existait)
     * - l'ajoute à la nouvelle locality
     */
    public void setLocality(Locality newLocality) {
        // 1. Retirer de l'ancienne locality
        if (this.locality != null) {
            this.locality.getLocations().remove(this);
        }
        // 2. Affecter la nouvelle
        this.locality = newLocality;
        // 3. Ajouter à la nouvelle locality (si elle n'est pas null)
        if (newLocality != null && !newLocality.getLocations().contains(this)) {
            newLocality.getLocations().add(this);
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

    // Instance statique réutilisée pour tous les slugs (créée une seule fois)
    private static final Slugify SLUGIFY = Slugify.builder().build();

    /**
    *Convertit une désignation en slug URL-friendly.
    * "Espace Delvaux / La Vénerie" → "espace-delvaux-la-venerie"
    */
    private static String generateSlug(String input) {
        return SLUGIFY.slugify(input);
    }    
}