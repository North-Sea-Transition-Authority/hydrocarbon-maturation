CREATE TABLE field_complexity (
    id UUID PRIMARY KEY NOT NULL,
    submission_detail_id UUID NOT NULL,
    viscosity_range TEXT,
    vertical_heterogeneity_range TEXT,
    areal_density_range TEXT,
    structural_complexity_range TEXT,
    calculated_field_complexity NUMERIC,

    CONSTRAINT field_complexity_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details(id)
);

CREATE INDEX field_complexity_idx1 ON field_complexity(submission_detail_id);