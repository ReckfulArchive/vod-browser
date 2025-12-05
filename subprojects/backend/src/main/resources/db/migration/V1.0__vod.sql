CREATE TABLE IF NOT EXISTS vod
(
    id           BIGSERIAL PRIMARY KEY,
    title        TEXT      NOT NULL,
    upload_ts    TIMESTAMP NOT NULL,
    duration_sec INTEGER   NOT NULL,
    description  TEXT,
    is_public    BOOLEAN   NOT NULL DEFAULT TRUE,
    external_id  TEXT
);

CREATE INDEX vod_title_idx ON vod (title);
CREATE INDEX vod_upload_ts_idx ON vod (upload_ts);
CREATE INDEX vod_duration_sec_idx ON vod (duration_sec);
CREATE INDEX vod_description_idx ON vod (description);
