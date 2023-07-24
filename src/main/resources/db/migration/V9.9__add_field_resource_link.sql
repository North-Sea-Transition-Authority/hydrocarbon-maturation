ALTER TABLE resource_links
ALTER COLUMN prospect_resource_id DROP NOT NULL;

ALTER TABLE resource_links
ADD COLUMN field_resource_id UUID,
ADD CONSTRAINT resource_links_fk3 FOREIGN KEY (field_resource_id) REFERENCES resources (id);

CREATE INDEX resource_links_idx2 ON resource_links(discovery_resource_id, field_resource_id);