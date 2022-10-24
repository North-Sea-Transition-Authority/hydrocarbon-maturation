CREATE TABLE mixed_case_outputs (
    id UUID PRIMARY KEY,
    submission_detail_id UUID NOT NULL,
    risked_oil_equivalent_recoverable_mean NUMERIC,
    has_associated_gas_recoverable_resources BOOLEAN,
    CONSTRAINT mixed_case_outputs_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details (id)
);

CREATE INDEX mixed_case_outputs_idx1 ON mixed_case_outputs(submission_detail_id);