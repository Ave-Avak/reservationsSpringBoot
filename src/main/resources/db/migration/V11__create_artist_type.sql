-- ============================================
-- V11 : Création de la table de jointure artist_type
-- ============================================
-- Relation ManyToMany entre artists et types.
-- Un artiste peut avoir plusieurs types (acteur, metteur en scène...).
-- Un type peut être attribué à plusieurs artistes.

CREATE TABLE artist_type (
    artist_id  bigint(20) NOT NULL,
    type_id    bigint(20) NOT NULL,
    PRIMARY KEY (artist_id, type_id),
    CONSTRAINT fk_artist_type_artist
        FOREIGN KEY (artist_id) REFERENCES artists(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_artist_type_type
        FOREIGN KEY (type_id) REFERENCES types(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Quelques associations pour les tests
-- (à adapter selon les IDs réels de tes artists dans la BDD)
-- Si tu n'es pas sûr des IDs, lance la requête : SELECT id, firstname, lastname FROM artists;
-- et SELECT id, type FROM types;

-- Exemple : si tu as les artistes "Jean Dujardin" (id=1) et "Marion Cotillard" (id=2)
-- INSERT INTO artist_type (artist_id, type_id) VALUES (1, 1);  -- Jean = Acteur
-- INSERT INTO artist_type (artist_id, type_id) VALUES (2, 1);  -- Marion = Acteur
-- INSERT INTO artist_type (artist_id, type_id) VALUES (2, 2);  -- Marion = Metteur en scène aussi