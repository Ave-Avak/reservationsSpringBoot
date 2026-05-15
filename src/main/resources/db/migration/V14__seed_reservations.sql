-- ============================================
-- V14 : Réservations de test pour l'admin
-- ============================================
-- Crée quelques réservations pour le user "admin" (id=1) afin de tester
-- la page "Mes réservations" du profil.

-- IMPORTANT : si ton admin n'a pas l'id=1, ajuste la requête.
-- Pour vérifier : SELECT id, login FROM users WHERE login = 'admin';

-- 3 réservations pour l'admin sur différents spectacles
INSERT INTO reservations (user_id, representation_id, places, booking_date, status) VALUES
    -- 2 places pour Ayiti le 12/10/2026 (rep id=1)
    (1, 1, 2, '2026-05-10 14:30:00', 'CONFIRMED'),
    -- 4 places pour Le Petit Prince le 19/10/2026 14h30 (rep id=6)
    (1, 6, 4, '2026-05-12 09:15:00', 'CONFIRMED'),
    -- 1 place pour Hamlet le 15/11/2026 (rep id=9), en attente
    (1, 9, 1, '2026-05-15 22:00:00', 'PENDING');

-- Si tu as créé un user "john" lors des tests d'inscription au Ch.4,
-- décommente ces lignes (et ajuste l'id du user) pour lui aussi :
-- INSERT INTO reservations (user_id, representation_id, places, booking_date, status) VALUES
--     (2, 4, 3, '2026-05-14 18:00:00', 'CONFIRMED'),   -- 3 places Ayiti au Théâtre 140
--     (2, 12, 2, '2026-05-15 20:30:00', 'PENDING');    -- 2 places Cabaret