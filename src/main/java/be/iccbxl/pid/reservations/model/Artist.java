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
     * 🆕 Les rôles que cet artiste peut tenir (chaque rôle = ArtistType).
     * Remplace l'ancienne ManyToMany directe vers Type.
     *
     * Relation OneToMany vers ArtistType (qui contient le lien vers Type).
     * Pour récupérer les types : artist.getArtistTypes().stream().map(ArtistType::getType)
     */
    @OneToMany(mappedBy = "artist",
               cascade = CascadeType.ALL,
               orphanRemoval = true,
               fetch = FetchType.LAZY)
    private Set<ArtistType> artistTypes = new HashSet<>();

    // =============================================================
    // CONSTRUCTEUR MÉTIER
    // =============================================================

    public Artist(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }

    // =============================================================
    // MÉTHODES MÉTIER pour ArtistTypes (synchronisation bilatérale)
    // =============================================================

    /**
     * Associe ce type/rôle à cet artiste (création d'un ArtistType).
     */
    public ArtistType addType(Type type) {
        // Vérifie si l'artiste a déjà ce type
        for (ArtistType at : this.artistTypes) {
            if (at.getType().equals(type)) {
                return at;  // déjà associé, on renvoie le ArtistType existant
            }
        }
        ArtistType at = new ArtistType(this, type);
        this.artistTypes.add(at);
        type.getArtistTypes().add(at);
        return at;
    }

    /**
     * Retire un type/rôle de cet artiste.
     */
    public void removeType(Type type) {
        ArtistType toRemove = null;
        for (ArtistType at : this.artistTypes) {
            if (at.getType().equals(type)) {
                toRemove = at;
                break;
            }
        }
        if (toRemove != null) {
            this.artistTypes.remove(toRemove);
            type.getArtistTypes().remove(toRemove);
            toRemove.setArtist(null);
            toRemove.setType(null);
        }
    }

    // =============================================================
    // EQUALS / HASHCODE basés sur l'id
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

    @Override
    public String toString() {
        return firstname + " " + lastname;
    }
}