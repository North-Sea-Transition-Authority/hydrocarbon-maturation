ALTER TABLE dry_well_details
ADD COLUMN source_failure_category TEXT,
ADD COLUMN reservoir_failure_category TEXT,
ADD COLUMN seal_failure_category TEXT,
ADD COLUMN trap_failure_category TEXT,
ADD COLUMN migration_failure_category TEXT;