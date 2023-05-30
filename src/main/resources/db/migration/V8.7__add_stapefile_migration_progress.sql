CREATE TABLE shapefile_migration_progress (
  id UUID PRIMARY KEY,
  migration_record_index INT,
  polygon_id TEXT NOT NULL,
  status TEXT NOT NULL
);