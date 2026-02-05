-- Roles
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
INSERT INTO roles (name) VALUES ('ROLE_USER');

-- Admin user (password = admin)
INSERT INTO users (username, password, email)
VALUES ('admin', '{noop}admin', 'admin@gmail.com');

-- Admin role binding
INSERT INTO user_roles (user_id, role_id)
VALUES (
           (SELECT id FROM users WHERE username='admin'),
           (SELECT id FROM roles WHERE name='ROLE_ADMIN')
       );
