CREATE TABLE IF NOT EXISTS vod_chapter
(
    id         BIGSERIAL PRIMARY KEY,
    name       TEXT NOT NULL UNIQUE,
    popularity INT  NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS vod_chapter_name_idx ON vod_chapter (name);
