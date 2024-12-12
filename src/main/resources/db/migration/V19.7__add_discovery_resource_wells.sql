ALTER TABLE regulator_managed_information
ADD COLUMN well_id INTEGER;

ALTER TABLE regulator_managed_information
ADD COLUMN well_reference TEXT;

ALTER TABLE regulator_managed_information_aud
ADD COLUMN well_id INTEGER;

ALTER TABLE regulator_managed_information_aud
ADD COLUMN well_reference TEXT;