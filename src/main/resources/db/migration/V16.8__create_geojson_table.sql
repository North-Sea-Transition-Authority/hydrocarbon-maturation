CREATE TABLE submission_geojson (
    id UUID PRIMARY KEY NOT NULL,
    submission_detail_id UUID NOT NULL,
    geojson JSONB NOT NULL,

    CONSTRAINT submission_geojson_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details(id)
);

CREATE INDEX submission_geojson_idx ON submission_geojson(submission_detail_id);