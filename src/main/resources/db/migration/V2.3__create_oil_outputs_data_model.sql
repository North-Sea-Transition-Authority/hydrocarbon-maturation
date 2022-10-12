CREATE TABLE oil_case_outputs (
    id UUID PRIMARY KEY,
    submission_detail_id UUID NOT NULL,
    risked_oil_equivalent_recoverable_mean NUMERIC,
    CONSTRAINT oil_case_outputs_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details (id)
);

CREATE INDEX oil_case_outputs_idx1 ON oil_case_outputs(submission_detail_id);