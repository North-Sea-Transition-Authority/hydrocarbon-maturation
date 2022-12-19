CREATE TABLE responsible_teams (
    id UUID PRIMARY KEY,
    resource_id UUID NOT NULL,
    responsible_team TEXT,
    CONSTRAINT responsible_teams_fk FOREIGN KEY (resource_id) REFERENCES resources (id)
);

CREATE INDEX responsible_teams_idx1 ON responsible_teams(resource_id);