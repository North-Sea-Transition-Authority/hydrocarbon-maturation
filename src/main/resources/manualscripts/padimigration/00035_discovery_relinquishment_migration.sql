DO $$
    DECLARE

        relinquishment_rec RECORD;
        remi_res_id UUID;
        reservoir_id UUID;
        remi_submission_id UUID;
        remi_submission_detail_id UUID;
        prospect_link_rec RECORD;
        discovery_link_rec RECORD;
        discovery_link_resource_id UUID;

    BEGIN

        -- Create an individual resource, submission, and reservoir per relinquished discovery
        FOR relinquishment_rec IN (
            SELECT
                prdh.resdat_id,
                prdh.resource_id,
                pom.agreed_match,
                prdh.name_alternative,
                TO_DATE(prdh.stage_date, 'DD/MM/YYYY HH24:MI:SS') approved_date
            FROM remi.padi_res_data_header prdh
            LEFT JOIN remi.padi_operator_mapping pom ON LOWER(pom.padi_name) = LOWER(prdh.company)
            WHERE prdh.opportunity_type = 'Discovery'
            AND prdh.stage_name = 'Relinquishment'
            GROUP BY
                prdh.resdat_id,
                prdh.resource_id,
                pom.agreed_match,
                prdh.name_alternative,
                TO_DATE(prdh.stage_date, 'DD/MM/YYYY HH24:MI:SS')
        )
        LOOP

            INSERT INTO remi.padi_resource_groupings (
                id,
                remi_resource_id,
                legacy_resource_id,
                agreed_operator,
                legacy_res_data_id,
                name_alternative,
                resource_type,
                relinquishment,
                multi_reservoir
            ) VALUES (
                gen_random_uuid(),
                NULL, -- updated once we have a remi resource
                relinquishment_rec.resource_id,
                relinquishment_rec.agreed_match,
                relinquishment_rec.resdat_id,
                relinquishment_rec.name_alternative,
                'DISCOVERY',
                true,
                false
            );

            INSERT INTO remi.resources (id, type, created_timestamp)
            VALUES (
                gen_random_uuid(),
                'DISCOVERY',
                now()
            ) RETURNING id INTO remi_res_id;

            UPDATE remi.padi_resource_groupings prg
            SET remi_resource_id = remi_res_id
            WHERE prg.legacy_res_data_id = relinquishment_rec.resdat_id;

            INSERT INTO remi.undeveloped_discoveries(id, is_undeveloped_discovery, resource_id)
            VALUES (gen_random_uuid(), false, remi_res_id);

            INSERT INTO remi.submissions (id, resource_id)
            VALUES(gen_random_uuid(), remi_res_id)
            RETURNING id INTO remi_submission_id;

            INSERT INTO remi.reservoirs (id, name)
            VALUES (
                gen_random_uuid(),
                relinquishment_rec.name_alternative
            ) RETURNING id INTO reservoir_id;

            INSERT INTO remi.padi_reservoirs (
                id,
                remi_resource_id,
                legacy_resource_id,
                remi_reservoir_id,
                legacy_res_data_id
            ) VALUES (
                gen_random_uuid(),
                remi_res_id,
                relinquishment_rec.resource_id,
                reservoir_id,
                relinquishment_rec.resdat_id
            );

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
                relinquishment_rec.approved_date,
                1,
                'ACCEPTED',
                'RELINQUISHMENT_DISCOVERY',
                1,
                relinquishment_rec.approved_date,
                relinquishment_rec.approved_date,
                1,
                1,
                relinquishment_rec.resdat_id
            ) RETURNING id INTO remi_submission_detail_id;

            INSERT INTO remi.reservoir_submission_details (
                id,
                reservoir,
                submission_detail_id,
                status
            ) VALUES (
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
                AND prg2.relinquishment IS TRUE
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
            WHERE prg.relinquishment IS TRUE
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