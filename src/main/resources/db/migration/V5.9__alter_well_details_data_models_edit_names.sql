ALTER TABLE dry_well_details
    RENAME COLUMN dry_well_id TO well_id;
ALTER TABLE dry_well_details
    RENAME COLUMN dry_well_name TO well_name;

ALTER TABLE discovery_well_details
    RENAME COLUMN dry_well_id TO well_id;
ALTER TABLE discovery_well_details
    RENAME COLUMN dry_well_name TO well_name;