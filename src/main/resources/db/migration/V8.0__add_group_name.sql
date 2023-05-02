ALTER TABLE source_and_seal_details
ADD COLUMN source_group_name TEXT;

ALTER TABLE source_and_seal_details
ADD COLUMN top_seal_group_name TEXT;

ALTER TABLE reservoir_properties
ADD COLUMN group_name TEXT;