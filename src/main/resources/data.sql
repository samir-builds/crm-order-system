-- Roles
INSERT INTO roles (name)
SELECT 'ROLE_ADMIN'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name='ROLE_ADMIN');

INSERT INTO roles (name)
SELECT 'ROLE_USER'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name='ROLE_USER');

-- Admin user (password = admin)
INSERT INTO users (username, password, email)
SELECT 'admin',
       '$2a$12$/N0oUkkBG4StfDWW967IE.Z5V3QTvBwiZu6gFUpd7b2QA3XYIVVy6',
       'admin@gmail.com'
    WHERE NOT EXISTS (SELECT 1 FROM users WHERE username='admin');

-- Admin role binding
INSERT INTO user_roles (user_id, role_id)
SELECT
    (SELECT id FROM users WHERE username='admin'),
    (SELECT id FROM roles WHERE name='ROLE_ADMIN')
    WHERE NOT EXISTS (
    SELECT 1 FROM user_roles
    WHERE user_id = (SELECT id FROM users WHERE username='admin')
      AND role_id = (SELECT id FROM roles WHERE name='ROLE_ADMIN')
);
