CREATE TABLE resource_details (
    id UUID PRIMARY KEY,
    submission_detail_id UUID NOT NULL,
    resource_name TEXT,
    onshore_offshore TEXT,
    organisation_id INTEGER,
    organisation_name TEXT,
    organisation_registered_number TEXT,
    CONSTRAINT resource_details_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details (id)

);

CREATE INDEX resource_details_idx1 ON resource_details(submission_detail_id);

ALTER TABLE general_details
    DROP COLUMN resource_name,
    DROP COLUMN onshore_offshore,
    DROP COLUMN organisation_id,
    DROP COLUMN organisation_name,
    DROP COLUMN organisation_registered_number;