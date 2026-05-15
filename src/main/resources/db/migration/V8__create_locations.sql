-- ============================================
-- V8 : Création de la table locations
-- ============================================
-- Stocke les lieux de spectacle (théâtres, salles, etc.).
-- Chaque location appartient à une seule locality (ManyToOne).

CREATE TABLE locations (
    id           bigint(20)   NOT NULL AUTO_INCREMENT,
    slug         varchar(60)  NOT NULL UNIQUE,
    designation  varchar(60)  NOT NULL,
    address      varchar(255) NOT NULL,
    locality_id  bigint(20)   NOT NULL,
    website      varchar(255) DEFAULT NULL,
    phone        varchar(30)  DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_locations_locality
        FOREIGN KEY (locality_id) REFERENCES localities(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Quelques lieux bruxellois pour les tests
INSERT INTO locations (slug, designation, address, locality_id, website, phone) VALUES
    ('espace-delvaux',     'Espace Delvaux / La Vénerie', 'Rue Gratès 3',           5, 'https://www.lavenerie.be', '02/672.14.39'),
    ('theatre-140',        'Théâtre 140',                 'Avenue Eugène Plasky 140', 1, 'https://www.theatre140.be', '02/733.97.08'),
    ('halles-schaerbeek',  'Les Halles de Schaerbeek',    'Rue Royale Sainte-Marie 22', 1, 'https://www.halles.be', '02/218.21.07'),
    ('theatre-national',   'Théâtre National',            'Boulevard Émile Jacqmain 111-115', 1, 'https://www.theatrenational.be', '02/203.41.55'),
    ('rideau-bruxelles',   'Le Rideau de Bruxelles',      'Rue Goffart 7a',          2, 'https://www.rideaudebruxelles.be', '02/737.16.01');