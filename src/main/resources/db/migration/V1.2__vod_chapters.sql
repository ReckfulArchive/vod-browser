CREATE TABLE IF NOT EXISTS vod_chapters
(
    vod_id     BIGINT  NOT NULL REFERENCES vod (id),
    chapter_id BIGINT  NOT NULL REFERENCES vod_chapter (id),
    start_time INTEGER NOT NULL NOT NULL,

    CONSTRAINT vod_id_start_time_unique UNIQUE (vod_id, start_time)
);

CREATE INDEX IF NOT EXISTS vod_chapters_vod_id_idx ON vod_chapters (vod_id);
CREATE INDEX IF NOT EXISTS vod_chapters_chapter_id_idx ON vod_chapters (chapter_id);
