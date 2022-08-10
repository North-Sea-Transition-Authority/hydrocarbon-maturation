CREATE TABLE trap_properties (
    id UUID PRIMARY KEY,
    submission_detail_id UUID NOT NULL,
    trap_description TEXT,
    trap_type TEXT,
    other_trap_type TEXT,
    latitude_degrees INTEGER,
    latitude_minutes INTEGER,
    latitude_seconds DECIMAL,
    longitude_degrees INTEGER,
    longitude_minutes INTEGER,
    longitude_seconds DECIMAL,
    longitude_hemisphere TEXT,
    CONSTRAINT trap_properties_fk FOREIGN KEY (submission_detail_id) REFERENCES submission_details (id)
);

CREATE INDEX trap_properties_idx1 ON trap_properties(submission_detail_id);
