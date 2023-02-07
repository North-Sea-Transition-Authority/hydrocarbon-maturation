DO $$
DECLARE

    prospect_rec RECORD;
    resource_rec RECORD;
    remi_res_id UUID;
    submission_rec RECORD;

BEGIN

    -- Create prospect groupings based on the PaDI resource and the agreed organisation names
    FOR prospect_rec IN (
        SELECT prdh.resdat_id, prdh.resource_id, pom.agreed_match
        FROM remi.padi_res_data_header prdh
        LEFT JOIN remi.padi_operator_mapping pom ON pom.padi_name = prdh.company -- does this need to take into account res header operators if data header company is empty?
        WHERE prdh.opportunity_type != 'Discovery'
    )
    LOOP

        INSERT INTO remi.padi_resource_groupings (id, remi_resource_id, legacy_resource_id, agreed_operator, legacy_res_data_id)
        VALUES (
          gen_random_uuid(), NULL, prospect_rec.resource_id, prospect_rec.agreed_match, prospect_rec.resdat_id
        );

    END LOOP;

    -- Create ReMI prospect resources and link them to the PaDI resource groupings. The groupings will be used to create the submissions later
    FOR resource_rec IN (
        SELECT prg.legacy_resource_id padi_res_id, prg.agreed_operator padi_operator
        FROM remi.padi_resource_groupings prg
        GROUP BY prg.legacy_resource_id, prg.agreed_operator
    )
    LOOP

        INSERT INTO remi.resources (id, type, created_timestamp)
        VALUES (
           gen_random_uuid(),
           'PROSPECT',
           now()
       ) RETURNING id INTO remi_res_id;

        UPDATE remi.padi_resource_groupings
        SET remi_resource_id = remi_res_id
        WHERE legacy_resource_id = resource_rec.padi_res_id
        AND ((agreed_operator = resource_rec.padi_operator) OR (agreed_operator IS NULL AND resource_rec.padi_operator IS NULL));

    END LOOP;

--     -- Create submissions for each item in the PaDI resource data header table
    FOR submission_rec IN (
        -- Initial prospects and prospect updates
        SELECT
            prg2.remi_resource_id,
            TO_DATE(prdh2.stage_date, 'DD/MM/YYYY HH24:MI:SS') approved_date, --is this date correct to use for approval date?
            CASE
                WHEN rank() over (PARTITION BY prg2.remi_resource_id ORDER BY prdh2.stage_date, prdh2.resdat_id) = 1 THEN 'CREATE_PROSPECT'
                ELSE 'PROSPECT_UPDATE'
            END submission_type
        FROM remi.padi_res_data_header prdh2
        JOIN remi.padi_resource_groupings prg2 ON prg2.legacy_res_data_id = prdh2.resdat_id
        JOIN remi.resources r ON r.id = prg2.remi_resource_id
        WHERE prdh2.opportunity_type != 'Dry Prospect (for post-well analysis)'
        AND prdh2.stage_name != 'Relinquishment'
        AND r.type = 'PROSPECT'
        UNION
        -- Dry well prospects
        SELECT
            prg2.remi_resource_id,
            TO_DATE(prdh2.stage_date, 'DD/MM/YYYY HH24:MI:SS') approved_date,
            'POST_DRILL_DRY_HOLE' submission_type
        FROM remi.padi_res_data_header prdh2
        JOIN remi.padi_resource_groupings prg2 ON prg2.legacy_res_data_id = prdh2.resdat_id
        JOIN remi.resources r ON r.id = prg2.remi_resource_id
        WHERE prdh2.opportunity_type = 'Dry Prospect (for post-well analysis)'
        AND prdh2.stage_name != 'Relinquishment'
        AND r.type = 'PROSPECT'
        UNION
        -- Prospect relinquishments
        SELECT
            prg2.remi_resource_id,
            TO_DATE(prdh2.stage_date, 'DD/MM/YYYY HH24:MI:SS') approved_date,
            'RELINQUISHMENT_PROSPECT' submission_type
        FROM remi.padi_res_data_header prdh2
        JOIN remi.padi_resource_groupings prg2 ON prg2.legacy_res_data_id = prdh2.resdat_id
        JOIN remi.resources r ON r.id = prg2.remi_resource_id
        WHERE prdh2.stage_name = 'Relinquishment'
        AND r.type = 'PROSPECT'
    )
    LOOP

        INSERT INTO remi.submission_details (
            id,
            resource_id,
            created_timestamp,
            created_by_wua_id,
            status,
            submission_type,
            version_number,
            submitted_timestamp,
            approved_timestamp,
            submitted_by_wua_id,
            approved_by_wua_id
        ) VALUES (
            gen_random_uuid(),
            submission_rec.remi_resource_id,
            submission_rec.approved_date,
            1,
            'APPROVED',
            submission_rec.submission_type,
            1,
            submission_rec.approved_date,
            submission_rec.approved_date,
            1,
            1
        );

    END LOOP;

END;
$$;