CREATE TABLE reservoir_hydrocarbons (
    id UUID PRIMARY KEY,
    reservoir_submission_detail_id UUID NOT NULL,
    hydrocarbons_discovered TEXT,
    CONSTRAINT reservoir_hydrocarbons_fk FOREIGN KEY (reservoir_submission_detail_id) REFERENCES reservoir_submission_details (id)
);