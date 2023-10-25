UPDATE submission_details
SET status = 'ACCEPTED'
WHERE status = 'APPROVED';

ALTER TABLE submission_details
    RENAME COLUMN approved_by_wua_id TO accepted_by_wua_id;

ALTER TABLE submission_details
    RENAME COLUMN approved_timestamp TO accepted_timestamp;