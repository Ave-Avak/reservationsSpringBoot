-- ============================================
-- V12 : Refactorisation de artist_type en entité
-- ============================================
-- Avant : table de jointure pure (2 FK, PK composite)
-- Après : vraie entité avec son propre id, pour pouvoir
--        être référencée par artist_type_show.

-- On reconstruit artist_type avec un id auto-incrément
DROP TABLE IF EXISTS artist_type;

CREATE TABLE artist_type (
    id         bigint(20) NOT NULL AUTO_INCREMENT,
    artist_id  bigint(20) NOT NULL,
    type_id    bigint(20) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_artist_type (artist_id, type_id),
    CONSTRAINT fk_artist_type_artist
        FOREIGN KEY (artist_id) REFERENCES artists(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_artist_type_type
        FOREIGN KEY (type_id) REFERENCES types(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table de jointure entre artist_type et shows
CREATE TABLE artist_type_show (
    artist_type_id  bigint(20) NOT NULL,
    show_id         bigint(20) NOT NULL,
    PRIMARY KEY (artist_type_id, show_id),
    CONSTRAINT fk_ats_artist_type
        FOREIGN KEY (artist_type_id) REFERENCES artist_type(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_ats_show
        FOREIGN KEY (show_id) REFERENCES shows(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;