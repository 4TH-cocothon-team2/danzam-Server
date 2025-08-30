-- 유저 1명 (UUID 고정)
INSERT INTO users (user_pk, weight_kg, age, smoker, pregnant, meds)
VALUES ('550e8400-e29b-41d4-a716-446655440000', 60.0, 23, 0, 0, 0)
ON DUPLICATE KEY UPDATE user_pk=user_pk;

-- 오늘 섭취 2건(09:00, 14:00)
INSERT INTO intakes (user_pk, mg, taken_at)
VALUES
('550e8400-e29b-41d4-a716-446655440000', 200, CONCAT(CURDATE(),' 09:00:00')),
('550e8400-e29b-41d4-a716-446655440000', 150, CONCAT(CURDATE(),' 14:00:00'));
