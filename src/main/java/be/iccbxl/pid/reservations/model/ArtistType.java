package be.iccbxl.pid.reservations.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entité-association entre Artist et Type.
 *
 * Représente "tel artiste peut tenir tel rôle/type".
 * Cette entité existe parce qu'elle est référencée par les Shows
 * (un même artiste/type peut participer à plusieurs spectacles).
 *
 * Relations :
 *  - ManyToOne vers Artist  (qui)
 *  - ManyToOne vers Type    (quel rôle)
 *  - ManyToMany vers Show   (sur quels spectacles)
 */
@Entity
@Table(name = "artist_type",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_artist_type",
           columnNames = {"artist_id", "type_id"}
       ))
@Getter
@Setter
@NoArgsConstructor
public class ArtistType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private Type type;

    /**
     * Spectacles auxquels cet artiste-type participe.
     * Relation ManyToMany via la table artist_type_show.
     */
    @ManyToMany(fetch = FetchType.LAZY,
                cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "artist_type_show",
        joinColumns = @JoinColumn(name = "artist_type_id"),
        inverseJoinColumns = @JoinColumn(name = "show_id")
    )
    private Set<Show> shows = new HashSet<>();

    // =============================================================
    // CONSTRUCTEUR MÉTIER
    // =============================================================

    public ArtistType(Artist artist, Type type) {
        this.artist = artist;
        this.type = type;
    }

    // =============================================================
    // MÉTHODES MÉTIER pour Shows (synchronisation bilatérale)
    // =============================================================

    public void addShow(Show show) {
        if (this.shows.add(show)) {
            show.getArtistTypes().add(this);
        }
    }

    public void removeShow(Show show) {
        if (this.shows.remove(show)) {
            show.getArtistTypes().remove(this);
        }
    }

    // =============================================================
    // EQUALS / HASHCODE basés sur l'id
    // =============================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArtistType)) return false;
        ArtistType at = (ArtistType) o;
        return id != null && id.equals(at.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return (artist != null ? artist.toString() : "?") +
               " (" + (type != null ? type.toString() : "?") + ")";
    }
}