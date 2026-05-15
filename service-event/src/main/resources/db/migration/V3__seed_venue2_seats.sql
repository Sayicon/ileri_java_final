-- Harbiye Açık Hava (venue_id=2) için koltuklar: 20 sıra × 50 koltuk = 1000
INSERT INTO seats (venue_id, row_label, seat_number, status)
SELECT 2, chr(64 + row_num), seat_num, 'AVAILABLE'
FROM generate_series(1, 20) AS row_num,
     generate_series(1, 50) AS seat_num
ON CONFLICT DO NOTHING;

-- Event 2'nin koltuk sayısını gerçek değerle güncelle
UPDATE events SET total_seats = 1000, available_seats = 1000 WHERE id = 2;
