-- Flyway migration: create api_logs table
CREATE TABLE IF NOT EXISTS api_logs (
    id bigserial PRIMARY KEY,
    endpoint varchar(255),
    method varchar(10),
    request_time timestamp with time zone,
    response_status integer,
    duration_ms bigint,
    request_body text,
    username varchar(100)
);

