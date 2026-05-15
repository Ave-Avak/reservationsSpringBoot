-- ============================================
-- V7 : Création de la table localities
-- ============================================
-- Stocke les couples (code postal, ville).
-- Une locality sera référencée par plusieurs locations (théâtres).
-- Ex: "1170 Watermael-Boitsfort" peut contenir Espace Delvaux + autre salle.

CREATE TABLE localities (
    id          bigint(20)  NOT NULL AUTO_INCREMENT,
    postal_code varchar(6)  NOT NULL,
    locality    varchar(60) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_postal_locality (postal_code, locality)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertion de quelques localités belges pour les tests
INSERT INTO localities (postal_code, locality) VALUES ('1000', 'Bruxelles');
INSERT INTO localities (postal_code, locality) VALUES ('1050', 'Ixelles');
INSERT INTO localities (postal_code, locality) VALUES ('1060', 'Saint-Gilles');
INSERT INTO localities (postal_code, locality) VALUES ('1080', 'Molenbeek-Saint-Jean');
INSERT INTO localities (postal_code, locality) VALUES ('1170', 'Watermael-Boitsfort');
INSERT INTO localities (postal_code, locality) VALUES ('1180', 'Uccle');
INSERT INTO localities (postal_code, locality) VALUES ('1190', 'Forest');
INSERT INTO localities (postal_code, locality) VALUES ('4000', 'Liège');
INSERT INTO localities (postal_code, locality) VALUES ('5000', 'Namur');
INSERT INTO localities (postal_code, locality) VALUES ('7000', 'Mons');