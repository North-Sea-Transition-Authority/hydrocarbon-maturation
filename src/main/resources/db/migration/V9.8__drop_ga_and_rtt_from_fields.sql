INSERT INTO regulator_managed_information (
  id
, resource_id
, responsible_team
, geographical_area
)
SELECT
  gen_random_uuid()
, f.resource_id
, f.responsible_team_type
, f.geographical_area
FROM fields f;

ALTER TABLE fields
  DROP COLUMN geographical_area,
  DROP COLUMN responsible_team_type;