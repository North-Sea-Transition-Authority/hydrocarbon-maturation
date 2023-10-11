CREATE TABLE barrier_baffle (
    id UUID NOT NULL PRIMARY KEY,
    reservoir_properties_id UUID NOT NULL,
    barrier_baffle TEXT NOT NULL,
    vertical_lateral_type TEXT NOT NULL,

    CONSTRAINT barrier_baffle_fk
        FOREIGN KEY (reservoir_properties_id)
            REFERENCES reservoir_properties(id));

ALTER TABLE reservoir_properties
ADD COLUMN fracture_pressure INTEGER,
ADD COLUMN depositional_setting TEXT,
ADD COLUMN depositional_environment TEXT,
ADD COLUMN planned_producers INTEGER,
ADD COLUMN planned_injectors INTEGER,
ADD COLUMN is_lateral_barriers_baffles BOOLEAN,
ADD COLUMN is_vertical_barriers_baffles BOOLEAN;

