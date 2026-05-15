-- ============================================
-- V10 : Création de la table representations
-- ============================================
-- Stocke les dates précises où un spectacle est joué.
-- Chaque representation a 2 relations ManyToOne :
--   - vers Show     (quel spectacle)
--   - vers Location (où il est joué, peut différer du lieu de création)

CREATE TABLE representations (
    id           bigint(20) NOT NULL AUTO_INCREMENT,
    show_id      bigint(20) NOT NULL,
    location_id  bigint(20) NOT NULL,
    schedule     datetime   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_representations_show
        FOREIGN KEY (show_id) REFERENCES shows(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_representations_location
        FOREIGN KEY (location_id) REFERENCES locations(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    UNIQUE KEY uk_show_location_schedule (show_id, location_id, schedule)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Quelques représentations pour les tests (dates futures à partir d'octobre 2026)
INSERT INTO representations (show_id, location_id, schedule) VALUES
    -- Ayiti (show 1) au Delvaux (location 1) - 3 dates
    (1, 1, '2026-10-12 20:30:00'),
    (1, 1, '2026-10-13 20:30:00'),
    (1, 1, '2026-10-14 14:00:00'),
    -- Ayiti en tournée au Théâtre 140 (location 2)
    (1, 2, '2026-11-05 20:00:00'),
    (1, 2, '2026-11-06 20:00:00'),
    -- Le Petit Prince (show 2) au Théâtre 140 (location 2)
    (2, 2, '2026-10-19 14:30:00'),
    (2, 2, '2026-10-19 17:00:00'),
    (2, 2, '2026-10-20 14:30:00'),
    -- Hamlet (show 3) aux Halles de Schaerbeek (location 3)
    (3, 3, '2026-11-15 20:00:00'),
    (3, 3, '2026-11-16 20:00:00'),
    (3, 3, '2026-11-17 20:00:00'),
    -- Cabaret (show 4) au Théâtre National (location 4)
    (4, 4, '2026-12-01 20:00:00'),
    (4, 4, '2026-12-02 20:00:00'),
    (4, 4, '2026-12-03 20:00:00'),
    (4, 4, '2026-12-04 20:00:00');