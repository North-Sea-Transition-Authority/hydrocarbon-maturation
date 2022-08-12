CREATE TABLE reservoir_properties (
    id UUID PRIMARY KEY,
    submission_detail_id UUID NOT NULL,
    formation_name TEXT,
    age_stage TEXT,
    lithology TEXT,
    CONSTRAINT reservoir_properties_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details (id)
);

CREATE INDEX reservoir_properties_idx1 ON reservoir_properties(submission_detail_id);