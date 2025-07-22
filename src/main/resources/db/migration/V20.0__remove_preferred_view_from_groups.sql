ALTER TABLE related_resources
DROP COLUMN IF EXISTS preferred_view;

ALTER TABLE resource_group_links
DROP COLUMN IF EXISTS preferred_view;

ALTER TABLE resource_group_links_aud
DROP COLUMN IF EXISTS preferred_view;