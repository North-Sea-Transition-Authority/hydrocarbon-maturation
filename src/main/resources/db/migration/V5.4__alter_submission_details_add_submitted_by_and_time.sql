ALTER TABLE submission_details
    ADD COLUMN submitted_timestamp TIMESTAMP,
    ADD COLUMN approved_timestamp TIMESTAMP,
    ADD COLUMN submitted_by_wua_id INTEGER,
    ADD COLUMN approved_by_wua_id INTEGER;