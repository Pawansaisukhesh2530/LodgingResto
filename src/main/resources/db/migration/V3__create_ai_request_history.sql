-- Flyway migration: create ai_request_history table
CREATE TABLE IF NOT EXISTS ai_request_history (
    id bigserial PRIMARY KEY,
    prompt text,
    response text,
    timestamp timestamp with time zone,
    username varchar(100),
    category varchar(50)
);
