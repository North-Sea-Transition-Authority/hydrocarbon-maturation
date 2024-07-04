DO $$
    DECLARE

        reservoir_id UUID;
        submission_rec RECORD;
        remi_submission_id UUID;
        remi_submission_detail_id UUID;
        prospect_link_rec RECORD;
        discovery_link_rec RECORD;
        discovery_link_resource_id UUID;

    BEGIN

        -- Create submissions for each item in the PaDI resource data header table
        FOR submission_rec IN (
            -- Discoveries tied to prospects
            SELECT
                prg2.remi_resource_id,
                TO_DATE(prdh2.stage_date, 'DD/MM/YYYY HH24:MI:SS') approved_date,
                CASE
                    WHEN rank() over (PARTITION BY prg2.remi_resource_id ORDER BY TO_DATE(prdh2.stage_date, 'DD/MM/YYYY HH24:MI:SS'), prdh2.resdat_id) = 1 THEN 'CREATE_DISCOVERY_FROM_PROSPECT'
                    ELSE 'UPDATE_DISCOVERY'
                END submission_type,
                prdh2.resource_id legacy_res_id,
                prg2.legacy_res_data_id,
                prdh2.name_alternative
            FROM remi.padi_res_data_header prdh2
            JOIN remi.padi_resource_groupings prg2 ON prg2.legacy_res_data_id = prdh2.resdat_id
            JOIN remi.resources r ON r.id = prg2.remi_resource_id
            WHERE r.type = 'DISCOVERY'
            AND prg2.relinquishment IS FALSE
            AND prg2.resource_type = 'DISCOVERY'
            AND prg2.multi_reservoir IS FALSE
            AND prdh2.stage_name != 'PARs'
            AND EXISTS (
                SELECT 1
                FROM remi.padi_resource_groupings prg3
                WHERE prg2.legacy_resource_id = prg3.legacy_resource_id
                AND prg2.agreed_operator = prg3.agreed_operator
                AND prg3.resource_type = 'PROSPECT'
              )
            UNION
            -- Discoveries not tied to prospects
            SELECT
                prg2.remi_resource_id,
                TO_DATE(prdh2.stage_date, 'DD/MM/YYYY HH24:MI:SS') approved_date,
                CASE
                    WHEN rank() over (PARTITION BY prg2.remi_resource_id ORDER BY TO_DATE(prdh2.stage_date, 'DD/MM/YYYY HH24:MI:SS'), prdh2.resdat_id) = 1 THEN 'CREATE_DISCOVERY'
                    ELSE 'UPDATE_DISCOVERY'
                END submission_type,
                prdh2.resource_id,
                prg2.legacy_res_data_id,
                prdh2.name_alternative
            FROM remi.padi_res_data_header prdh2
            JOIN remi.padi_resource_groupings prg2 ON prg2.legacy_res_data_id = prdh2.resdat_id
            JOIN remi.resources r ON r.id = prg2.remi_resource_id
            WHERE r.type = 'DISCOVERY'
            AND prg2.relinquishment IS FALSE
            AND prg2.resource_type = 'DISCOVERY'
            AND prg2.multi_reservoir IS FALSE
            AND prdh2.stage_name != 'PARs'
            AND NOT EXISTS (
                SELECT 1
                FROM remi.padi_resource_groupings prg3
                WHERE prg2.legacy_resource_id = prg3.legacy_resource_id
                AND prg2.agreed_operator = prg3.agreed_operator
                AND prg3.resource_type = 'PROSPECT'
            )
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
                submission_rec.legacy_res_data_id
            ) RETURNING id INTO remi_submission_detail_id;

            INSERT INTO remi.reservoirs (id, name)
            VALUES (
               gen_random_uuid(),
               submission_rec.name_alternative
            ) RETURNING id INTO reservoir_id;

            INSERT INTO remi.padi_reservoirs (
                id,
                remi_resource_id,
                legacy_resource_id,
                remi_reservoir_id,
                legacy_res_data_id
            ) VALUES (
                 gen_random_uuid(),
                 submission_rec.remi_resource_id,
                 submission_rec.legacy_res_id,
                 reservoir_id,
                 submission_rec.legacy_res_data_id
             );

            INSERT INTO remi.reservoir_submission_details (id, reservoir, submission_detail_id, status)
            VALUES (
                gen_random_uuid(),
                reservoir_id,
                remi_submission_detail_id,
                'EXTANT'
            );

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
                AND prg2.multi_reservoir IS FALSE
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
            WHERE prg.multi_reservoir IS FALSE
            AND EXISTS (
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