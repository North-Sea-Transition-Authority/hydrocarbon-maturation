DO $$
DECLARE


    multi_reservoir_rec RECORD;
    reservoir_id UUID;
    submission_rec RECORD;
    remi_submission_id UUID;
    remi_submission_detail_id UUID;
    remi_reservoir_rec RECORD;
    prospect_link_rec RECORD;
    discovery_link_rec RECORD;
    discovery_link_resource_id UUID;
    common_legacy_res_data_id INTEGER;

BEGIN

    -- Get all resources which are listed as having multiple reservoirs and create the reservoirs for those
    FOR multi_reservoir_rec IN (
        SELECT prg.remi_resource_id, prg.legacy_resource_id, prg.name_alternative, prg.legacy_res_data_id
        FROM remi.padi_resource_groupings prg
        WHERE prg.resource_type = 'DISCOVERY'
        AND prg.relinquishment IS FALSE
        AND prg.multi_reservoir IS TRUE
    )
    LOOP

        reservoir_id := NULL;

        INSERT INTO remi.reservoirs (id, name)
        VALUES (
            gen_random_uuid(),
            multi_reservoir_rec.name_alternative
        ) RETURNING id INTO reservoir_id;

        INSERT INTO remi.padi_reservoirs (
            id,
            remi_resource_id,
            legacy_resource_id,
            remi_reservoir_id,
            legacy_res_data_id
        ) VALUES (
            gen_random_uuid(),
            multi_reservoir_rec.remi_resource_id,
            multi_reservoir_rec.legacy_resource_id,
            reservoir_id,
            multi_reservoir_rec.legacy_res_data_id
        );

    END LOOP;

    -- Create submissions for each item in the PaDI resource data header table
    FOR submission_rec IN (
        -- Discoveries tied to prospects
        SELECT
            prg2.remi_resource_id,
            TO_DATE(MAX(prdh2.stage_date), 'DD/MM/YYYY HH24:MI:SS') approved_date,
            'CREATE_DISCOVERY_FROM_PROSPECT' submission_type
        FROM remi.padi_res_data_header prdh2
        JOIN remi.padi_resource_groupings prg2 ON prg2.legacy_res_data_id = prdh2.resdat_id
        JOIN remi.resources r ON r.id = prg2.remi_resource_id
        WHERE r.type = 'DISCOVERY'
        AND prg2.relinquishment IS FALSE
        AND prg2.resource_type = 'DISCOVERY'
        AND prg2.multi_reservoir IS TRUE
        AND EXISTS (
            SELECT 1
            FROM remi.padi_resource_groupings prg3
            WHERE prg2.legacy_resource_id = prg3.legacy_resource_id
            AND prg2.agreed_operator = prg3.agreed_operator
            AND prg3.resource_type = 'PROSPECT'
        )
        GROUP BY prg2.remi_resource_id
        UNION
        -- Discoveries not tied to prospects
        SELECT
            prg2.remi_resource_id,
            TO_DATE(MAX(prdh2.stage_date), 'DD/MM/YYYY HH24:MI:SS') approved_date,
            'CREATE_DISCOVERY' submission_type
        FROM remi.padi_res_data_header prdh2
        JOIN remi.padi_resource_groupings prg2 ON prg2.legacy_res_data_id = prdh2.resdat_id
        JOIN remi.resources r ON r.id = prg2.remi_resource_id
        WHERE r.type = 'DISCOVERY'
        AND prg2.relinquishment IS FALSE
        AND prg2.resource_type = 'DISCOVERY'
        AND prg2.multi_reservoir IS TRUE
        AND NOT EXISTS (
            SELECT 1
            FROM remi.padi_resource_groupings prg3
            WHERE prg2.legacy_resource_id = prg3.legacy_resource_id
            AND prg2.agreed_operator = prg3.agreed_operator
            AND prg3.resource_type = 'PROSPECT'
        )
        GROUP BY prg2.remi_resource_id
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
            accepted_by_wua_id
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
             1
         ) RETURNING id INTO remi_submission_detail_id;

        -- Get legacy res data id to draw the common (submission level) data from
        -- First attempt to get it based on the maximum oil equivalent recoverable P50 value
        -- MAX is used as there may be multiple with the same highest P50
        SELECT MAX(prdvo.resdat_id)
        INTO common_legacy_res_data_id
        FROM remi.padi_res_data_vol_outputs prdvo
        JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = prdvo.resdat_id
        JOIN remi.padi_resource_groupings prg ON prg.legacy_res_data_id = prdh.resdat_id
        WHERE prg.remi_resource_id = submission_rec.remi_resource_id
        AND prdvo.oilequiv_recoverable_p50 = (
            SELECT MAX(prdvo2.oilequiv_recoverable_p50)
            FROM remi.padi_res_data_vol_outputs prdvo2
            JOIN remi.padi_res_data_header prdh2 ON prdh2.resdat_id = prdvo2.resdat_id
            JOIN remi.padi_resource_groupings prg2 ON prg2.legacy_res_data_id = prdh2.resdat_id
            WHERE prg2.remi_resource_id = prg.remi_resource_id
        );

        -- If there is no P50 value attempt to get it based on the maximum oil equivalent mean value
        IF common_legacy_res_data_id IS NULL THEN

            SELECT MAX(prdvo.resdat_id)
            INTO common_legacy_res_data_id
            FROM remi.padi_res_data_vol_outputs prdvo
            JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = prdvo.resdat_id
            JOIN remi.padi_resource_groupings prg ON prg.legacy_res_data_id = prdh.resdat_id
            WHERE prg.remi_resource_id = submission_rec.remi_resource_id
            AND prdvo.oilequiv_recoverable_mean = (
                SELECT MAX(prdvo2.oilequiv_recoverable_mean)
                FROM remi.padi_res_data_vol_outputs prdvo2
                JOIN remi.padi_res_data_header prdh2 ON prdh2.resdat_id = prdvo2.resdat_id
                JOIN remi.padi_resource_groupings prg2 ON prg2.legacy_res_data_id = prdh2.resdat_id
                WHERE prg2.remi_resource_id = prg.remi_resource_id
            );

        END IF;

        -- If there is no P50 or mean value then simply get the latest res data id for the resource
        IF common_legacy_res_data_id IS NULL THEN

            SELECT MAX(prg.legacy_res_data_id)
            INTO common_legacy_res_data_id
            FROM remi.padi_resource_groupings prg
            WHERE prg.remi_resource_id = submission_rec.remi_resource_id;

        END IF;

        -- Set the common legacy data id against the submission detail
        UPDATE remi.submission_details sd
        SET legacy_res_data_id = common_legacy_res_data_id
        WHERE sd.id = remi_submission_detail_id;

        -- Create the reservoirs for the submission detail
        FOR remi_reservoir_rec IN (
            SELECT pr.remi_reservoir_id
            FROM remi.padi_reservoirs pr
            WHERE pr.remi_resource_id = submission_rec.remi_resource_id
        )
        LOOP

            INSERT INTO remi.reservoir_submission_details (
                id,
                reservoir,
                submission_detail_id,
                status
            ) VALUES (
                gen_random_uuid(),
                remi_reservoir_rec.remi_reservoir_id,
                remi_submission_detail_id,
                'EXTANT'
            );

        END LOOP;

    END LOOP;

    FOR prospect_link_rec IN (
        SELECT prg.remi_resource_id, prg.legacy_resource_id, prg.agreed_operator
        FROM remi.padi_resource_groupings prg
        WHERE EXISTS (
            SELECT 1
            FROM remi.padi_resource_groupings prg2
            WHERE prg2.resource_type != 'PROSPECT'
            AND prg2.legacy_resource_id = prg.legacy_resource_id
            AND (
                (prg2.agreed_operator = prg.agreed_operator)
                OR
                (prg.agreed_operator IS NULL AND prg2.agreed_operator IS NULL)
            )
            AND prg2.multi_reservoir IS TRUE
        )
        AND prg.resource_type = 'PROSPECT'
        GROUP BY prg.remi_resource_id, prg.legacy_resource_id, prg.agreed_operator
    )
    LOOP

        -- temp insert into resources to satisfy FK constraint on resource links
        INSERT INTO remi.resources (
            id,
            type,
            created_timestamp
        ) VALUES (
            gen_random_uuid(),
            'DISCOVERY',
            now()
        ) RETURNING id INTO discovery_link_resource_id;

        INSERT INTO remi.padi_temp_resources (resource_id)
        VALUES (discovery_link_resource_id);

        INSERT INTO remi.resource_links (
            id,
            prospect_resource_id,
            discovery_resource_id,
            legacy_resource_id,
            legacy_agreed_operator
        ) VALUES (
            gen_random_uuid(),
            prospect_link_rec.remi_resource_id,
            discovery_link_resource_id, --temp set to resource above
            prospect_link_rec.legacy_resource_id,
            prospect_link_rec.agreed_operator
        );
    END LOOP;

    FOR discovery_link_rec IN (
        SELECT prg.remi_resource_id, prg.legacy_resource_id, prg.agreed_operator
        FROM remi.padi_resource_groupings prg
        WHERE EXISTS (
            SELECT 1
            FROM remi.padi_resource_groupings prg2
            WHERE prg2.resource_type != 'DISCOVERY'
            AND prg2.legacy_resource_id = prg.legacy_resource_id
            AND (
                (prg2.agreed_operator = prg.agreed_operator)
                OR
                (prg.agreed_operator IS NULL AND prg2.agreed_operator IS NULL)
            )
        )
        AND prg.resource_type = 'DISCOVERY'
        AND prg.multi_reservoir IS TRUE
        GROUP BY prg.remi_resource_id, prg.legacy_resource_id, prg.agreed_operator
    )
    LOOP
        UPDATE remi.resource_links
        SET discovery_resource_id = discovery_link_rec.remi_resource_id
        WHERE legacy_resource_id = discovery_link_rec.legacy_resource_id
        AND (
          (legacy_agreed_operator IS NULL AND discovery_link_rec.agreed_operator IS NULL)
          OR
          (legacy_agreed_operator = discovery_link_rec.agreed_operator)
        )
        AND discovery_resource_id IN (
            SELECT ptr.resource_id
            FROM remi.padi_temp_resources ptr
        );
    END LOOP;

    -- Delete temp resource used to link prospect and discoveries
    DELETE FROM remi.resources r
    WHERE r.id IN (
        SELECT ptr.resource_id
        FROM remi.padi_temp_resources ptr
    );

    DELETE FROM remi.padi_temp_resources ptr;

END;
$$;