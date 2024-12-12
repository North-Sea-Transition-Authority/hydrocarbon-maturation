DO $$
    DECLARE

        discovery_group_rec RECORD;
        group_id UUID;
        single_reservoir_choice_rec RECORD;
        multi_reservoir_choice_rec RECORD;

    BEGIN

        -- Get all prospects which have linked prospect resources based on the legacy resource ID
        FOR discovery_group_rec IN (
            SELECT prg.legacy_resource_id, prg.remi_resource_id
            FROM remi.padi_resource_groupings prg
            JOIN remi.resources r ON r.id = prg.remi_resource_id
            JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = prg.legacy_res_data_id
            WHERE r.type = 'DISCOVERY'
            AND EXISTS (
                SELECT 1
                FROM remi.padi_resource_groupings prg2
                JOIN remi.resources r2 ON r2.id = prg2.remi_resource_id
                WHERE prg2.legacy_resource_id = prg.legacy_resource_id
                AND prg2.remi_resource_id != prg.remi_resource_id
                AND r2.type = 'DISCOVERY'
              )
            GROUP BY prg.legacy_resource_id, prg.remi_resource_id
            ORDER BY prg.legacy_resource_id, prg.remi_resource_id
        )
        LOOP

            -- Get the existing group ID if one exists, else create one
            SELECT rgl.resource_group_id
            INTO group_id
            FROM remi.resource_group_links rgl
            JOIN remi.resources r ON r.id = rgl.resource_id
            WHERE rgl.legacy_resource_id = discovery_group_rec.legacy_resource_id
            AND r.type = 'DISCOVERY';

            IF group_id IS NULL THEN
                group_id := gen_random_uuid();
            END IF;

            -- Create group links
            INSERT INTO remi.resource_group_links (
                id,
                resource_group_id,
                resource_id,
                preferred_view,
                legacy_resource_id
            ) VALUES (
                 gen_random_uuid(),
                 group_id,
                 discovery_group_rec.remi_resource_id,
                 NULL, -- temp NULL value to be determined later in script
                 discovery_group_rec.legacy_resource_id
            );

        END LOOP;

        -- Get all choice 1 data (i.e. the preferred view) and set the preferred view for those resources in the group
        FOR single_reservoir_choice_rec IN (
            SELECT rgl.id
            FROM remi.padi_resource_groupings prg
            JOIN remi.resource_group_links rgl ON rgl.resource_id = prg.remi_resource_id
            JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = prg.legacy_res_data_id
            WHERE TRIM(prdh.stage_choice) = '1'
            AND prg.resource_type = 'DISCOVERY'
            AND prdh.resdat_id NOT IN (
                SELECT pmrd.resdat_id
                FROM remi.padi_multi_reservoir_discoveries pmrd
            )
        )
        LOOP

            UPDATE remi.resource_group_links
            SET preferred_view = TRUE
            WHERE id = single_reservoir_choice_rec.id;

        END LOOP;

        FOR multi_reservoir_choice_rec IN (
            SELECT rgl.id, pmrd.resource_id, CASE WHEN pmrd.choice = '1' THEN TRUE ELSE FALSE END choice_view
            FROM remi.padi_resource_groupings prg
            JOIN remi.resource_group_links rgl ON rgl.resource_id = prg.remi_resource_id
            JOIN remi.padi_multi_reservoir_discoveries pmrd ON pmrd.resdat_id = prg.legacy_res_data_id
            WHERE prg.resource_type = 'DISCOVERY'
            GROUP BY rgl.id, pmrd.resource_id, CASE WHEN pmrd.choice = '1' THEN TRUE ELSE FALSE END
            ORDER BY pmrd.resource_id
        )
            LOOP

                UPDATE remi.resource_group_links
                SET preferred_view = TRUE
                WHERE id = single_reservoir_choice_rec.id;

            END LOOP;

        UPDATE remi.resource_group_links
        SET preferred_view = FALSE
        WHERE preferred_view IS NULL;

    END;
$$;