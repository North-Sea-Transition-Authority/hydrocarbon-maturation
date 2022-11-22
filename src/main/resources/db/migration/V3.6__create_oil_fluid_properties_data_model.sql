CREATE TABLE oil_fluid_properties (
    id UUID PRIMARY KEY,
    submission_detail_id UUID NOT NULL,
    oil_gravity INTEGER,
    oil_viscosity DECIMAL,
    oil_sulphur INTEGER,
    oil_wax DECIMAL,
    oil_other_wax_appearance_temperature INTEGER,
    CONSTRAINT oil_fluid_properties_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details (id)
);

CREATE INDEX oil_fluid_properties_idx1 ON oil_fluid_properties(submission_detail_id);