INSERT INTO venues (name, address, capacity) VALUES
    ('Atatürk Kültür Merkezi', 'Taksim Meydanı, İstanbul', 500),
    ('Harbiye Açık Hava', 'Harbiye, İstanbul', 2000);

INSERT INTO events (title, description, venue_id, start_time, end_time, total_seats, available_seats, status) VALUES
    ('Spring Boot Workshop', 'İleri seviye Spring Boot eğitimi', 1,
     NOW() + INTERVAL '7 days', NOW() + INTERVAL '7 days' + INTERVAL '3 hours', 500, 500, 'ACTIVE'),
    ('Java Konferansı 2026', 'Yıllık Java geliştirici konferansı', 2,
     NOW() + INTERVAL '14 days', NOW() + INTERVAL '14 days' + INTERVAL '8 hours', 2000, 2000, 'ACTIVE');

INSERT INTO seats (venue_id, row_label, seat_number, status)
SELECT 1, chr(64 + row_num), seat_num, 'AVAILABLE'
FROM generate_series(1, 10) AS row_num,
     generate_series(1, 50) AS seat_num;
