CREATE TABLE hydrocarbon_case_details (
    id UUID PRIMARY KEY,
    submission_detail_id UUID NOT NULL,
    oil_only BOOLEAN,
    oil_only_probability DECIMAL,
    oil_only_comments TEXT,
    gas_only BOOLEAN,
    gas_only_probability DECIMAL,
    gas_only_comments TEXT,
    oil_and_gas BOOLEAN,
    oil_and_gas_probability DECIMAL,
    oil_and_gas_comments TEXT,
    total_probability DECIMAL,
    CONSTRAINT hydrocarbon_case_details_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details (id)
);

CREATE INDEX hydrocarbon_case_details_idx1 ON hydrocarbon_case_details(submission_detail_id);