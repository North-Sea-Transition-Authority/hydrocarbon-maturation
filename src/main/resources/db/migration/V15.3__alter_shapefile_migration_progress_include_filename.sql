ALTER TABLE shapefile_migration_progress
ADD COLUMN filename TEXT,
ADD COLUMN invalid_geometry_reason TEXT,
DROP COLUMN migration_record_index;