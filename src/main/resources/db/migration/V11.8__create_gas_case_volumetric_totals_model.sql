CREATE TABLE gas_case_volumetric_totals (
    id UUID PRIMARY KEY,
    submission_detail_id UUID NOT NULL,
    has_associated_condensate_recoverable_resources BOOLEAN,
    CONSTRAINT gas_case_volumetric_totals_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details (id)
);

CREATE INDEX gas_case_volumetric_totals_idx1 ON gas_case_volumetric_totals(submission_detail_id);