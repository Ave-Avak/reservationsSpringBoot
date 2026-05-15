package be.iccbxl.pid.reservations.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Type {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false, unique = true, length = 60)
    private String type;

    /**
     * 🆕 Les ArtistTypes qui utilisent ce type/rôle.
     * Relation inverse de ArtistType.type.
     */
    @OneToMany(mappedBy = "type",
               cascade = CascadeType.ALL,
               orphanRemoval = true,
               fetch = FetchType.LAZY)
    private Set<ArtistType> artistTypes = new HashSet<>();

    // =============================================================
    // CONSTRUCTEUR DE CONFORT
    // =============================================================

    public Type(String type) {
        this.type = type;
    }

    // =============================================================
    // EQUALS / HASHCODE basés sur l'id
    // =============================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Type)) return false;
        Type t = (Type) o;
        return id != null && id.equals(t.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return type;
    }
}