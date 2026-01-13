ALTER TABLE remi.regulator_managed_information
DROP COLUMN resource_name,
DROP COLUMN hydrocarbon_case,
DROP COLUMN opportunity_type,
DROP COLUMN other_type_of_opportunity,
DROP COLUMN well_id,
DROP COLUMN well_reference;

ALTER TABLE remi.regulator_managed_information_aud
DROP COLUMN resource_name,
DROP COLUMN hydrocarbon_case,
DROP COLUMN opportunity_type,
DROP COLUMN other_type_of_opportunity,
DROP COLUMN well_id,
DROP COLUMN well_reference;
