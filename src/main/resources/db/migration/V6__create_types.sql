-- ============================================
-- V6 : Création de la table types
-- ============================================
-- Stocke les rôles possibles d'un artiste sur un spectacle :
-- Acteur, Metteur en scène, Technicien, Scénographe, etc.

CREATE TABLE types (
    id     bigint(20)  NOT NULL AUTO_INCREMENT,
    type   varchar(60) NOT NULL UNIQUE,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertion des types de base
INSERT INTO types (type) VALUES ('Acteur');
INSERT INTO types (type) VALUES ('Metteur en scène');
INSERT INTO types (type) VALUES ('Scénariste');
INSERT INTO types (type) VALUES ('Technicien');
INSERT INTO types (type) VALUES ('Scénographe');
INSERT INTO types (type) VALUES ('Costumier');
INSERT INTO types (type) VALUES ('Compositeur');
INSERT INTO types (type) VALUES ('Chorégraphe');