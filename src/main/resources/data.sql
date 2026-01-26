-- Roles
INSERT INTO roles (name)
SELECT 'ROLE_ADMIN'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name='ROLE_ADMIN');

INSERT INTO roles (name)
SELECT 'ROLE_USER'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name='ROLE_USER');

-- Admin user (ID-ni özün yazmırsan!)
INSERT INTO users (username, password, email)
SELECT 'admin',
       '$2a$10$ICfK43hiGzyFZc0tHs8GUuIM5nPmy78qehQYYXnzHEAE3tD.4MwbS',
       'admin@gmail.com'
    WHERE NOT EXISTS (SELECT 1 FROM users WHERE username='admin');

-- Admin role binding (DÜZGÜN VERSİYA)
INSERT INTO user_roles (user_id, role_id)
SELECT
    (SELECT id FROM users WHERE username='admin'),
    (SELECT id FROM roles WHERE name='ROLE_ADMIN')
    WHERE NOT EXISTS (
    SELECT 1 FROM user_roles
    WHERE user_id = (SELECT id FROM users WHERE username='admin')
      AND role_id = (SELECT id FROM roles WHERE name='ROLE_ADMIN')
);
