CREATE TABLE gas_fluid_properties (
    id UUID PRIMARY KEY,
    submission_detail_id UUID NOT NULL,
    gas_specific_gravity DECIMAL,
    gas_h2s DECIMAL,
    gas_co2 DECIMAL,
    gas_n2 DECIMAL,
    condensate_present BOOLEAN,
    condensate_gravity DECIMAL,
    condensate_viscosity DECIMAL,
    condensate_sulphur DECIMAL,
    condensate_wax DECIMAL,
    condensate_other_incl_wax DECIMAL,
    advise_other_gases_concentration TEXT,
    CONSTRAINT gas_fluid_properties_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details (id)
);

CREATE INDEX gas_fluid_properties_idx1 ON gas_fluid_properties(submission_detail_id);