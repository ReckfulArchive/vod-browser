CREATE TABLE IF NOT EXISTS vod_mirrors
(
    vod_id BIGINT NOT NULL REFERENCES vod (id),
    type   TEXT   NOT NULL,
    url    TEXT   NOT NULL
);

CREATE INDEX IF NOT EXISTS vod_mirrors_vod_id_idx ON vod_mirrors (vod_id);
