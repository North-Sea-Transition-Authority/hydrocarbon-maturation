ALTER TABLE discovery_well_details
RENAME COLUMN latitude_degrees TO top_reservoir_latitude_degrees;

ALTER TABLE discovery_well_details
RENAME COLUMN latitude_minutes TO top_reservoir_latitude_minutes;

ALTER TABLE discovery_well_details
RENAME COLUMN latitude_seconds TO top_reservoir_latitude_seconds;

ALTER TABLE discovery_well_details
RENAME COLUMN longitude_degrees TO top_reservoir_longitude_degrees;

ALTER TABLE discovery_well_details
RENAME COLUMN longitude_minutes TO top_reservoir_longitude_minutes;

ALTER TABLE discovery_well_details
RENAME COLUMN longitude_seconds TO top_reservoir_longitude_seconds;

ALTER TABLE discovery_well_details
RENAME COLUMN longitude_hemisphere TO top_reservoir_longitude_hemisphere;

ALTER TABLE discovery_well_details
RENAME COLUMN datum TO top_reservoir_datum;

ALTER TABLE dry_well_details
RENAME COLUMN latitude_degrees TO top_reservoir_latitude_degrees;

ALTER TABLE dry_well_details
RENAME COLUMN latitude_minutes TO top_reservoir_latitude_minutes;

ALTER TABLE dry_well_details
RENAME COLUMN latitude_seconds TO top_reservoir_latitude_seconds;

ALTER TABLE dry_well_details
RENAME COLUMN longitude_degrees TO top_reservoir_longitude_degrees;

ALTER TABLE dry_well_details
RENAME COLUMN longitude_minutes TO top_reservoir_longitude_minutes;

ALTER TABLE dry_well_details
RENAME COLUMN longitude_seconds TO top_reservoir_longitude_seconds;

ALTER TABLE dry_well_details
RENAME COLUMN longitude_hemisphere TO top_reservoir_longitude_hemisphere;

ALTER TABLE dry_well_details
RENAME COLUMN datum TO top_reservoir_datum;