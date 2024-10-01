CREATE TABLE post_field_discovery_links (
    id UUID PRIMARY KEY,
    field_resource_id UUID NOT NULL,
    discovery_resource_id UUID NOT NULL,
    UNIQUE(field_resource_id, discovery_resource_id),
    CONSTRAINT post_field_discovery_links_fk1 FOREIGN KEY (field_resource_id) REFERENCES resources (id),
    CONSTRAINT post_field_discovery_links_fk2 FOREIGN KEY (discovery_resource_id) REFERENCES resources (id)
);