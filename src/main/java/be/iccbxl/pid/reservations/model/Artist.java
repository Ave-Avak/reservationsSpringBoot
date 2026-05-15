package be.iccbxl.pid.reservations.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "artists")
@Getter
@Setter
@NoArgsConstructor
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le prénom ne peut pas être vide.")
    @Size(min = 2, max = 60, message = "Le prénom doit contenir entre 2 et 60 caractères.")
    private String firstname;

    @NotBlank(message = "Le nom ne peut pas être vide.")
    @Size(min = 2, max = 60, message = "Le nom doit contenir entre 2 et 60 caractères.")
    private String lastname;

    /**
     * 🆕 Types/rôles que cet artiste peut tenir (acteur, metteur en scène...).
     *
     * Relation ManyToMany OWNER : c'est cette entité qui définit la table de jointure.
     * Set au lieu de List : un artiste ne peut pas avoir deux fois le même type.
     */
    @ManyToMany(fetch = FetchType.LAZY,
                cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "artist_type",
        joinColumns = @JoinColumn(name = "artist_id"),
        inverseJoinColumns = @JoinColumn(name = "type_id")
    )
    private Set<Type> types = new HashSet<>();

    // =============================================================
    // CONSTRUCTEUR MÉTIER
    // =============================================================

    public Artist(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }

    // =============================================================
    // 🆕 MÉTHODES MÉTIER pour Types (synchronisation bilatérale)
    // =============================================================

    /**
     * Ajoute un type à cet artiste (synchronise les 2 côtés).
     */
    public void addType(Type type) {
        if (this.types.add(type)) {  // Set.add() renvoie true si ajouté (pas déjà présent)
            type.getArtists().add(this);
        }
    }

    /**
     * Retire un type de cet artiste.
     */
    public void removeType(Type type) {
        if (this.types.remove(type)) {
            type.getArtists().remove(this);
        }
    }

    // =============================================================
    // EQUALS / HASHCODE basés sur l'id seulement
    // (évite les boucles infinies avec les relations bilatérales)
    // =============================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Artist)) return false;
        Artist artist = (Artist) o;
        return id != null && id.equals(artist.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // =============================================================
    // toString
    // =============================================================

    @Override
    public String toString() {
        return firstname + " " + lastname;
    }
}