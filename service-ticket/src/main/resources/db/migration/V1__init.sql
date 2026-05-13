CREATE TABLE tickets (
    id          BIGSERIAL    PRIMARY KEY,
    event_id    BIGINT       NOT NULL,
    seat_id     BIGINT       NOT NULL,
    user_id     BIGINT       NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    reserved_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    expires_at  TIMESTAMP    NOT NULL,
    CONSTRAINT uq_ticket_seat UNIQUE (event_id, seat_id)
);
