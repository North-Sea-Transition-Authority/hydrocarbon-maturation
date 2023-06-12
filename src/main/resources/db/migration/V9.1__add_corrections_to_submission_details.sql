ALTER TABLE submission_details
ADD COLUMN correction BOOLEAN,
ADD COLUMN correction_by_wua_id INTEGER,
ADD COLUMN correction_timestamp TIMESTAMP;