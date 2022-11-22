CREATE TABLE general_fluid_properties (
    id UUID PRIMARY KEY,
    submission_detail_id UUID NOT NULL,
    datum_depth DECIMAL,
    temperature_at_datum_depth INTEGER,
    pressure_at_datum_depth INTEGER,
    saturation_pressure_at_datum_depth INTEGER,
    water_salinity INTEGER,
    CONSTRAINT general_fluid_properties_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details (id)
);

CREATE INDEX general_fluid_properties_idx1 ON general_fluid_properties(submission_detail_id);