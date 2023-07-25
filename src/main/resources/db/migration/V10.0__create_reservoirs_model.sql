CREATE TABLE reservoirs (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE reservoir_submission_details (
    id UUID PRIMARY KEY,
    reservoir UUID NOT NULL,
    submission_detail_id UUID NOT NULL,
    CONSTRAINT reservoir_submission_details_fk1 FOREIGN KEY (reservoir) REFERENCES reservoirs (id),
    CONSTRAINT reservoir_submission_details_fk2 FOREIGN KEY (submission_detail_id) REFERENCES submission_details (id)
);

CREATE INDEX reservoir_submission_details_idx1 ON reservoir_submission_details(reservoir);
CREATE INDEX reservoir_submission_details_idx2 ON reservoir_submission_details(submission_detail_id);