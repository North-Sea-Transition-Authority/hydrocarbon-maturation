DELETE FROM gas_fluid_properties;

ALTER TABLE gas_fluid_properties
    DROP COLUMN submission_detail_id;

ALTER TABLE gas_fluid_properties
    ADD COLUMN reservoir_submission_detail_id UUID NOT NULL;

ALTER TABLE gas_fluid_properties
    ADD CONSTRAINT gas_fluid_properties_fk FOREIGN KEY (reservoir_submission_detail_id) REFERENCES reservoir_submission_details (id);

