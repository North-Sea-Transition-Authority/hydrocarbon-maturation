ALTER TABLE related_prospects RENAME TO related_resources;
ALTER TABLE related_resources
    RENAME COLUMN related_to_another_prospect TO related_to_another_resource;