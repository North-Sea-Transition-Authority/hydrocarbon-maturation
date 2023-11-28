CREATE TABLE considering_ccus_detail (
    id UUID PRIMARY KEY NOT NULL,
    submission_detail_id UUID NOT NULL,
    is_connected_to_project BOOLEAN,
    connected_projects TEXT,
    is_any_shallower_horizons BOOLEAN,
    age_and_formation_of_horizons TEXT,

    CONSTRAINT considering_ccus_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details(id)
);

CREATE INDEX idx_considering_ccus_on_submission_detail_id ON considering_ccus_detail(submission_detail_id);