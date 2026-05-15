-- ============================================
-- V5 : Insertion des 3 rôles métier du PID
-- ============================================
-- ROLE_USER et ROLE_ADMIN sont déjà créés par la migration V2
-- On ajoute les 3 rôles spécifiques aux profils métier du cahier des charges :
--   - ROLE_AFFILIATE : membre affilié, accès à l'API selon niveau (Free/Starter/Premium)
--   - ROLE_PRESS     : critique de presse, peut soumettre des articles
--   - ROLE_PRODUCER  : producteur, voit stats et modère les commentaires

INSERT INTO roles (role) VALUES ('ROLE_AFFILIATE');
INSERT INTO roles (role) VALUES ('ROLE_PRESS');
INSERT INTO roles (role) VALUES ('ROLE_PRODUCER');