DO $$
    DECLARE

        discovery_rec RECORD;
        resource_rec RECORD;
        remi_res_id UUID;
        submission_rec RECORD;
        prospect_link_rec RECORD;
        discovery_link_rec RECORD;

    BEGIN

        -- Create discovery groupings based on the PaDI resource and the agreed organisation names
        FOR discovery_rec IN (
            SELECT prdh.resdat_id, prdh.resource_id, pom.agreed_match
            FROM remi.padi_res_data_header prdh
            LEFT JOIN remi.padi_operator_mapping pom ON pom.padi_name = prdh.company
            WHERE prdh.opportunity_type = 'Discovery'
        )
        LOOP

            INSERT INTO remi.padi_resource_groupings (
                id,
                remi_resource_id,
                legacy_resource_id,
                agreed_operator,
                legacy_res_data_id,
                resource_type
            ) VALUES (
                gen_random_uuid(),
                NULL,
                discovery_rec.resource_id,
                discovery_rec.agreed_match,
                discovery_rec.resdat_id,
                'DISCOVERY'
            );

        END LOOP;

        -- Create ReMI discovery resources and link them to the PaDI resource groupings. The groupings will be used to create the submissions later
        FOR resource_rec IN (
            SELECT prg.legacy_resource_id padi_res_id, prg.agreed_operator padi_operator
            FROM remi.padi_resource_groupings prg
            WHERE prg.resource_type = 'DISCOVERY'
            GROUP BY prg.legacy_resource_id, prg.agreed_operator
        )
        LOOP

            INSERT INTO remi.resources (id, type, created_timestamp)
            VALUES (
                gen_random_uuid(),
                'DISCOVERY',
                now()
            ) RETURNING id INTO remi_res_id;

            UPDATE remi.padi_resource_groupings
            SET remi_resource_id = remi_res_id
            WHERE legacy_resource_id = resource_rec.padi_res_id
            AND ((agreed_operator = resource_rec.padi_operator) OR (agreed_operator IS NULL AND resource_rec.padi_operator IS NULL))
            AND resource_type = 'DISCOVERY';

        END LOOP;

    -- Create submissions for each item in the PaDI resource data header table
        FOR submission_rec IN (
            -- Discoveries tied to prospects and updates to those
            SELECT
                prg2.remi_resource_id,
                TO_DATE(prdh2.stage_date, 'DD/MM/YYYY HH24:MI:SS') approved_date,
                CASE
                    WHEN rank() over (PARTITION BY prg2.remi_resource_id ORDER BY prdh2.stage_date, prdh2.resdat_id) = 1 THEN 'CREATE_DISCOVERY_FROM_PROSPECT'
                    ELSE 'UPDATE_DISCOVERY'
                    END submission_type,
                prg2.legacy_res_data_id legacy_data_id
            FROM remi.padi_res_data_header prdh2
            JOIN remi.padi_resource_groupings prg2 ON prg2.legacy_res_data_id = prdh2.resdat_id
            JOIN remi.resources r ON r.id = prg2.remi_resource_id
            WHERE prdh2.stage_name != 'Relinquishment'
            AND r.type = 'DISCOVERY'
            AND prg2.resource_type = 'DISCOVERY'
            AND EXISTS (
                SELECT 1
                FROM remi.padi_resource_groupings prg3
                WHERE prg2.legacy_resource_id = prg3.legacy_resource_id
                AND prg2.agreed_operator = prg3.agreed_operator
                AND prg3.resource_type = 'PROSPECT'
            )
            UNION
            -- Discoveries not tied to prospects and updates to those
            SELECT
                prg2.remi_resource_id,
                TO_DATE(prdh2.stage_date, 'DD/MM/YYYY HH24:MI:SS') approved_date,
                CASE
                    WHEN rank() over (PARTITION BY prg2.remi_resource_id ORDER BY prdh2.stage_date, prdh2.resdat_id) = 1 THEN 'CREATE_DISCOVERY'
                    ELSE 'UPDATE_DISCOVERY'
                    END submission_type,
                prg2.legacy_res_data_id legacy_data_id
            FROM remi.padi_res_data_header prdh2
            JOIN remi.padi_resource_groupings prg2 ON prg2.legacy_res_data_id = prdh2.resdat_id
            JOIN remi.resources r ON r.id = prg2.remi_resource_id
            WHERE prdh2.stage_name != 'Relinquishment'
            AND r.type = 'DISCOVERY'
            AND prg2.resource_type = 'DISCOVERY'
            AND NOT EXISTS (
                SELECT 1
                FROM remi.padi_resource_groupings prg3
                WHERE prg2.legacy_resource_id = prg3.legacy_resource_id
                AND prg2.agreed_operator = prg3.agreed_operator
                AND prg3.resource_type = 'PROSPECT'
            )
            UNION
            -- Discovery relinquishments
            SELECT
                prg2.remi_resource_id,
                TO_DATE(prdh2.stage_date, 'DD/MM/YYYY HH24:MI:SS') approved_date,
                'RELINQUISHMENT_DISCOVERY' submission_type,
                prg2.legacy_res_data_id
            FROM remi.padi_res_data_header prdh2
            JOIN remi.padi_resource_groupings prg2 ON prg2.legacy_res_data_id = prdh2.resdat_id
            JOIN remi.resources r ON r.id = prg2.remi_resource_id
            WHERE prdh2.stage_name = 'Relinquishment'
            AND r.type = 'DISCOVERY'
            AND prg2.resource_type = 'DISCOVERY'
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
                approved_by_wua_id,
                legacy_res_data_id
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
                 1,
                 submission_rec.legacy_data_id
             );

        END LOOP;

        FOR prospect_link_rec IN (
            SELECT prg.remi_resource_id, prg.legacy_resource_id, prg.agreed_operator
            FROM remi.padi_resource_groupings prg
            WHERE EXISTS (
              SELECT 1
              FROM remi.padi_resource_groupings prg2
              WHERE prg2.resource_type != prg.resource_type
              AND prg2.legacy_resource_id = prg.legacy_resource_id
              AND (
                    (prg2.agreed_operator = prg.agreed_operator)
                    OR
                    (prg.agreed_operator IS NULL AND prg2.agreed_operator IS NULL)
                )
            )
            AND prg.resource_type = 'PROSPECT'
            GROUP BY prg.remi_resource_id, prg.legacy_resource_id, prg.agreed_operator
        )
        LOOP
            INSERT INTO remi.resource_links (
                id,
                prospect_resource_id,
                discovery_resource_id,
                legacy_resource_id
            ) VALUES (
                gen_random_uuid(),
                prospect_link_rec.remi_resource_id,
                prospect_link_rec.remi_resource_id, --temp set an ID as it is a not null column
                prospect_link_rec.legacy_resource_id
            );
        END LOOP;

        FOR discovery_link_rec IN (
            SELECT prg.remi_resource_id, prg.legacy_resource_id, prg.agreed_operator
            FROM remi.padi_resource_groupings prg
            WHERE EXISTS (
                SELECT 1
                FROM remi.padi_resource_groupings prg2
                WHERE prg2.resource_type != prg.resource_type
                AND prg2.legacy_resource_id = prg.legacy_resource_id
                AND (
                    (prg2.agreed_operator = prg.agreed_operator)
                    OR
                    (prg.agreed_operator IS NULL AND prg2.agreed_operator IS NULL)
                )
            )
            AND prg.resource_type = 'DISCOVERY'
            GROUP BY prg.remi_resource_id, prg.legacy_resource_id, prg.agreed_operator
        )
        LOOP
            UPDATE remi.resource_links
            SET discovery_resource_id = discovery_link_rec.remi_resource_id
            WHERE legacy_resource_id = discovery_link_rec.legacy_resource_id;
        END LOOP;

    END;
$$;