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
