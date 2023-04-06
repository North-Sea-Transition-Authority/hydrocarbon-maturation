CREATE TABLE submissions (
    id UUID PRIMARY KEY,
    resource_id UUID NOT NULL,
    CONSTRAINT submissions_fk FOREIGN KEY (resource_id) REFERENCES resources (id)
);

CREATE INDEX submissions_idx1 ON submissions (resource_id);

ALTER TABLE submission_details
    ADD COLUMN submission_id UUID,
    ADD CONSTRAINT submission_details_fk2 FOREIGN KEY (submission_id) REFERENCES submissions (id);

CREATE INDEX submission_details_idx2 ON submission_details (submission_id);