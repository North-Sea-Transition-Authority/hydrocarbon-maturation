CREATE TABLE resource_links (
    id UUID PRIMARY KEY,
    prospect_resource_id UUID NOT NULL,
    discovery_resource_id UUID NOT NULL,
    UNIQUE(prospect_resource_id, discovery_resource_id),
    CONSTRAINT resource_links_fk1 FOREIGN KEY (prospect_resource_id) REFERENCES resources (id),
    CONSTRAINT resource_links_fk2 FOREIGN KEY (discovery_resource_id) REFERENCES resources (id)
);

CREATE INDEX resource_links_idx1 ON resource_links(prospect_resource_id, discovery_resource_id);