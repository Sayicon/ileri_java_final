INSERT INTO users (username, email, password_hash, role_id)
VALUES (
    'admin1',
    'admin1@tbl324.com',
    '$2a$10$IsG56lGmxQ/fOV/wO4y0D.QxZSWo0lGlvBTmddmtnoVKmn1Qid.Qu',
    (SELECT id FROM roles WHERE name = 'ADMIN')
)
ON CONFLICT (username) DO UPDATE
    SET role_id = (SELECT id FROM roles WHERE name = 'ADMIN');
