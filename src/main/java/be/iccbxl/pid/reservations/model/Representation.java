package be.iccbxl.pid.reservations.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Représente une date précise où un spectacle est joué.
 *
 * Relations :
 *  - ManyToOne vers Show     (cascade DELETE)
 *  - ManyToOne vers Location (restrict)
 */
@Entity
@Table(name = "representations",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_show_location_schedule",
           columnNames = {"show_id", "location_id", "schedule"}
       ))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Representation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "schedule", nullable = false)
    private LocalDateTime schedule;

    // =============================================================
    // CONSTRUCTEUR MÉTIER
    // =============================================================

    public Representation(Show show, Location location, LocalDateTime schedule) {
        setShow(show);
        setLocation(location);
        this.schedule = schedule;
    }

    // =============================================================
    // SETTERS INTELLIGENTS (synchronisation bilatérale)
    // =============================================================

    public void setShow(Show newShow) {
        if (this.show != null) {
            this.show.getRepresentations().remove(this);
        }
        this.show = newShow;
        if (newShow != null && !newShow.getRepresentations().contains(this)) {
            newShow.getRepresentations().add(this);
        }
    }

    public void setLocation(Location newLocation) {
        if (this.location != null) {
            this.location.getRepresentations().remove(this);
        }
        this.location = newLocation;
        if (newLocation != null && !newLocation.getRepresentations().contains(this)) {
            newLocation.getRepresentations().add(this);
        }
    }

    // =============================================================
    // MÉTHODES MÉTIER
    // =============================================================

    public String getFormattedSchedule() {
        if (schedule == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("EEEE d MMMM yyyy 'à' HH:mm", Locale.FRENCH);
        return schedule.format(formatter);
    }

    public String getShortSchedule() {
        if (schedule == null) return "N/A";
        return schedule.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public boolean isPast() {
        return schedule != null && schedule.isBefore(LocalDateTime.now());
    }

    public boolean isUpcoming() {
        return schedule != null && schedule.isAfter(LocalDateTime.now());
    }
}