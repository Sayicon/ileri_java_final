CREATE TABLE venues (
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    address  VARCHAR(500) NOT NULL,
    capacity INT          NOT NULL CHECK (capacity > 0)
);

CREATE TABLE events (
    id               BIGSERIAL PRIMARY KEY,
    title            VARCHAR(255) NOT NULL,
    description      TEXT,
    venue_id         BIGINT       NOT NULL REFERENCES venues (id),
    start_time       TIMESTAMP    NOT NULL,
    end_time         TIMESTAMP    NOT NULL,
    total_seats      INT          NOT NULL CHECK (total_seats > 0),
    available_seats  INT          NOT NULL CHECK (available_seats >= 0),
    status           VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT chk_event_times CHECK (end_time > start_time)
);

CREATE TABLE seats (
    id          BIGSERIAL PRIMARY KEY,
    venue_id    BIGINT      NOT NULL REFERENCES venues (id),
    row_label   VARCHAR(10) NOT NULL,
    seat_number INT         NOT NULL CHECK (seat_number > 0),
    status      VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    CONSTRAINT uq_seat UNIQUE (venue_id, row_label, seat_number)
);
