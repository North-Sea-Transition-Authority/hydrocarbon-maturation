ALTER TABLE resources
RENAME COLUMN archived TO active;

ALTER TABLE resources_aud
RENAME COLUMN archived TO active;