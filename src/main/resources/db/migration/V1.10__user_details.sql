CREATE TABLE IF NOT EXISTS user_details
(
    id               BIGSERIAL PRIMARY KEY,
    name             TEXT NOT NULL UNIQUE,
    password_encoded TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS user_authorities
(
    user_id   BIGINT NOT NULL REFERENCES user_details (id),
    authority TEXT   NOT NULL
);

CREATE INDEX IF NOT EXISTS user_authorities_user_id_idx ON user_authorities (user_id);
