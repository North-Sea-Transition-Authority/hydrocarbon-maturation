DO $$
DECLARE

    prospect_rec RECORD;
    resource_rec RECORD;
    remi_res_id UUID;
    resource_audit_rev INTEGER;
    submission_rec RECORD;
    remi_submission_id UUID;
    reservoir_id UUID;
    reservoir_audit_rev INTEGER;
    remi_submission_detail_id UUID;

BEGIN

    -- Create prospect groupings based on the PaDI resource and the agreed organisation names
    FOR prospect_rec IN (
        SELECT
            prdh.resdat_id,
            prdh.resource_id,
            CASE WHEN prdh.stage_name != 'PARs' THEN pom.agreed_match END agreed_match
        FROM remi.padi_res_data_header prdh
        LEFT JOIN remi.padi_operator_mapping pom ON LOWER(pom.padi_name) = LOWER(prdh.company)
        WHERE prdh.opportunity_type != 'Discovery'
        GROUP BY
            prdh.resdat_id,
            prdh.resource_id,
            CASE WHEN prdh.stage_name != 'PARs' THEN pom.agreed_match END
    )
    LOOP

        INSERT INTO remi.padi_resource_groupings (
            id,
            remi_resource_id,
            legacy_resource_id,
            agreed_operator,
            legacy_res_data_id,
            resource_type
        )
        VALUES (
            gen_random_uuid(),
            NULL,
            prospect_rec.resource_id,
            prospect_rec.agreed_match,
            prospect_rec.resdat_id,
            'PROSPECT'
        );

    END LOOP;

    -- Create ReMI prospect resources and link them to the PaDI resource groupings. The groupings will be used to create the submissions later
    FOR resource_rec IN (
        SELECT prg.legacy_resource_id padi_res_id, prg.agreed_operator padi_operator
        FROM remi.padi_resource_groupings prg
        WHERE prg.resource_type = 'PROSPECT'
        GROUP BY prg.legacy_resource_id, prg.agreed_operator
    )
    LOOP

        INSERT INTO remi.resources (id, type, created_timestamp, active)
        VALUES (
           gen_random_uuid(),
           'PROSPECT',
           now(),
            false
       ) RETURNING id INTO remi_res_id;

        INSERT INTO remi.audit_revisions (
            updated_at,
            updated_by_user_id
        ) VALUES (
            now(),
                  1
        ) RETURNING rev INTO resource_audit_rev;

        INSERT INTO remi.resources_aud (
            rev,
            revtype,
            id,
            type,
            created_timestamp,
            active
        ) VALUES (
            resource_audit_rev,
            0,
            remi_res_id,
            'PROSPECT',
            now(),
            false
        );

        UPDATE remi.padi_resource_groupings
        SET remi_resource_id = remi_res_id
        WHERE legacy_resource_id = resource_rec.padi_res_id
        AND ((agreed_operator = resource_rec.padi_operator) OR (agreed_operator IS NULL AND resource_rec.padi_operator IS NULL))
        AND resource_type = 'PROSPECT';

    END LOOP;

--     -- Create submissions for each item in the PaDI resource data header table
    FOR submission_rec IN (
        -- Initial prospects and prospect updates
        SELECT
            prg2.remi_resource_id,
            TO_DATE(prdh2.stage_date, 'MM/DD/YYYY HH24:MI:SS') approved_date,
            CASE
                WHEN rank() over (PARTITION BY prg2.remi_resource_id ORDER BY TO_DATE(prdh2.stage_date, 'MM/DD/YYYY HH24:MI:SS'), prdh2.resdat_id) = 1 THEN 'CREATE_PROSPECT'
                ELSE 'PROSPECT_UPDATE'
            END submission_type,
            prg2.legacy_res_data_id legacy_data_id,
            prdh2.name_alternative
        FROM remi.padi_res_data_header prdh2
        JOIN remi.padi_resource_groupings prg2 ON prg2.legacy_res_data_id = prdh2.resdat_id
        JOIN remi.resources r ON r.id = prg2.remi_resource_id
        WHERE prdh2.opportunity_type != 'Dry Prospect (for post-well analysis)'
        AND prdh2.stage_name != 'Relinquishment'
        AND r.type = 'PROSPECT'
        AND prg2.resource_type = 'PROSPECT'
        UNION
        -- Dry well prospects
        SELECT
            prg2.remi_resource_id,
            TO_DATE(prdh2.stage_date, 'MM/DD/YYYY HH24:MI:SS') approved_date,
            'POST_DRILL_DRY_HOLE' submission_type,
            prg2.legacy_res_data_id,
            prdh2.name_alternative
        FROM remi.padi_res_data_header prdh2
        JOIN remi.padi_resource_groupings prg2 ON prg2.legacy_res_data_id = prdh2.resdat_id
        JOIN remi.resources r ON r.id = prg2.remi_resource_id
        WHERE prdh2.opportunity_type = 'Dry Prospect (for post-well analysis)'
        AND prdh2.stage_name != 'Relinquishment'
        AND r.type = 'PROSPECT'
        AND prg2.resource_type = 'PROSPECT'
        UNION
        -- Prospect relinquishments
        SELECT
            prg2.remi_resource_id,
            TO_DATE(prdh2.stage_date, 'MM/DD/YYYY HH24:MI:SS') approved_date,
            'RELINQUISHMENT_PROSPECT' submission_type,
            prg2.legacy_res_data_id,
            prdh2.name_alternative
        FROM remi.padi_res_data_header prdh2
        JOIN remi.padi_resource_groupings prg2 ON prg2.legacy_res_data_id = prdh2.resdat_id
        JOIN remi.resources r ON r.id = prg2.remi_resource_id
        WHERE prdh2.stage_name = 'Relinquishment'
        AND r.type = 'PROSPECT'
        AND prg2.resource_type = 'PROSPECT'
    )
    LOOP

        INSERT INTO remi.submissions (id, resource_id)
        VALUES(gen_random_uuid(), submission_rec.remi_resource_id)
        RETURNING id INTO remi_submission_id;

        INSERT INTO remi.submission_details (
            id,
            submission_id,
            created_timestamp,
            created_by_wua_id,
            status,
            submission_type,
            version_number,
            submitted_timestamp,
            accepted_timestamp,
            submitted_by_wua_id,
            accepted_by_wua_id,
            legacy_res_data_id
        ) VALUES (
            gen_random_uuid(),
            remi_submission_id,
            submission_rec.approved_date,
            1,
            'ACCEPTED',
            submission_rec.submission_type,
            1,
            submission_rec.approved_date,
            submission_rec.approved_date,
            1,
            1,
            submission_rec.legacy_data_id
        ) RETURNING id INTO remi_submission_detail_id;

        INSERT INTO remi.reservoirs (id, name)
        VALUES (
            gen_random_uuid(),
            submission_rec.name_alternative
        ) RETURNING id INTO reservoir_id;

        INSERT INTO remi.audit_revisions (
            updated_at,
            updated_by_user_id
        ) VALUES (
             now(),
             1
         ) RETURNING rev INTO reservoir_audit_rev;

        INSERT INTO remi.reservoirs_aud (
            rev,
            revtype,
            id,
            name
        ) VALUES (
            reservoir_audit_rev,
            0,
            reservoir_id,
            submission_rec.name_alternative
        );

        INSERT INTO remi.reservoir_submission_details (id, reservoir, submission_detail_id, status)
        VALUES (
            gen_random_uuid(),
            reservoir_id,
            remi_submission_detail_id,
            'EXTANT'
        );

    END LOOP;

END;
$$;