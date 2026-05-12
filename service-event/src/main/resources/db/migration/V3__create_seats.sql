CREATE TABLE seats (
    id          BIGSERIAL PRIMARY KEY,
    venue_id    BIGINT      NOT NULL REFERENCES venues (id),
    row_label   VARCHAR(10) NOT NULL,
    seat_number INT         NOT NULL CHECK (seat_number > 0),
    status      VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    CONSTRAINT uq_seat UNIQUE (venue_id, row_label, seat_number)
);
