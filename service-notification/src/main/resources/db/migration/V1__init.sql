CREATE TABLE notification_logs (
    id          BIGSERIAL    PRIMARY KEY,
    type        VARCHAR(10)  NOT NULL,
    recipient   VARCHAR(255) NOT NULL,
    subject     VARCHAR(500),
    body        TEXT,
    status      VARCHAR(10)  NOT NULL,
    sent_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);
