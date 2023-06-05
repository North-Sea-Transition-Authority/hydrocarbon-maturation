INSERT INTO remi.regulator_managed_information (
  id
, resource_id
, responsible_team
, geographical_area
)
SELECT
  gen_random_uuid()
, r.id
, 'EXPLORATION'
, NULL
FROM remi.resources r
WHERE r.id NOT IN (
  SELECT resource_id
  FROM remi.regulator_managed_information
);

UPDATE remi.regulator_managed_information
SET responsible_team = 'EXPLORATION'
WHERE responsible_team IS NULL;
