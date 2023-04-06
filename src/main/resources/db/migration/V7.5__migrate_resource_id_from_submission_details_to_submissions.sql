INSERT INTO submissions(id, resource_id)
SELECT gen_random_uuid(), resource_id
FROM submission_details
WHERE submission_details.submission_id IS NULL;

UPDATE submission_details
SET submission_id = submissions.id
FROM submissions
WHERE submissions.resource_id = submission_details.resource_id AND submission_details.submission_id IS NULL;