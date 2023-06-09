ALTER TABLE source_and_seal_details
ADD COLUMN source_age_stage_manual TEXT;

ALTER TABLE source_and_seal_details
ADD COLUMN top_seal_age_stage_manual TEXT;

ALTER TABLE reservoir_properties
ADD COLUMN age_stage_manual TEXT;
