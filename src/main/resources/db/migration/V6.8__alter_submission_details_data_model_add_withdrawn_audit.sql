ALTER TABLE submission_details
    ADD COLUMN withdrawn_timestamp TIMESTAMP,
    ADD COLUMN withdrawn_by_wua_id INTEGER;