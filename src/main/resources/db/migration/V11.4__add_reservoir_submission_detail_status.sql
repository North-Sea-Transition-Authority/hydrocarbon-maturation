ALTER TABLE reservoir_submission_details
ADD COLUMN status TEXT;

UPDATE reservoir_submission_details
SET status = 'EXTANT';