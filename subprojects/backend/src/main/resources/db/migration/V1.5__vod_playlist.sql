CREATE TABLE IF NOT EXISTS vod_playlist
(
    id          BIGSERIAL PRIMARY KEY,
    name        TEXT    NOT NULL,
    description TEXT    NOT NULL,
    is_public   BOOLEAN NOT NULL DEFAULT TRUE
);
