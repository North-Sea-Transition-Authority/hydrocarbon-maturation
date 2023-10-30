CREATE TABLE combined_case_volumetric_totals (
    id UUID PRIMARY KEY,
    submission_detail_id UUID NOT NULL,
    has_associated_condensate_recoverable_resources BOOLEAN,
    CONSTRAINT combined_case_volumetric_totals_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details (id)
);

CREATE INDEX combined_case_volumetric_totals_idx1 ON combined_case_volumetric_totals(submission_detail_id);