CREATE TABLE discovery_well_details (
    id UUID PRIMARY KEY,
    submission_detail_id UUID NOT NULL,
    dry_well_id TEXT,
    dry_well_name TEXT,
    latitude_degrees INTEGER,
    latitude_minutes INTEGER,
    latitude_seconds DECIMAL,
    longitude_degrees INTEGER,
    longitude_minutes INTEGER,
    longitude_seconds DECIMAL,
    longitude_hemisphere TEXT,
    datum TEXT,
    total_depth_formation TEXT,
    total_depth_formation_comments TEXT,
    data_acquisition_programme TEXT,
    geophysical_update_commentary TEXT,
    hydrocarbons_discovered TEXT,
    discovery_status TEXT,
    chance_of_commerciality_comments TEXT,
    total_well_cost DECIMAL,
    success_related_costs DECIMAL,
    CONSTRAINT discovery_well_details_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details (id)
);

CREATE INDEX discovery_well_details_idx1 on discovery_well_details(submission_detail_id);