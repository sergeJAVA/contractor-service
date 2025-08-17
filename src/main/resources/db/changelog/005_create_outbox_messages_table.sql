CREATE TABLE IF NOT EXISTS outbox_messages(
    message_id UUID PRIMARY KEY,
    payload TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    sent_at TIMESTAMP NOT NULL
);