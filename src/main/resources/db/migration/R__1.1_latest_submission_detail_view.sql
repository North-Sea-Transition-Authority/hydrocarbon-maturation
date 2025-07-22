-- drop required so that column changes / removals can be made
-- postgres only allows create or replace view for additional columns
DROP VIEW IF EXISTS latest_submission_detail_view;

CREATE VIEW latest_submission_detail_view AS
WITH groupings AS (
    SELECT
        s.resource_id,
        r.type,
        sd.id submission_detail_id,
        r.active,
        CASE WHEN rgl.resource_group_id IS NOT NULL THEN TRUE
             WHEN r.type = 'FIELD' THEN NULL
             ELSE FALSE
        END in_resource_group, -- an additional helper flag to know if the resource is in a group
        ROW_NUMBER() OVER (
            PARTITION BY s.resource_id
            ORDER BY
                sd.accepted_timestamp DESC, -- order the submission by the latest accepted timestamp
                sd.correction_timestamp DESC NULLS LAST, -- also order by correction timestamp as the accepted timestamp may be the same across corrections
                sd.legacy_res_data_id DESC	-- also order by the PaDI data ID (the latest stage info provided for a resource) in the case that two accepted timestamps are the same
            ) ord
    FROM remi.resources r
    JOIN remi.submissions s ON s.resource_id = r.id
    JOIN remi.submission_details sd ON sd.submission_id = s.id
    LEFT JOIN remi.resource_group_links rgl ON rgl.resource_id = s.resource_id
    WHERE sd.status = 'ACCEPTED' -- only care about accepted submissions for reporting
    AND s.withdrawn IS NULL
)
SELECT
    g.resource_id, -- the overall resource id
    g.type,
    g.submission_detail_id latest_submission_detail_id, -- the latest submission detail id for the resource
    g.active, -- true/false
    g.in_resource_group -- true/false
FROM groupings g
WHERE g.ord = 1 -- we only care about the latest submission information for a resource