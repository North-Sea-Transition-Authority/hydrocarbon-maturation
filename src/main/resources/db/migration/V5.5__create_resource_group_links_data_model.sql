CREATE TABLE resource_group_links (
    id UUID PRIMARY KEY,
    resource_group_id UUID NOT NULL,
    resource_id UUID NOT NULL,
    preferred_view BOOLEAN,

    CONSTRAINT resource_group_links_fk1 FOREIGN KEY (resource_id) REFERENCES resources (id)
);

CREATE INDEX resource_group_links_idx1 ON resource_group_links(resource_id);
CREATE INDEX resource_group_links_idx2 ON resource_group_links(resource_group_id);