INSERT INTO venues (name, address, capacity) VALUES
    ('Kocaeli Kongre Merkezi', 'İzmit, Kocaeli', 500),
    ('Teknoloji Amfisi', 'KÜ Umuttepe Yerleşkesi', 200);

INSERT INTO events (title, description, venue_id, start_time, end_time, total_seats, available_seats, status) VALUES
    ('Java 21 Yenilikleri Semineri', 'Java 21 ile gelen record pattern, virtual thread ve daha fazlası.',
     1, NOW() + INTERVAL '7 days', NOW() + INTERVAL '7 days' + INTERVAL '3 hours', 500, 500, 'ACTIVE'),
    ('Spring Boot Workshop', 'Mikroservis mimarisi ve Spring Boot 3 uygulamalı eğitim.',
     2, NOW() + INTERVAL '14 days', NOW() + INTERVAL '14 days' + INTERVAL '4 hours', 200, 200, 'ACTIVE');

INSERT INTO seats (venue_id, row_label, seat_number, status)
SELECT 2, chr(64 + r), s, 'AVAILABLE'
FROM generate_series(1, 10) AS r,
     generate_series(1, 20) AS s;
