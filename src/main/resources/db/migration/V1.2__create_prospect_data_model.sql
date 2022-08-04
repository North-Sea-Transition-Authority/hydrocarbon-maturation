CREATE TABLE resources (
    id UUID PRIMARY KEY,
    type TEXT NOT NULL,
    created_timestamp TIMESTAMP NOT NULL
);

CREATE TABLE submission_details (
    id UUID PRIMARY KEY,
    resource_id UUID NOT NULL,
    created_timestamp TIMESTAMP NOT NULL,
    created_by_wua_id INT NOT NULL,
    status TEXT NOT NULL,
    submission_type TEXT NOT NULL,
    version_number INT,
    CONSTRAINT submission_details_fk FOREIGN KEY (resource_id) REFERENCES resources (id)
);

CREATE INDEX submission_details_idx1 ON submission_details(resource_id);