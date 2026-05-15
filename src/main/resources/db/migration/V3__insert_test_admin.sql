-- ============================================
-- V3 : Utilisateur admin de test
-- Login    : admin
-- Email    : admin@test.local
-- Password : password   (hashé en BCrypt)
-- ============================================

-- Insertion de l'utilisateur admin
INSERT INTO users (login, email, password, firstname, lastname, enabled, created_at)
VALUES (
    'admin',
    'admin@test.local',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'Admin',
    'Test',
    TRUE,
    CURRENT_TIMESTAMP
);

-- Association admin <-> ROLE_ADMIN
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.login = 'admin' AND r.role = 'ROLE_ADMIN';

-- Bonus : on lui donne aussi le ROLE_USER (un admin est aussi un utilisateur)
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.login = 'admin' AND r.role = 'ROLE_USER';