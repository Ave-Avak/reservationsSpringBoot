-- ============================================
-- V9 : Création de la table shows
-- ============================================
-- Stocke les spectacles (pièce centrale du projet).
-- Chaque spectacle a un lieu de création (location).

CREATE TABLE shows (
    id           bigint(20)    NOT NULL AUTO_INCREMENT,
    slug         varchar(60)   NOT NULL UNIQUE,
    title        varchar(255)  NOT NULL,
    description  text          DEFAULT NULL,
    poster_url   varchar(255)  DEFAULT NULL,
    duration     smallint(5)   UNSIGNED DEFAULT NULL,
    created_in   year(4)       DEFAULT NULL,
    location_id  bigint(20)    NOT NULL,
    bookable     tinyint(1)    NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT fk_shows_location
        FOREIGN KEY (location_id) REFERENCES locations(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Quelques spectacles pour les tests
INSERT INTO shows (slug, title, description, poster_url, duration, created_in, location_id, bookable) VALUES
    ('ayiti',
     'Ayiti',
     'Création percutante mêlant théâtre, danse et musique, qui interroge l''identité et la mémoire haïtienne contemporaine.',
     'https://via.placeholder.com/300x450/8B4513/FFFFFF?text=Ayiti',
     90, 2023, 1, 1),
    ('le-petit-prince',
     'Le Petit Prince',
     'Adaptation théâtrale du chef-d''œuvre de Saint-Exupéry. Pour petits et grands rêveurs.',
     'https://via.placeholder.com/300x450/4169E1/FFFFFF?text=Le+Petit+Prince',
     75, 2024, 2, 1),
    ('hamlet',
     'Hamlet',
     'La tragédie shakespearienne dans une mise en scène contemporaine et radicale.',
     'https://via.placeholder.com/300x450/2F4F4F/FFFFFF?text=Hamlet',
     150, 2022, 3, 1),
    ('cabaret',
     'Cabaret',
     'Comédie musicale culte dans le Berlin décadent des années 30.',
     'https://via.placeholder.com/300x450/8B0000/FFFFFF?text=Cabaret',
     135, 2024, 4, 1),
    ('huis-clos',
     'Huis Clos',
     'L''enfer, c''est les autres. La pièce mythique de Sartre, courte et brutale.',
     'https://via.placeholder.com/300x450/000000/FFFFFF?text=Huis+Clos',
     90, 2025, 5, 0);