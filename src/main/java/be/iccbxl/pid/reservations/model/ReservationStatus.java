package be.iccbxl.pid.reservations.model;

/**
 * Statuts possibles d'une réservation.
 * Stocké en BDD comme String via @Enumerated(EnumType.STRING).
 */
public enum ReservationStatus {

    PENDING("En attente de paiement"),
    CONFIRMED("Confirmée"),
    CANCELLED("Annulée"),
    REFUNDED("Remboursée");

    private final String label;

    ReservationStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}