ALTER TABLE submission_details
ADD COLUMN legacy_res_data_id INTEGER;

ALTER TABLE resource_links
ADD COLUMN legacy_resource_id TEXT;