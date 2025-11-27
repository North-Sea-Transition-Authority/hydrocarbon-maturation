ALTER TABLE remi.regulator_managed_information
ADD COLUMN opportunity_type TEXT;

ALTER TABLE remi.regulator_managed_information
ADD COLUMN other_type_of_opportunity TEXT;

ALTER TABLE remi.regulator_managed_information_aud
ADD COLUMN opportunity_type TEXT;

ALTER TABLE remi.regulator_managed_information_aud
ADD COLUMN other_type_of_opportunity TEXT;