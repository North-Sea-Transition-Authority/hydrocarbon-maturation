CREATE TABLE reservoir_volumetrics (
    id UUID PRIMARY KEY,
    reservoir_submission_detail_id UUID NOT NULL,
    initially_in_place_correct BOOLEAN,
    CONSTRAINT reservoir_hydrocarbons_fk FOREIGN KEY (reservoir_submission_detail_id) REFERENCES reservoir_submission_details (id)
);