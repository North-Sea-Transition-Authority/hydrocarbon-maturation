CREATE TABLE reservoir_submission_section (
    id UUID PRIMARY KEY,
    reservoir_submission_detail_id UUID NOT NULL,
    reservoir_submission_section_type TEXT NOT NULL,

    CONSTRAINT reservoir_section_fk FOREIGN KEY (reservoir_submission_detail_id) REFERENCES reservoir_submission_details(id)
);

CREATE INDEX idx_reservoir_section_on_reservoir_submission_id ON reservoir_submission_section(reservoir_submission_detail_id);