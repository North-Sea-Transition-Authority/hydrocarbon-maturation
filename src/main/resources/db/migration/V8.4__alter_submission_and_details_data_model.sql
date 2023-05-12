ALTER TABLE submissions
ADD COLUMN withdrawn_timestamp TIMESTAMP,
ADD COLUMN withdrawn_by_wua_id INTEGER,
ADD COLUMN withdrawal_reason TEXT,
ADD COLUMN withdrawn BOOLEAN;

UPDATE submissions
SET
  withdrawn_timestamp = submission_details.withdrawn_timestamp,
  withdrawn_by_wua_id = submission_details.withdrawn_by_wua_id,
  withdrawal_reason = submission_details.withdrawal_reason,
  withdrawn = TRUE
FROM submission_details
WHERE submission_details.submission_id = submissions.id AND submission_details.status = 'WITHDRAWN';

ALTER TABLE submission_details
DROP COLUMN withdrawn_timestamp,
DROP COLUMN withdrawn_by_wua_id,
DROP COLUMN withdrawal_reason;

UPDATE submission_details
SET status = 'SUBMITTED'
WHERE submission_details.status = 'WITHDRAWN';