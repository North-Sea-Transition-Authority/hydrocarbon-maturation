CREATE TABLE planned_sanctioned_wells (
    id UUID PRIMARY KEY NOT NULL,
    submission_detail_id UUID NOT NULL,
        CONSTRAINT planned_sanctioned_wells_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details(id),

    number_of_planned_sanctioned_wells INTEGER,
    total_capex NUMERIC,
    number_of_injectors INTEGER,
    number_of_producers INTEGER,
    number_of_monitors INTEGER,

    any_horizontal_wells BOOLEAN,
    number_of_horizontal_wells INTEGER,
    number_of_horizontal_wells_with_erd INTEGER,

    any_high_pressure_temperature_wells BOOLEAN,
    number_of_high_pressure_temperature_wells INTEGER,

    any_wells_within_narrow_or_tight_ppfg_window BOOLEAN,
    number_of_wells_within_narrow_or_tight_ppfg_window INTEGER,

    is_managed_pressure_drilling_utilized BOOLEAN,

    any_wellbore_stability_challenges BOOLEAN,
    number_of_wellbore_stability_challenges INTEGER,
    stability_challenges_information TEXT,

    any_wells_expected_to_cross_faults BOOLEAN,
    number_of_wells_expected_to_cross_faults INTEGER,

    any_expected_losses_from_drilling BOOLEAN,
    why_losses_expected TEXT,

    any_expected_drilling_challenges BOOLEAN,
    drilling_challenges_mitigations TEXT,

    stimulation_type_required TEXT,
    number_of_wells_requiring_stimulation INTEGER,

    is_expecting_formation_damage BOOLEAN,
    expected_formation_damage TEXT,

    any_heavy_oil BOOLEAN,

    any_specialised_drilling_equipment_required BOOLEAN,
    specialised_drilling_equipment_required TEXT
);
CREATE INDEX idx_planned_sanctioned_wells_on_submission_detail_id ON planned_sanctioned_wells(submission_detail_id);

CREATE TABLE planned_sanctioned_wells_faults (
     id UUID PRIMARY KEY NOT NULL,
     planned_sanctioned_wells_id UUID NOT NULL,
         CONSTRAINT planned_sanctioned_wells_faults_fk FOREIGN KEY (planned_sanctioned_wells_id) REFERENCES planned_sanctioned_wells(id),
     expected_fault TEXT
);
CREATE INDEX idx_psw_faults_on_psw_id ON planned_sanctioned_wells_faults(planned_sanctioned_wells_id);

CREATE TABLE hole_completion_types (
    id UUID PRIMARY KEY NOT NULL,
    planned_sanctioned_wells_id UUID NOT NULL,
        CONSTRAINT planned_sanctioned_wells_faults_fk FOREIGN KEY (planned_sanctioned_wells_id) REFERENCES planned_sanctioned_wells(id),
    hole_completion_type TEXT,
    other_hole_completion_type TEXT,
    number_of_wells_with_hole_completion_type INTEGER
);
CREATE INDEX idx_hole_completion_types_on_planned_sanctioned_wells_id ON hole_completion_types(planned_sanctioned_wells_id);

CREATE TABLE planned_completion_types (
    id UUID PRIMARY KEY NOT NULL,
    planned_sanctioned_wells_id UUID NOT NULL,
        CONSTRAINT planned_sanctioned_wells_faults_fk FOREIGN KEY (planned_sanctioned_wells_id) REFERENCES planned_sanctioned_wells(id),
    planned_completion_type TEXT,
    other_planned_completion_type TEXT,
    number_of_wells_with_planned_completion_type INTEGER
);
CREATE INDEX idx_planned_completion_types_on_planned_sanctioned_wells_id ON planned_completion_types(planned_sanctioned_wells_id);

CREATE TABLE artificial_lift_mechanisms (
    id UUID PRIMARY KEY NOT NULL,
    planned_sanctioned_wells_id UUID NOT NULL,
        CONSTRAINT planned_sanctioned_wells_faults_fk FOREIGN KEY (planned_sanctioned_wells_id) REFERENCES planned_sanctioned_wells(id),
    artificial_lift_mechanism TEXT,
    other_artificial_lift_mechanism TEXT,
    number_of_wells_with_artificial_lift_mechanism INTEGER
);
CREATE INDEX idx_artificial_lift_mechanisms_on_planned_sanctioned_wells_id ON artificial_lift_mechanisms(planned_sanctioned_wells_id);