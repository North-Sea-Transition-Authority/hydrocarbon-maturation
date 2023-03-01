ALTER TABLE dry_well_details
    DROP COLUMN well_id,
    DROP COLUMN well_name,
    ADD COLUMN well_id INTEGER,
    ADD COLUMN well_reference TEXT;

ALTER TABLE discovery_well_details
    DROP COLUMN well_id,
    DROP COLUMN well_name,
    ADD COLUMN well_id INTEGER,
    ADD COLUMN well_reference TEXT;
