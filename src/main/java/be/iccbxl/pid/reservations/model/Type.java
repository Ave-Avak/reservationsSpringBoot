package be.iccbxl.pid.reservations.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Représente un type/rôle d'artiste sur un spectacle.
 * Exemples : Acteur, Metteur en scène, Technicien, Scénographe.
 *
 * Sera lié aux Artists via une relation ManyToMany (table artist_type)
 * dans une étape ultérieure.
 */
@Entity
@Table(name = "types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Type {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false, unique = true, length = 60)
    private String type;

    /**
     * Constructeur de confort pour créer un type à partir de son libellé.
     */
    public Type(String type) {
        this.type = type;
    }
}