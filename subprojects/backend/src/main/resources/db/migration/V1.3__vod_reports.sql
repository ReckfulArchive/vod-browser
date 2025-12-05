CREATE TABLE IF NOT EXISTS vod_reports
(
    vod_id  BIGINT NOT NULL REFERENCES vod (id),
    type    TEXT   NOT NULL,
    message TEXT
);

CREATE INDEX IF NOT EXISTS vod_report_vod_id_idx ON vod_reports (vod_id);
CREATE INDEX IF NOT EXISTS vod_report_type_idx ON vod_reports (type);
