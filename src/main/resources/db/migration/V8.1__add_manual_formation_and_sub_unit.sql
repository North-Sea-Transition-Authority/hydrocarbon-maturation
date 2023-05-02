ALTER TABLE source_and_seal_details
ADD COLUMN source_formation_name_manual TEXT;

ALTER TABLE source_and_seal_details
ADD COLUMN top_seal_formation_name_manual TEXT;

ALTER TABLE reservoir_properties
ADD COLUMN formation_name_manual TEXT;

ALTER TABLE reservoir_properties
ADD COLUMN sub_unit TEXT;

ALTER TABLE reservoir_properties
ADD COLUMN sub_unit_manual TEXT;