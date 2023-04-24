CREATE TABLE submission_update (
    id UUID PRIMARY KEY,
    submission_id UUID NOT NULL,
    requested_timestamp TIMESTAMP,
    requested_by_wua_id INTEGER,
    reason TEXT,
    resolved BOOLEAN,
    resolved_timestamp TIMESTAMP,
    resolved_by_wua_id INTEGER
);

CREATE INDEX idx_submission_update1 ON submission_update(id);
CREATE INDEX idx_submission_update2 ON submission_update(submission_id);