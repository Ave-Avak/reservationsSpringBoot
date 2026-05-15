-- ============================================
-- V13 : Création de la table reservations
-- ============================================
-- Entité-association entre users et representations.
-- Stocke la quantité de places réservées + métadonnées.

CREATE TABLE reservations (
    id                bigint(20)   NOT NULL AUTO_INCREMENT,
    user_id           bigint(20)   NOT NULL,
    representation_id bigint(20)   NOT NULL,
    places            int(11)      NOT NULL DEFAULT 1,
    booking_date      datetime     NOT NULL,
    status            varchar(20)  NOT NULL DEFAULT 'PENDING',
    PRIMARY KEY (id),
    CONSTRAINT fk_reservations_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_reservations_representation
        FOREIGN KEY (representation_id) REFERENCES representations(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Index pour requêtes fréquentes
CREATE INDEX idx_reservations_user ON reservations(user_id);
CREATE INDEX idx_reservations_representation ON reservations(representation_id);
CREATE INDEX idx_reservations_status ON reservations(status);