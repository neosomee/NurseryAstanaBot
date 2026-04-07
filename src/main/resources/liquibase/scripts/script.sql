--liquibase formatted sql

--changeset neosome:001-create-users
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       telegram_id BIGINT UNIQUE NOT NULL,
                       username VARCHAR(255)
);

--rollback DROP TABLE users;

--changeset neosome:002-create-reports
CREATE TABLE reports (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         text TEXT,
                         photo_url VARCHAR(500),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_reports_user_id ON reports(user_id);
CREATE INDEX idx_reports_created_at ON reports(created_at DESC);

--rollback DROP TABLE reports;