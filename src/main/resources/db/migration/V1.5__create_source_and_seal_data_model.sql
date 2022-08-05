CREATE TABLE source_and_seal_details (
    id UUID PRIMARY KEY,
    submission_detail_id UUID NOT NULL,
    source_formation_name TEXT,
    source_age_stage TEXT,
    source_lithology TEXT,
    top_seal_formation_name TEXT,
    top_seal_age_stage TEXT,
    top_seal_lithology TEXT,
    CONSTRAINT source_and_seal_details_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details (id)
);

CREATE INDEX source_and_seal_details_idx1 ON source_and_seal_details(submission_detail_id);