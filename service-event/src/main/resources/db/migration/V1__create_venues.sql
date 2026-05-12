CREATE TABLE venues (
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    address  TEXT         NOT NULL,
    capacity INT          NOT NULL CHECK (capacity > 0)
);
