CREATE TABLE probability_distributions (
    id UUID PRIMARY KEY,
    submission_detail_id UUID NOT NULL,
    distribution_type TEXT,
    instance TEXT,
    min_value DECIMAL,
    p99 DECIMAL,
    p90 DECIMAL,
    mode_value DECIMAL,
    p50 DECIMAL,
    mean_value DECIMAL,
    p10 DECIMAL,
    p1 DECIMAL,
    max_value DECIMAL,
    constant_value DECIMAL,
    CONSTRAINT probability_distributions_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details (id)
);

CREATE INDEX probability_distributions_idx1 ON probability_distributions(submission_detail_id, instance);