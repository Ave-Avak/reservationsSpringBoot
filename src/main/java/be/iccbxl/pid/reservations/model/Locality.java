package be.iccbxl.pid.reservations.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;        // 🆕
import java.util.List;              // 🆕

@Entity
@Table(name = "localities",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_postal_locality",
           columnNames = {"postal_code", "locality"}
       ))
@Getter                            // 🆕 (remplace @Data)
@Setter                            // 🆕 (remplace @Data)
@NoArgsConstructor
@AllArgsConstructor
public class Locality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "postal_code", nullable = false, length = 6)
    private String postalCode;

    @Column(name = "locality", nullable = false, length = 60)
    private String locality;

    /**
     * 🆕 Relation OneToMany inverse de Location.locality.
     *
     * mappedBy = "locality" → "le côté propriétaire est Location.locality"
     *                        (c'est Location qui contient la clé étrangère)
     *
     * cascade = PERSIST → si on persiste une locality, ses locations sont persistées aussi
     *           (utile pour les seeds : on crée la locality avec ses locations d'un coup)
     */
    @OneToMany(mappedBy = "locality",
               cascade = {CascadeType.PERSIST},
               fetch = FetchType.LAZY)
    private List<Location> locations = new ArrayList<>();  // 🆕

    // =============================================================
    // CONSTRUCTEUR DE CONFORT
    // =============================================================

    public Locality(String postalCode, String locality) {
        this.postalCode = postalCode;
        this.locality = locality;
    }

    public String getFullName() {
        return postalCode + " " + locality;
    }

    // =============================================================
    // 🆕 MÉTHODES MÉTIER (synchronisation bilatérale)
    // =============================================================

    /**
     * Ajoute une location à cette locality.
     * Met à jour les deux côtés de la relation.
     */
    public void addLocation(Location location) {
        if (!this.locations.contains(location)) {
            this.locations.add(location);
            location.setLocality(this);  // synchronise l'autre côté
        }
    }

    /**
     * Retire une location de cette locality.
     */
    public void removeLocation(Location location) {
        if (this.locations.contains(location)) {
            this.locations.remove(location);
            // On ne fait PAS location.setLocality(null) car la FK est NOT NULL.
            // C'est à l'appelant de réaffecter la location à une autre locality.
        }
    }
}