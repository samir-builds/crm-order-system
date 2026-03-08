INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_USER');

INSERT INTO users (id, username, password, email)
VALUES (1, 'admin',
        '$2a$12$/N0oUkkBG4StfDWW967IE.Z5V3QTvBwiZu6gFUpd7b2QA3XYIVVy6',
        'admin@gmail.com');

INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
