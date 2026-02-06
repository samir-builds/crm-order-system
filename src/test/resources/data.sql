DELETE FROM user_roles;
DELETE FROM users;
DELETE FROM roles;

INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
INSERT INTO roles (name) VALUES ('ROLE_USER');

INSERT INTO users (username, password, email)
VALUES (
           'admin',
           '$2a$10$Dow1GQJrXPX3yvGN28AL.eOHnqd7qV3CyMfCVxYvBy06SnVAk0nn6',
           'admin@gmail.com'
       );

INSERT INTO user_roles (user_id, role_id)
VALUES (
           (SELECT id FROM users WHERE username='admin'),
           (SELECT id FROM roles WHERE name='ROLE_ADMIN')
       );
