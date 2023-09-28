ALTER TABLE resources
ALTER COLUMN created_timestamp TYPE timestamptz;

ALTER TABLE submissions
ALTER COLUMN withdrawn_timestamp TYPE timestamptz;

ALTER TABLE submission_details
ALTER COLUMN created_timestamp TYPE timestamptz;

ALTER TABLE submission_details
ALTER COLUMN submitted_timestamp TYPE timestamptz;

ALTER TABLE submission_details
ALTER COLUMN approved_timestamp TYPE timestamptz;

ALTER TABLE submission_details
ALTER COLUMN correction_timestamp TYPE timestamptz;

ALTER TABLE submission_update
ALTER COLUMN requested_timestamp TYPE timestamptz;

ALTER TABLE submission_update
ALTER COLUMN resolved_timestamp TYPE timestamptz;