CREATE TABLE oil_case_volumetric_totals (
    id UUID PRIMARY KEY,
    submission_detail_id UUID NOT NULL,
    field_volumetric_information_correct BOOLEAN,
    CONSTRAINT oil_case_volumetric_totals_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details (id)
);

CREATE INDEX oil_case_volumetric_totals_idx1 ON oil_case_volumetric_totals(submission_detail_id);