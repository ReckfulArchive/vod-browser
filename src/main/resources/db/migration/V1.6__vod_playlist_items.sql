CREATE TABLE IF NOT EXISTS vod_playlist_items
(
    playlist_id BIGINT NOT NULL REFERENCES vod_playlist (id),
    vod_id BIGINT NOT NULL REFERENCES vod (id)
);

CREATE INDEX IF NOT EXISTS vod_playlist_items_playlist_id_idx ON vod_playlist_items (playlist_id);
CREATE INDEX IF NOT EXISTS vod_playlist_items_vod_id_idx ON vod_playlist_items (vod_id);
