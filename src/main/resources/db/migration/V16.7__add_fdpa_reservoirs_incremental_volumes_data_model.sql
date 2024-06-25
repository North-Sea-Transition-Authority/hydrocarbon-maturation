CREATE TABLE fdpa_reservoir_incremental_volumes (
    id                             UUID PRIMARY KEY,
    reservoir_submission_detail_id UUID NOT NULL,
    change_to_in_place_volumes     BOOLEAN,
    comment                        TEXT,

    CONSTRAINT fdpa_reservoir_incremental_volumes_fk FOREIGN KEY (reservoir_submission_detail_id) REFERENCES reservoir_submission_details(id)
);

CREATE INDEX fdpa_reservoir_incremental_volumes_idx ON fdpa_reservoir_incremental_volumes(reservoir_submission_detail_id);