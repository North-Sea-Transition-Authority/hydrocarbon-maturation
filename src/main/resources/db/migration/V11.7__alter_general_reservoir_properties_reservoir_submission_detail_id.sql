DELETE FROM general_fluid_properties;

ALTER TABLE general_fluid_properties
    DROP COLUMN submission_detail_id;

ALTER TABLE general_fluid_properties
    ADD COLUMN reservoir_submission_detail_id UUID NOT NULL,
    ADD COLUMN initial_temperature INTEGER,
    ADD COLUMN initial_pressure INTEGER;

ALTER TABLE general_fluid_properties
    ADD CONSTRAINT general_fluid_properties_fk FOREIGN KEY (reservoir_submission_detail_id) REFERENCES reservoir_submission_details (id);

