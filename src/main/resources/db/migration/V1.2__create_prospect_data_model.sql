CREATE TABLE hydrocarbon_maturation.resources (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4 (),
    type TEXT NOT NULL,
    created_timestamp TIMESTAMP NOT NULL
);

CREATE TABLE hydrocarbon_maturation.submission_details (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4 (),
    resource_id UUID NOT NULL,
    created_timestamp TIMESTAMP NOT NULL,
    created_by_wua_id INT NOT NULL,
    status TEXT NOT NULL,
    submission_type TEXT NOT NULL,
    version_number INT,
    CONSTRAINT submission_details_fk FOREIGN KEY (resource_id) REFERENCES hydrocarbon_maturation.resources (id)
);

CREATE INDEX submission_details_idx1 ON hydrocarbon_maturation.submission_details(resource_id);