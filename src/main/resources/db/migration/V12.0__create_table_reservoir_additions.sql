CREATE TABLE reservoir_additions (
    id UUID PRIMARY KEY NOT NULL,
    submission_detail_id UUID NOT NULL,
    reservoir_addition_status TEXT,

    CONSTRAINT reservoir_additions_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details(id)
);

CREATE INDEX reservoir_additions_idx ON reservoir_additions(submission_detail_id);
